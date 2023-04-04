# Icon Tool Guide
`icontool.py` is a simple tool which can be used to automatically add your new icons and sort the link in alphabtical order.

# Adding an icon
```python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name"```

# Adding an icon (with generated message)
```python3 icontool.py -s /path/to/icon.svg -c com.app.app/com.app.app.appActivity -n "App Name" -m ```

It'll print a message with the icons added along with the package/activity name:
f.lux (`com.justgetflux.flux/com.justgetflux.flux.FluxActivity`)

# Removing a link

```python3 icontool.py -d com.app.app/com.app.app.appActivity```