# Icon Tool Guide
`icontool.py` is a simple tool which can be used to automatically add your new icons and sort the link in alphabtical order.

# Adding an icon
```c
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

# Adding an icon (with generated message)
```c
python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name" -m
```

It'll print a message with the icons added along with the package/activity name:
App Name (`com.app.app/com.app.app.appActivity`)

# Add link to an existing icon
```c
python3 icontool.py -l icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"
```

**Note: The path to the icon is not required as it should already exist in the `svgs` folder**

# Removing a link

```c
python3 icontool.py -d com.app.app/com.app.app.appActivity
```