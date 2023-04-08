# Icon Tool Guide
[icontool.py](https://github.com/LawnchairLauncher/lawnicons/blob/develop/icontool.py) is a simple tool which can be used to automatically add/remove icons and sort `appfilter.xml` link in alphabtical order.

# Add icons/links

## Add an icon
```xml
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

## Add an icon (with generated message)
```xml
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name" -m
```

It'll print a message with the icons added along with the package/activity name:
App Name (`com.app.app/com.app.app.appActivity`)

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