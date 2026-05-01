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

1. Study the Lawnicons design guidelines in practice (for example, in Figma) and create a suitable icon (*.svg).
2. Learn how to find the app ID that you will need to link with the created icon.
3. Learn how to fork the Lawnicons repository and make a local copy for yourself, where you will add the created icon.
4. Add the created icon and the app ID to your local copy and push the changes to your fork on GitHub.
5. Create a pull request to the Lawnicons repository and wait for a review.


## Lawnicons design guidelines

The contributors who laid the foundations: [GrabsterTV](https://github.com/Grabstertv) and [Chefski](https://github.com/Chefski).

> [!TIP]
> The design guidelines are also available in Figma, you can practice there.  
> [View in Figma](https://www.figma.com/community/file/1544976260626797886)

[Common issues](https://github.com/LawnchairLauncher/lawnicons/blob/develop/docs/images/common-issues-to-fix.png)

### Approach

Please read these guidelines carefully to minimize rework. The goal is to create high-quality icons that represent their apps, even if it means redesigning from scratch.

Tips: prioritize quality over exact reproduction and practice on simple icons first.

[Merged PRs](https://github.com/LawnchairLauncher/lawnicons/pulls?q=is%3Apr+is%3Amerged+label%3Aicons)

### Naming

**TL;DR**
```
_2048.svg | 2048
lawnicons.svg | Lawnicons
habitacao_caixa.svg | Habitação Caixa
beijing_card.svg | 北京一卡通 ~~ Beijing Card
a_and_w.svg | A&amp;W
```

**App name**  

The app name should be in its primary language, sourced from app stores. For non-English names, add a localized or transliterated English version, separated by `~~` (main name first). If the name is mostly English letters, no second name is needed.

Delete things that aren't part of an app name, and use HTML character references for special symbols (for example, &amp; instead of &).

**Icon name (drawable)**  

Repeat the app name, using `a–z`, `0–9`, and `_` for spaces. Insert `_` before a digit if the icon name starts with one. For multiple apps sharing one icon, use the most popular name.

### Fundamentals

> [!TIP]
> [View on YouTube](https://youtu.be/XO-5IwowonQ)

#### 1 Canvas

<img src="docs/images/fundamentals-1-canvas.png" alt="1 Canvas" height="200" />

`192 × 192 px`. Use the correct canvas size to create a safe zone around icons.  

#### 2 Abstract icons

<img src="docs/images/fundamentals-2-abstract-icons.png" alt="2 Abstract icons" height="200" />

Determine the abstract icon size before you start. The exact size is determined by the stroke's position, weight, and the graphic editor used. For a `12 px` center stroke in Figma, the icon content area is `148 × 148 px`.

Tips: follow the blue guides, use existing icons as an example, aim for pixel-perfect.

#### 3 Square icons

<img src="docs/images/fundamentals-3-square-icons.png" alt="3 Square icons" height="200" />

Determine the square icon size before you start. These are icons with `50%` or more of the edges running along the square. The exact size is determined by the stroke's position, weight, and the graphic editor used. For a `12 px` center stroke in Figma, the square icon content area is `142 × 142 px`.

Tips: follow the golden guides, use existing icons as an example, aim for pixel-perfect.

#### 4 Color

<img src="docs/images/fundamentals-4-color.png" alt="4 Color" height="200" />

All lines must be non-transparent black color: `#000000`.  

#### 5 Stroke weights

<img src="docs/images/fundamentals-5-stroke-weights.png" alt="5 Stroke weights" height="200" />

Core stroke weight: `12 px`  
Minimal icons: `14 px`  
Dense icons: `10 px`  
Ellipses, rectangles and fine details: `12 px`, `10 px`, `8 px`, `6 px`  

Exact values only, no fill.

#### 6 Caps and joints

<img src="docs/images/fundamentals-6-caps-and-joints.png" alt="6 Caps and joints" height="200" />

Caps and joints should be rounded.  

#### 7 Corner radius

<img src="docs/images/fundamentals-7-corner-radius.png" alt="7 Corner radius" height="200" />

Use no less than `6 px` for `90°` angles. Refer to the original icon to select a value. It's allowed to leave a `0 px` radius in cases when the others spoil the shape: for instance, when `90°` angles are formed of short lines.

### Quality

#### 1 Consistency

<img src="docs/images/quality-1-consistency.png" alt="*1 Consistency" height="200" />

All shapes should be outlined.

#### 2 Visual balance

<img src="docs/images/quality-2-visual-balance.png" alt="2 Visual balance" height="200" />

Avoid drastic changes in stroke weights. For instance, using a `12 px` stroke and suddenly decreasing it to `8 px` creates an unbalanced visual effect.

#### 3 Black spots

<img src="docs/images/quality-3-black-spots.png" alt="3 Black spots" height="200" />

Avoid black spots as much as possible.

#### 4 Excessive density

<img src="docs/images/quality-4-excessive-density.png" alt="4 Excessive density" height="200" />

Keep at least `8 px` between lines, using an `8 × 8 px` rectangle to verify the spacing.  
It’s better to make the distance a little more, especially in closed shapes.

Tips: move lines further apart or combine into one, enlarge original icons to make the main features easier to draw.

#### 5 Alignment

<img src="docs/images/quality-5-alignment.png" alt="5 Alignment" height="200" />

Icons should be centered, but shape-aware. Align them to the optical center as much as possible within the icon content area. The optical aligment is where your icon looks and feels centered.

#### 6 Text icons

<img src="docs/images/quality-6-text-icons.png" alt="6 Text icons" height="200" />

Text longer than `3` letters in `1` line usually doesn’t fit the Lawnicons style. Brands and apps with text icons often need to be studied in order to create a recognizable Lawnicons-style icon.

If you want to keep only a text, then it should be of high quality and occupy at least `¹⁄₃` of the icon content area.

#### 7 Complex icons

<img src="docs/images/quality-7-complex-icons.png" alt="7 Complex icons" height="200" />

First, try to make a complex icon based on the original. When it’s clear that the original icon can’t be conveyed in the Lawnicons style, you need to study the visual part of an app or a game. Whatever you come to, the result should be at least logical and high-quality.

Recognition sources: branding guidelines, UI or gameplay, website favicons, in-app icons, essence of an app or game, and a combination of recognizable features with your own ideas.

#### 8 Minimal icons

<img src="docs/images/quality-8-minimal-icons.png" alt="8 Minimal icons" height="200" />

Some minimal icons need distinctive features to aid recognition.

#### 9 Version badges

<img src="docs/images/quality-9-version-badges.png" alt="9 Version badges" height="200" />

Use one of our version badges to highlight a separate version of an app if the original icons are indistinguishable. For instance, it could be nightly builds or paid apps with a free one available. Keep in mind that cases such as Opera Mini or Firefox Klar are different.

Tips: cut lines around the badge, place it in the lower right corner when possible, and don't shift icons for it.

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
Package: `app.lawnchair.lawnicons`  
Activity: `app.lawnchair.lawnicons.MainActivity`  
App ID: `app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity`  

**Lawnicons**  

This method is suitable if you are interested in installed apps that aren't supported in Lawnicons.
1. Install and open Lawnicons.
2. Long press our logo.
3. Swipe down.
4. Copy missing app IDs to clipboard.
5. Save it wherever it's convenient.

[Download Lawnicons](https://github.com/LawnchairLauncher/lawnicons#download)

**Icon Request**  

1. Download and launch Icon Request.
2. Tap one of the options:
- UPDATE EXISTING — to copy app IDs.
- REQUEST NEW — to save icon images and app IDs. This option is better if you are creating icons.
3. Use the Icon Request toolbar to select apps.
4. Copy, save or share.

[Google Play](https://play.google.com/store/apps/details?id=de.kaiserdragon.iconrequest) • [GitHub](https://github.com/Kaiserdragon2/IconRequest/releases)

**Icon Pusher**  

1. Download and launch Icon Pusher.
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

> [!TIP]
> [View on YouTube](https://youtu.be/UXic1zy-CiQ)

You need to link SVGs and app IDs correctly, create a PR to our repository through your fork, and wait for it to be reviewed.

Tips
- Avoid name conflicts.
- Add missing app IDs to icons that are identical to the originals.
- Make sure your icons or missing app IDs haven't been added earlier: search the `appfilter.xml` and check PRs.

[Simplified icon contribution](https://docs.google.com/spreadsheets/d/11YoKFuksS3Tmi_UNoSTtrqfYydhDqbR-2t0Fnsr7wL4/edit?usp=sharing) • [How to find app IDs](#how-to-find-app-ids) • [Icon contribution tools](#icon-contribution-tools) • [appfilter.xml](app/assets/appfilter.xml) • [PRs](https://github.com/LawnchairLauncher/lawnicons/pulls)

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
7. Open your fork in a web browser and create a PR: `Contribute → Open pull request`. Describe your PR according to our templates.
8. Make sure that the build went without errors and await a review (better to do a self-review).
9. We will merge your PR, fix the little things, or leave a comment asking you to rework.

**Clean commit history**  

A commit history appears after your PR is merged. Please keep your repository up to date if you plan to create more than one PR, otherwise you may drag the commit history through all your PRs. There are two main ways to do this:
- Open `Terminal` on the local copy of your repository via GitHub Desktop. Run `git reset --hard upstream/develop`. Overwrite your repository with your local copy via GitHub Desktop: `Force push origin`.
- Or delete your repository and start the contribution process from scratch.

### icontool.py

This tool will help you if you regularly contribute icons or missing app IDs.

[icontool.py guide](/docs/icontool_guide.md)
