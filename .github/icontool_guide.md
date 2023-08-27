# Icon Tool Guide
[`icontool.py`](/icontool.py) is a simple python tool which can be used to automatically add/remove icons and sort `appfilter.xml` links in alphabetical order.

For Mac/Linux users, you can simply type `./icontool` to run the program. Otherwise, you must run `python3 ./icontool.py`.

## Summary of usage
```console
./icontool [-m] [-h]
           add    (a)    svg component name
           link   (l)    svg component name
           remove (r, d) component [-d]
           sort   (s)
           find   (f)    {duplicates, unused}
```

## General syntax
Adding the flag `-m` will generate a list item (depending on the subcommand) that looks like this:
```
* App Name (component info)
```

Note that you should add it *before* the other parameters:

:x: `python3 icontool.py add ... -m`<br/>
:white_check_mark: `python3 icontool.py -m add ...`

## Adding icons
```console
python3 icontool.py add /path/to/icon com.app.app/com.app.app.appActivity "App Name"
```

The `.svg` extension for `icon.svg` is optional, since the program automatically adds them.

## Linking icons
```console
python3 icontool.py link icon com.app.app/com.app.app.appActivity "App Name"
```

Note that `icon.svg` should be an SVG file located in the `svgs/` directory. The `.svg` extension for `icon.svg` is optional as well.

## Removing icons
### Normal usage
```console
python3 icontool.py remove com.app.app/com.app.app.appActivity
```

### Removing components with the same package name
```console
python3 icontool.py remove com.app.app
```

### Removing a component and its SVG file
Simply pass the `-d` or `--delete` flag:

```console
python3 icontool.py remove com.app.app -d
```

Note that the SVG file's name is based on the `drawable` attribute of the first `<item>` element.

## Utilities
Some common utilities are described below.

### Sorting appfilter.xml
```console
python3 ./icontool.py sort
```

This will sort the `appfilter.xml` file via the `name` attribute.

### Finding duplicate entries in appfilter.xml
```console
python3 ./icontool.py find duplicates
```

### Finding unused icons
```console
python3 ./icontool.py find unused
```
