# Icon Tool Guide
[icontool.py](/icontool.py) is a simple tool which can be used to automatically add/remove icons and sort `appfilter.xml` link in alphabtical order.

# Add icons/links

## Add an icon
```xml
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

## Add an icon (with generated message)
```xml
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name" -m
```

When adding, it'll print out the name of the app with the package and activity name.
```xml
App Name (`com.app.app/com.app.app.appActivity`)
```
When linking, it'll print out the name of the app, the package and activity name and the icon its been linked to.
```xml
App Name (linked to `com.app.app/com.app.app.appActivities` to `@drawable/icon`)
```

## Add a link to an existing icon
```xml
python3 icontool.py -l icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

**Note: The path to the icon is not required as it should already exist in the `svgs` folder**


# Removing icons/links

## Remove a link

```xml
python3 icontool.py -r com.app.app/com.app.app.appActivity
```

## Remove links with same package name

```xml
python3 icontool.py -r com.app.app
```

## Remove a link and SVG

```xml
python3 icontool.py -r com.app.app -d
```