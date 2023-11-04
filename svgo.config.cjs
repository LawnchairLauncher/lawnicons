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
    const path = element.pathJS;

    let start = [0, 0];
    let cursor = [0, 0];

    for (let i = 0; i < path.length; i += 1) {
        const pathItem = path[i];
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
