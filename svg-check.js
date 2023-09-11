const fs = require('fs');
const path = require('path');

const rules = [
    {
        name: "viewBox",
        rule: "viewBox=\"0 0 192 192\"",
        error: "Incorrect viewbox size",
        description: "Viewbox should be \`0 0 192 192\`",
        protip: null
    },
    {
        name: "fillValue",
        rule: "fill=\"none\"",
        error: "Incorrect fill value",
        description: "Fill should be set to \`none\` and be outlined",
        protip: "If your icon does not contain any \`<path>\` (only \`<circle>\` or \`<rect>\`), you can ignore this warning"
    },
    {
        name: "strokeColor",
        rule: [
            "stroke=\"black\"",
            "stroke=\"#000000\"",
            "stroke=\"#000\"",
            "stroke=\"rgb(0,0,0)\"",
            "stroke=\"rgb(0%,0%,0%)\""
        ],
        error: "Incorrect stroke value",
        description: "Stroke should be set to **black** *(\`black\`, \`#000000\`, \`#000\`, \`rgb(0,0,0)\`, or \`rgb(0%,0%,0%)\`)*",
        protip: null
    },
    {
        name: "strokeWidth",
        rule: "stroke-width=\"12\"",
        error: "Incorrect stroke-width value",
        description: "Standard stroke width should be \`12\`px",
        protip: "Other width can be used for finer details but 12 should be the main width"
    },
    {
        name: "strokeLinejoin",
        rule: "stroke-linejoin=\"round\"",
        error: "Incorrect stroke-linejoin value",
        description: "Stroke-linejoin should be set to \`round\`",
        protip: "If your icon does not contain any \`<path>\` (only \`<circle>\` or \`<rect>\`), you can ignore this warning"
    }
];

function validateSvg(file) {
    let hasError = false;
    let errors = [];

    try {
        const svg = fs.readFileSync(file, 'utf8');

        rules.forEach(rule => {
            rule.rule = Array.isArray(rule.rule)? rule.rule : [rule.rule];
            if(!rule.rule.some(r => svg.includes(r))) {
                rule.affectedFiles = rule.affectedFiles || [];
                rule.affectedFiles.push(file);
            }
        });
    } catch (err) {
        console.error(`File ${file} does not exist or is not readable`);
        return [];
    }
    console.log(`${file} tested`);
}

// files should be process.argv minus 0 and 1
const files = process.argv.slice(2);
console.log(`${files}`)

if (files.length === 0) {
    console.log('No file path provided');
    process.exit(0);
}

files.forEach(file => {
    const errors = validateSvg(file);
});

// get each rules with affected files
const rulesWithAffectedFiles = rules.filter(rule => rule.affectedFiles);
if (rulesWithAffectedFiles.length > 0) {
    const errorFile = path.join(process.cwd(), 'errors.md');
    // for each rule, write the rule name and the affected files
    try {
        fs.appendFileSync(errorFile, `# Thanks for contributing! Some of the svgs don't conform to the [Lawnicon guidelines](https://github.com/LawnchairLauncher/lawnicons/blob/develop/.github/CONTRIBUTING.md):\n`);
        rulesWithAffectedFiles.forEach(rule => {
            fs.appendFileSync(errorFile, `- ## ${rule.name}:\n`);
            fs.appendFileSync(errorFile, `\t${rule.description}.  \n`);
            fs.appendFileSync(errorFile, `\tAffected files:\n`);
            rule.affectedFiles.forEach(affectedFile => {
                fs.appendFileSync(errorFile, `\t- ${affectedFile}\n`);
            });
            if (rule.protip) fs.appendFileSync(errorFile, `\n\t*${rule.protip}.*\n`);
        });
        fs.appendFileSync(errorFile, `\n### Please fix these mistakes and update your pull request, thank you! See the [Contributing guide](https://github.com/LawnchairLauncher/lawnicons/blob/develop/.github/CONTRIBUTING.md) for more details.`);
    } catch (err) {
        console.log(`Error while writing errors to ${errorFile}`);
    }
} else {
    try {
        const errorFile = path.join(process.cwd(), 'errors.md');
        fs.appendFileSync(errorFile, `# Thanks for contributing! No mistakes have been found in your pull request. Kindly wait for a reviewer to validate your contribution.`);
    } catch (err) {
        console.log(`Error while writing errors to ${errorFile}`);
    }
}