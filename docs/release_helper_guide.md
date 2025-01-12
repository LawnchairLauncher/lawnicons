# Release Update

> [!NOTE]
> This documentation is only specific to Lawnicons' development.

[`release_helper.py`](/.github/release_helper.py) is a Python tool specified designed to for Lawnicons' CI automated build and publishing services. Use in conjuncture with [`release_update.yml](/.github/workflows/release_update.yml)

Which can be used to examine the status, and publish an release simultaneously across different markets with or without requiring manual intervention.

## Summary
To use the `release_helper.py`, simply just run the command via Python to get it's output.
```log
🎉 There have been 457 new icons since release!
🔗 291 icons have been linked to a new component since release!
🔴 Today is 3, which isn't the target release day 1.
🚦 Not eligible for release!
```

To add new app store or market to the `release_update.yml`, 
```yml
publish-(market name):
  runs-on: ubuntu-latest

  # Add this to skip the job from running when greenlight isn't given
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

        # Next version from the predictor
        tag_version: ${{ needs.release-validation.outputs.next_version }}

        ...
```

## Release Helper
Release Helper is an Python script designed to examine and provide insight to release readiness, by examining the time & date, and the number of additions & linked icons.

### Threshold
Threshold to release are as follow:
- Today is 1st day of the Month (see: `DAY_THRESHOLD`)
- More than 100 new icons (see: `NEW_THRESHOLD`)
- More than 20 linked icons (see: `LINK_THRESHOLD`)

### Version Predictor
Predict the next version of Lawnicons, if the script detected that it's launched by manual CI trigger, it will forcibly trigger a release.

To override, set `INCREMENT` in the enviroment as `PATCH`, `MINOR` or `MAJOR`.

> [!IMPORTANT]
> The script always assume the version is using [Semantic Versioning 2.0.0](https://semver.org/)

#### Version Component Bump Threshold

> [!WARNING]
> To achieve the MAJOR bump, you must override it by setting `INCREMENT` to `MAJOR`.

PATCH:
* Less than `NEW_THRESHOLD` and `LINK_THRESHOLD`

MINOR:
* Equal or more than `NEW_THRESHOLD` and `LINK_THRESHOLD`

### Get icons

#### SVG-Processor / Appfilter Method
Support: `Total Icons Check` | `Total Linked Icons Check` | `New Icons Check` | `Linked Icons Check`

> [!IMPORTANT]
> This is the format of `appfilter.xml`
> ```xml
> <resources>
>   <!-- Dynamic Calendars -->
>   <calendar component="(component)" prefix="(calendar_date_prefix)" name="(native) ~~ (transliteration)" />
>   <!-- Static Calendars -->
>   <item drawableIgnore="true" component="(component)" drawable="(svg drawable)" name="(native) ~~ (transliteration)" />
>   <!-- Lawnicons -->
>   <item component="(component)" drawable="(svg drawable)" name="(native) ~~ (transliteration)" />
> </resource>
> ```
> The script completely ignores the `<calendar>` element from the calculation so the main of focus is going to be all of `<item>` with `component` and `drawable`.

The script runs the svg-processor to compare the `appfilter.xml` with `appfilter_diff.xml` to get new icons/links.

### Configuration
Set any of the variable as enviroment and you'll be good to go:

- `RELEASE_LINK_THRESHOLD` (default: 20) - Amount of link icons required to greenlight.
- `RELEASE_NEW_THRESHOLD` (default: 100) - Amount of new icons required to greenlight.
- `RELEASE_DAY_THRESHOLD` (default: 1) - Number of day required to greenlight.

- `REPOSITORY` (default: `.`) - Git repository's directory.
- `SVG_PATH` (default: `REPOSITORY` + `svgs`) - svgs' folder directory.
- `APPFILTER_PATH` (default: `REPOSITORY` + `app` + `assets` + `appfilter.xml`) - Appfilter.xml's file.

- `INCREMENT` (default: `default` | `auto`) - Override version predictor's decision.
