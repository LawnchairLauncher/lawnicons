
# Release Update
[`release_helper.py`](/.github/release_helper.py) is a Python tool specified designed to for Lawnicons' CI automated build and publishing services. 

which can be used to examine the status, and publish an release simultaneously across different markets with or without requiring manual intervention.

## Summary
To use the `release_helper.py`, simply just run the command via Python to get it's output.

```java
Checking out version v2.12.0
🎉 There have been 376 new icons release!
🔴 Today is 26, which is not the target release day.
🚦 Not eligible for release!
```

To add new app store or market to the `release_update.yml`, 

```yml
publish-(market name):
  runs-on: ubuntu-latest
  needs: build-release-apk
  if: needs.release-validation.outputs.greenlight == 'true'
  steps:
    - name: Download artifact
      uses: actions/download-artifact@v4
      with:
        name: Release APK
        path: artifacts/release-apk
    - name: Publish to Acme Market
      uses: wild-e-coyote/acme-market
      with:
        ...
```

```xml
<?xml version="1.0" encoding="UTF-8"?>

<resources>
  <!-- Dynamic Calendars -->
  <calendar component="ComponentInfo{com.android.calendar/com.android.calendar.AllInOneActivity}" prefix="themed_icon_calendar_" name="Calendar" />
  <!-- Static Calendars -->
  <item drawableIgnore="true" component="ComponentInfo{com.google.android.calendar/com.android.calendar.AllInOneActivity}" drawable="themed_icon_calendar_31" name="Calendar" />
  <!-- Lawnicons -->
  <item component="ComponentInfo{com.arabiait.belal/com.arabiait.belal.MainActivity}" drawable="bilal" name=" بلال ~~ Bilal" />
</resource>
```
