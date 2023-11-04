const { stringifyPathData } = require("svgo/lib/path.js");

module.exports = {
    multipass: true,
    plugins: [
        {
            name: "preset-default",
            params: {
                overrides: {
                    removeViewBox: false,
                    cleanupIds: {
                        force: true,
                    },
                    inlineStyles: {
                        onlyMatchedOnce: false,
                    },
                },
            },
        },
        {
            name: "convertStyleToAttrs",
        },
        {
            name: "removeAttrs",
            params: {
                attrs: ["xml:space", "data-name", "class", "*^rx|ry^0"],
                elemSeparator: "^",
            },
        },
        {
            name: "fixZ",
            fn: (ast, params) => ({
                element: { enter: enterElement },
            }),
        },
    ],
};

const enterElement = (element) => {
    if (!element.pathJS) return;
    /** @type {any[]} */
    const path = element.pathJS;

    let start = [0, 0];
    let cursor = [0, 0];

    for (let i = 0; i < path.length; i += 1) {
        const pathItem = path[i];
        pathItem.base = [...cursor];
        let { command, args } = pathItem;

        // moveto (x y)
        if (command === "m") {
            cursor[0] += args[0];
            cursor[1] += args[1];
            start[0] = cursor[0];
            start[1] = cursor[1];
        }
        if (command === "M") {
            cursor[0] = args[0];
            cursor[1] = args[1];
            start[0] = cursor[0];
            start[1] = cursor[1];
        }

        // lineto (x y)
        if (command === "l") {
            cursor[0] += args[0];
            cursor[1] += args[1];
        }
        if (command === "L") {
            cursor[0] = args[0];
            cursor[1] = args[1];
        }
        if (command === "l" || command === "L") {
            if (start[0] == cursor[0] && start[1] == cursor[1])
                path[i] = { command: "z", args: [] };
        }

        // horizontal lineto (x)
        if (command === "h") {
            cursor[0] += args[0];
        }
        if (command === "H") {
            cursor[0] = args[0];
        }

        // vertical lineto (y)
        if (command === "v") {
            cursor[1] += args[0];
        }
        if (command === "V") {
            cursor[1] = args[0];
        }

        // curveto (x1 y1 x2 y2 x y)
        if (command === "c") {
            cursor[0] += args[4];
            cursor[1] += args[5];
        }
        if (command === "C") {
            cursor[0] = args[4];
            cursor[1] = args[5];
        }

        // smooth curveto (x2 y2 x y)
        if (command === "s") {
            cursor[0] += args[2];
            cursor[1] += args[3];
        }
        if (command === "S") {
            cursor[0] = args[2];
            cursor[1] = args[3];
        }

        // quadratic Bézier curveto (x1 y1 x y)
        if (command === "q") {
            cursor[0] += args[2];
            cursor[1] += args[3];
        }
        if (command === "Q") {
            cursor[0] = args[2];
            cursor[1] = args[3];
        }

        // smooth quadratic Bézier curveto (x y)
        if (command === "t") {
            cursor[0] += args[0];
            cursor[1] += args[1];
        }
        if (command === "T") {
            cursor[0] = args[0];
            cursor[1] = args[1];
        }

        // elliptical arc (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
        if (command === "a") {
            cursor[0] += args[5];
            cursor[1] += args[6];
        }
        if (command === "A") {
            cursor[0] = args[5];
            cursor[1] = args[6];
        }
        const back1 = path[i - 1];
        if (
            back1 &&
            (command === "a" || command === "A") &&
            (back1.command === "a" || back1.command === "A")
        ) {
            const { args: argsO } = back1;
            const start = back1.base;
            const midway = pathItem.base;
            const end = cursor;
            const diff1 = args[0] - argsO[0];
            const diff2 = args[1] - argsO[1];
            if (diff1 == 0 && diff2 == 0 || (args[0] > 1 && args[1] > 1 && diff1 > -.1 && diff1 < .1 && diff2 > -.1 && diff2 < .1)) {
                const oldACenter = getArcCenter(start, midway, argsO.slice(0, 5));
                const oldBCenter = getArcCenter(midway, end, args.slice(0, 5));
                const newCenter = getArcCenter(start, end, args.slice(0, 5));
                const diffA = Math.sqrt(
                    Math.pow(newCenter[0] - oldACenter[0], 2) +
                    Math.pow(newCenter[1] - oldACenter[1], 2)
                )
                const diffB = Math.sqrt(
                    Math.pow(newCenter[0] - oldBCenter[0], 2) +
                    Math.pow(newCenter[1] - oldBCenter[1], 2)
                )
                if (diffA + diffB < 0.2) {
                    path.splice(i, 1)
                    i -= 1;
                    path[i] = {
                        ...path[i],
                        base: start,
                        command: "A",
                        args: [args[0], args[1], args[2], args[3], args[4], cursor[0], cursor[1]],
                    }
                }
            }
        }

        // closepath
        if (command === "Z" || command === "z") {
            // reset cursor
            cursor[0] = start[0];
            cursor[1] = start[1];
        }
    }
    element.attributes.d = stringifyPathData({
        pathData: element.pathJS,
    });
};

const getArcCenter = (
    [px, py],
    [cx, cy],
    [rx, ry, xAxisRotation, largeArcFlag, sweepFlag]
) => {
    const sinphi = Math.sin((xAxisRotation * Math.PI * 2) / 360);
    const cosphi = Math.cos((xAxisRotation * Math.PI * 2) / 360);
    const pxp = (cosphi * (px - cx)) / 2 + (sinphi * (py - cy)) / 2;
    const pyp = (-sinphi * (px - cx)) / 2 + (cosphi * (py - cy)) / 2;
    const rxsq = Math.pow(rx, 2);
    const rysq = Math.pow(ry, 2);
    const pxpsq = Math.pow(pxp, 2);
    const pypsq = Math.pow(pyp, 2);

    let rad = rxsq * rysq - rxsq * pypsq - rysq * pxpsq;

    if (rad < 0) {
        rad = 0;
    }

    rad /= rxsq * pypsq + rysq * pxpsq;
    rad = Math.sqrt(rad) * (largeArcFlag === sweepFlag ? -1 : 1);

    const centerxp = ((rad * rx) / ry) * pyp;
    const centeryp = ((rad * -ry) / rx) * pxp;

    return [
        cosphi * centerxp - sinphi * centeryp + (px + cx) / 2,
        sinphi * centerxp + cosphi * centeryp + (py + cy) / 2,
    ];
};
