# Icon Tool Guide
[`icontool.py`](/icontool.py) is a simple python tool which can be used to automatically add/remove icons and sort `appfilter.xml` links in alphabetical order. You can see how some of icontool.py commands work on YouTube.

For Mac/Linux users, you can simply type `./icontool` to run the program. Otherwise, you must run `python3 ./icontool.py`.

[View on YouTube](https://youtu.be/EAvYelOK5Nw?t=266)

## Summary of usage
```console
./icontool [-h]
           add    (a)    svg component name
           link   (l)    svg component name
           remove (r, d) component [-d]
           sort   (s)
           find   (f)    {duplicates, unused}
```

## Adding icons
```console
python3 ./icontool.py add /path/to/ICON_NAME APP_COMPONENT "APP_NAME"
```

The `.svg` extension for `icon.svg` is optional, since the program automatically adds them.

## Linking icons
```console
python3 ./icontool.py link APP_COMPONENT "APP_NAME"
```

Note that `icon.svg` should be an SVG file located in the `svgs/` directory. The `.svg` extension for `icon.svg` is optional as well.

## Removing icons
### Normal usage
```console
python3 ./icontool.py remove APP_COMPONENT
```

### Removing components with the same package name
```console
python3 ./icontool.py remove PACKAGE
```

### Removing a component and its SVG file
Simply pass the `-d` or `--delete` flag:

```console
python3 ./icontool.py remove PACKAGE -d
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
