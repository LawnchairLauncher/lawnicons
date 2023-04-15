# Lawnicons contributing guide
Welcome to the Lawnicons contributing guide! This file will tell you what you need to know to contribute to Lawnicons.

## Icon guidelines
See the below image for a summary of the icon guidelines. If you don't follow them, a team member will likely request changing the icons.

![](images/contributing-image-1.png)

Each icon must fit the 160x160px or 144x144px (depending on the shape) content area size. It must not be smaller nor bigger than the specified sizes.

The default stroke should be kept at 12px. For fine detail, a stroke of 6px can be used. The standard widths are 6px, 8px, 10px, 12px and 14px.

If an icon is too minimal or does not have enough surrounding details, 14px should be used. You can check whether to use it if the stroke width looks too thin alongside other icons. ([More information in the Figma document](https://www.figma.com/community/file/1227718471680779613))

In addition to the above, the icons must have an outlined (not filled) style and the colour must be black `#000000`. If the original icon has a filled style, change the icon to adhere to the guidelines as seen below.

![](images/contributing-image-2.png)

See also [this Figma repository](https://www.figma.com/community/file/1227718471680779613) made by Grabstertv.

## Adding an icon to Lawnicons
Here’s how to add an icon to&nbsp;Lawnicons:

### Prerequesties
* Your icon in the SVG format, adhering to the [above guidelines](#icon-guidelines). The filename should also be snake case (e.g.&nbsp;`files_by_google.svg`).
* The package and activity name of the app.

### Via `icontool.py`
Please check the [icon tool guide](/.github/icontool_guide.md) for more information.

### Via manual process
1. Add the ready SVG to the `svgs`&nbsp;directory.

1. Add a new line to `app/assets/appfilter.xml` (in alphabetical order, by the `name` attribute), and map the new icon to a package name and app's activity. For&nbsp;example:

    ```xml
      <item component="ComponentInfo{com.google.android.apps.nbu.files/com.google.android.apps.nbu.files.home.HomeActivity}" drawable="files_by_google" name="Files by Google"/> 
    ```

    A general template is as&nbsp;follows:

    ```xml
    <item component="ComponentInfo{[PACKAGE_NAME]/[APP_ACIVITY_NAME]}" drawable="[DRAWABLE NAME]" name="[APP NAME]"/> 
    ```

1. Done! You’re ready to open a pull request. Please set `develop` as the base&nbsp;branch.

## Finding the package and activity name of an app
### Using adb via computer/phone with OTG
1. Connect your Android device or emulator to the PC/Mac/Phone via otg and open the app whose details you want to inspect, i.e. Files by Google.
1. Open a new Command Prompt or Terminal window and input `adb devices`.
1. Finally, type the below-given command to get the information about the currently open application.

  **For Mac/Linux**:

  ```sh
  adb shell dumpsys window | grep 'mCurrentFocus'  
  ```

  **For Windows**:

  ```sh
  adb shell dumpsys window | find "mCurrentFocus"
  ```
  ![](images/contributing-image-3.png)

  The part before the `/` character in the above image, i.e `org.telegram.messenger`, is the package name `[PACKAGE_NAME]`, while the part after it, i.e `org.telegram.messenger.DefaultIcon`, is the activity name `[APP_ACIVITY_NAME]`.

### Using 3rd party apps
#### Icon Request app
1. Download [Icon Request App](https://github.com/Kaiserdragon2/IconRequest/releases). 
2. Launch the app and click ok, let's start.
3. Get the Activity details for each app.

![](images/contributing-image-4.png)
  
#### Icon Pusher App
1. Download [Icon Pusher App](https://play.google.com/store/apps/details?id=dev.southpaw.iconpusher&hl=en&gl=US).
2. Launch the app.
3. Select the icon(s) you want to upload or select all by pressing the square in the top right. Then press send.
4. View the Activity details for each app on the [Icon Pusher website](https://iconpusher.com/). Please make sure the `drawable="[DRAWABLE NAME]"` matches the icon SVG file name.
