# Lawnicons reFilled contributing guide
Welcome to the Lawnicons reFilled contributing guide! This file will tell you  what you need to know to contribute to the project.

## Icon guidelines
See the below image for a summary of the icon guidelines. If you don't follow them, a team member will likely request changing the icons.

![](./contributing-image-1.png)

Each icon must fit the 20x20 or 18x18 (depending on the shape) content area size. It must not be smaller nor bigger than the specified sizes.

In addition to the above, the icons must have a filled (not outlined) style.

![](./contributing-image-2.png)

## Adding an icon
Here’s how to add an icon to&nbsp;Lawnicons reFilled:

1. Prepare your icon in the SVG format, adhering to the [above guidelines](#icon-guidelines). Use snake case for the filename (e.g.,&nbsp;`youtube_music.svg`).

1. Add the ready SVG to the `svgs`&nbsp;directory.


1. Add a new line to `app/assets/appfilter.xml` (in alphabetical order), and map the new icon to a package name and app's activity. For&nbsp;example:

    ```xml
      <item component="ComponentInfo{com.google.android.apps.nbu.files/com.google.android.apps.nbu.files.home.HomeActivity}" drawable="files_by_google" name="Files by Google"/>
    ```

    A general template is as&nbsp;follows:

    ```xml
    <item component="ComponentInfo{[PACKAGE_NAME]/[APP_ACIVITY_NAME]}" drawable="[DRAWABLE NAME]"/>
    ```
1. Done! You’re ready to open a pull request. Please set `develop` as the base&nbsp;branch.

## How to find the package and activity name of your app


### Using adb via computer/phone with OTG

  1. Connect your Android device or emulator to the PC/Mac/Phone via otg and open the app whose details you want to inspect, i.e. Files by Google.
  1. Open a new Command Prompt or Terminal window and input `adb devices`
  1. Finally, type the below-given command to get the information about the currently open application.

  **For Mac/Linux**:

  ```xml
  adb shell dumpsys window | grep -E 'mCurrentFocus'
  ```

  **For Windows**:

  ```xml
  adb shell dumpsys window | find "mCurrentFocus"
  ```
  ![](./contributing-image-5.png)

  Here, the part before the `/` character i.e `org.telegram.messenger` is the package name `[PACKAGE_NAME]` and the part after that i.e `org.telegram.messenger_.DefaultIcon` is the Activity name `[APP_ACIVITY_NAME]`.

### Using 3rd Party Apps.
  1. Download [Icon Request App](https://github.com/Kaiserdragon2/IconRequest/releases).
  1. Launch the app and click ok, let's start.
  1. Get the Activity details for each app.

  ![](./contributing-image-6.png)


