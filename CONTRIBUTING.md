# Lawnicons contributing guide

Welcome to the Lawnicons contributing guide!

In case of unclear wording, ask us in our Discord. If you find errors or want to suggest improvements in the guide itself, create an issue.

[Our Discord](https://discord.com/invite/3x8qNWxgGZ)

## Involvement

**Contributors**

The Lawnchair team is focused on development only. Our community makes icons and sometimes touches the code too. Anyone can become a contributor — it takes some learning, but it's doable.

[Our contributors](https://github.com/LawnchairLauncher/lawnicons/graphs/contributors) • [Lawnchair](https://github.com/LawnchairLauncher/lawnchair)

**Development**  

The main tasks are to maintain Lawnicons and interaction with launchers, fix bugs, add new features and automate processes. Please see our issues for more details.

**Icons**  

You can contribute icons, fulfill icon requests, add missing app IDs, refine existing icons, clean up dead apps, and remove duplicates. Mastering the Lawnicons guidelines in practice will also allow you to review icons.

[Icon requests dashboard](https://lawnicons-requests.vercel.app/)

## Contributing code

Code-related contributions are welcome. Significant changes to the UI should be discussed in our Discord. Generally, we want to keep things clean and simple.

Visit the Lawnicons developer wiki for developer information regarding Lawnicons.

[Lawnicons developer wiki](https://github.com/LawnchairLauncher/lawnicons/wiki)

## Contributing icons TL;DR

1. Create an icon (*.svg) following the Lawnicons design guidelines (for example, in Figma).
2. Find the app ID to link with your icon.
3. Fork the Lawnicons repository and make a local copy via GitHub Desktop.
4. Add the icon and the app ID to your local copy and push the changes.
5. Create a pull request and wait for review.

[Design guidelines](https://github.com/LawnchairLauncher/lawnicons/blob/develop/docs/images/common-issues-to-fix.png) • [Check app IDs on Dashboard](https://lawnicons-requests.vercel.app/) • [How to find app IDs](https://github.com/LawnchairLauncher/lawnicons/blob/develop/CONTRIBUTING.md#how-to-find-app-ids) • [Add icons to Lawnicons](https://github.com/LawnchairLauncher/lawnicons/blob/develop/CONTRIBUTING.md#adding-icons-and-missing-app-ids-to-lawnicons)

## Icon reviews

Reviews serve two goals: keeping icon quality high and filtering out rushed attempts that skip the guidelines. This saves time for everyone involved.

To review contributions, you should be able to create at least 20 random icons flawlessly in one go. That demonstrates solid knowledge of the design guidelines. Reviews often take longer than making an icon, so fixing a few incorrect ones is faster than explaining the issues.

### Review stages

1. Our review bot runs its checks and gives feedback. If the contributor doesn't fix the icon issues, the PR isn't worth pursuing.
2. If the bot finds no issues, a maintainer does a detailed review — up to 5 icons at a time, depending on how many issues are left.
3. The maintainer merges the PR once all issues are resolved, sometimes fixing them directly to speed things up.

PRs with long-ignored issues are closed automatically.

## Icon contribution tools

### Vector graphics editor

To create icons, you need a vector graphics editor, which allows you to save icons in SVG format. Mobile vector editors won't work. We recommend Figma because it has easier quality control. You can use Advanced SVG Export to save optimized SVGs in Figma.

[Figma](https://www.figma.com/) • [Advanced SVG Export](https://www.figma.com/community/plugin/782713260363070260) 

### GitHub Desktop

You can use it to create a local copy of your repository on GitHub and upload all the changes. Before getting into your repository, the changes must appear in your local copy.

[GitHub Desktop](https://github.com/apps/desktop)

### App ID search tool

You can use it to find app IDs. If you fulfill icon requests from our table, all the app IDs are there.

[How to find app IDs](#how-to-find-app-ids)

### Other tools

**File explorer**. It will help you copy icons to a local copy of your repository.

**Text editor**. It will help you to link icons and app IDs in `appfilter.xml`. This is how icon packs work.

**Terminal (command line)**. It will add convenience if you regularly contribute dozens of icons.

## How to find app IDs

An app ID is a record consisting of a package and an activity, separated by `/`. App IDs allow you to link icons and apps. 

Sample (Lawnicons)  
App ID: `app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity`  
Package: `app.lawnchair.lawnicons`  
Activity: `app.lawnchair.lawnicons.MainActivity`  

**Lawnicons**  

This method is suitable if you are interested in installed apps that aren't supported in Lawnicons.  

Install and open Lawnicons. → Tap `⋮` → Tap `Request icons` → Select icons → Tap `Copy App IDs` in the split button's menu and save it wherever it's convenient.

[Download Lawnicons](https://github.com/LawnchairLauncher/lawnicons#download)

**Icon Request**  

1. Install and open Icon Request.
2. Tap one of the options:
- UPDATE EXISTING — to copy app IDs.
- REQUEST NEW — to save icon images and app IDs. This option is better if you are creating icons.
3. Use the Icon Request toolbar to select apps.
4. Copy, save or share.

[Google Play](https://play.google.com/store/apps/details?id=de.kaiserdragon.iconrequest) • [GitHub](https://github.com/Kaiserdragon2/IconRequest/releases)

**Icon Pusher**  

1. Install and open Icon Pusher.
2. Select the icons you want to upload or select all by pressing the square in the top right.
3. Submit the selected apps.
4. View your submission on the Icon Pusher website.

[Google Play](https://play.google.com/store/apps/details?id=dev.southpaw.iconpusher) • [Website](https://iconpusher.com/)

**Android Debug Bridge (adb)**  

1. Connect your Android device or emulator to your laptop/desktop PC that has `adb` installed.
2. Open the app whose details you want to inspect (e.g. Telegram).
3. Open a new Command Prompt or Terminal window and input `adb devices`.
4. Finally, type the below-given command to get the information about the currently open app.

[How to install ADB](https://www.xda-developers.com/install-adb-windows-macos-linux/)

  Mac or Linux

  ```console
  adb shell dumpsys window | grep 'mCurrentFocus'
  ```

  Windows

  ```console
  adb shell dumpsys window | findstr "mCurrentFocus"
  ```
  ![](docs/images/contributing-image-3.png)

## Adding icons and missing app IDs to Lawnicons

You need to link SVGs and app IDs correctly, create a PR to our repository through your fork, and wait for it to be reviewed.

> [!TIP]
> [View on YouTube](https://youtu.be/UXic1zy-CiQ)

Tips
- Avoid name conflicts.
- Add missing app IDs to icons that are identical to the originals.
- Make sure your icons or missing app IDs haven't been added earlier: search the `appfilter.xml` and check PRs.

[Plan contributions on our dashboard](https://lawnicons-requests.vercel.app/) • [How to find app IDs](#how-to-find-app-ids) • [Icon contribution tools](#icon-contribution-tools)

### Manual process

Let's imagine that you have an icon in SVG format, an app name and an app ID.  

Icon: `lawnicons.svg`  
App name: `Lawnicons`  
App ID: `app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity`

1. Fork the Lawnicons repository.
2. Clone the fork via GitHub Desktop.
3. Open it with a file explorer. This is your local copy.
4. Сopy `lawnicons.svg` to the `svgs/` folder. Note the icon name.
5. Open `app/assets/appfilter.xml` and add a new line using the same template as the existing lines.

```
Do
<item component="ComponentInfo{app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity}" drawable="lawnicons" name="Lawnicons" />

Template
<item component="ComponentInfo{APP_ID}" drawable="ICON_NAME" name="APP_NAME" />
```

6. Save changes and push it to your fork via GitHub Desktop.
7. Open your fork in a web browser and create a PR: `Contribute → Open pull request`.
8. Make sure that the build went without errors and common issues are fixed, then await review.
9. We will merge your PR, fix the little things, or leave a comment asking you to rework.

**Clean commit history**  

A commit history appears after your PR is merged. Please keep your repository up to date if you plan to create more than one PR, otherwise you may drag the commit history through all your PRs. There are two main ways to do this:
- Open `Terminal` on the local copy of your repository via GitHub Desktop. Run `git reset --hard upstream/develop`. Overwrite your repository with your local copy via GitHub Desktop: `Force push origin`.
- Or delete your repository and start the contribution process from scratch.

### icontool.py

This tool will help you if you regularly contribute icons or missing app IDs.

[icontool.py guide](/docs/icontool_guide.md)
