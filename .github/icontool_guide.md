# Icon Tool Guide
[icontool.py](/icontool.py) is a simple tool which can be used to automatically add/remove icons and sort `appfilter.xml` links in alphabetical order.

## Summary of usage
```sh
icontool [-m] [-h]
         add (a)    svg component name
         link (l)   svg component name
         remove (r) component [-d]
```

## General syntax
Adding the flag `-m` will generate a list item (depending on the subcommand) that looks like this:
```
* App Name (component info)
```

## Adding icons
```sh
python3 icontool.py add /path/to/icon com.app.app/com.app.app.appActivity "App Name"
```

The `.svg` extension for `icon.svg` is optional, since the program automatically adds them.

## Linking icons
```sh
python3 icontool.py link icon com.app.app/com.app.app.appActivity "App Name"
```

Note that `icon.svg` should be an SVG file located in the `svgs/` directory. The `.svg` extension for `icon.svg` is optional as well.

## Removing icons
### Normal usage
```sh
python3 icontool.py remove com.app.app/com.app.app.appActivity
```

### Removing components with the same package name
```sh
python3 icontool.py remove com.app.app
```

### Removing a component and its SVG file
Simply pass the `-d` or `--delete` flag:

```sh
python3 icontool.py remove com.app.app -d
```

Note that the SVG file's name is based on the `drawable` attribute of the first `<item>` element.
