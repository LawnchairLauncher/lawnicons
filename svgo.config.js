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
                attrs: ["xml:space", "data-name"],
                elemSeparator: "^",
            },
        },
    ],
};
