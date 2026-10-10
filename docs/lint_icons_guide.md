# Lint Icons Guide
[`lint_icons.py`](/lint_icons.py) is a tool used to verify that the provided icons adhere to [the Lawnicons guidelines](../CONTRIBUTING.md).

For Mac/Linux users, you can simply type `./lint_icons.py` to run the program. Otherwise, you must run `python3 ./lint_icons.py`.

## Summary of usage
```console
./lint_icons.py [inputs] [-h]
                [--verbose]
                [--format {text,json}]
                [--output-file FILE]
                [--speed {fast,medium,slow}]
                [--workers N]
                [--exceptions FILE]
```

## Running the linter
### Checking specific files or directories
You can pass one or more files or directories as arguments. If a directory is provided, it will be searched recursively for SVG files.

```console
python3 ./lint_icons.py svgs/
python3 ./lint_icons.py svgs/icon1.svg svgs/icon2.svg
```

### Verbose mode
By default, only warnings and failures are shown. Use the `--verbose` flag to see all checks, including those that passed.

```console
python3 ./lint_icons.py svgs/ --verbose
```

## Formatting output
The linter supports `text` (default) and `json` output formats. You can also redirect the output to a file.

```console
python3 ./lint_icons.py svgs/ --format json --output-file results.json
```

## Check Complexity
The `--speed` flag controls the depth of analysis:
- `fast`: Only regex/string-based checks.
- `medium`: Includes XML tree parsing.
- `slow`: Includes full geometry analysis using `svgelements` (default).

```console
python3 ./lint_icons.py svgs/ --speed medium
```

## Managing Exceptions
If an icon fails a rule but is intentionally designed that way, you can add it to an exceptions file (default is `exceptions.json`). Icons listed here for a specific rule will be marked as `EXEMPT` instead of `FAIL`.

Example `exceptions.json`:
```json
{
  "C06": ["icon_name.svg"],
  "C10": ["another_icon.svg"]
}
```
