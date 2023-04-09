# Icon Tool Guide
[icontool.py](/icontool.py) is a simple tool which can be used to automatically add/remove icons and sort `appfilter.xml` link in alphabtical order.

# Add icons/links

## Add an icon
```xml
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

**Note**: `-m` can be added at the end of the command to generate a message.

## Add a link to an existing icon
```xml
python3 icontool.py -l icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

**Note:** The path to the icon is not required as it should already exist in the `svgs` folder.

# Removing icons/links

## Remove a link

```xml
python3 icontool.py -r com.app.app/com.app.app.appActivity
```

## Remove links with same package name

```xml
python3 icontool.py -r com.app.app
```

## Remove a link and its SVG

```xml
python3 icontool.py -r com.app.app -d
```