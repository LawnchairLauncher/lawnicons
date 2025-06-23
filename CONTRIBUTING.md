# Lawnicons contributing guide

Welcome to the Lawnicons contributing guide!

In case of unclear wording, ask us in our Discord. If you find errors or want to suggest improvements in the guide itself, create an issue.

[Our Discord](https://discord.com/invite/3x8qNWxgGZ)

## Involvement

**Contributors**

The Lawnchair team is focused on development only. Our community (basically everyone who wants to) makes icons and sometimes touches the code too. 

[Our contributors](https://github.com/LawnchairLauncher/lawnicons/graphs/contributors) • [Lawnchair](https://github.com/LawnchairLauncher/lawnchair)

**Development**  

The main tasks are to maintain Lawnicons and interaction with launchers, fix bugs, add new features and automate organizational processes. Please see our issues for more details.

**Icons**  

You can contribute your icons, fulfill icon requests, add missing app components, refine and update existing icons, clean up dead apps and duplicates. Mastering the Lawnicons design guidelines in practice will allow you to do icon reviews.

[Requested icons](https://docs.google.com/spreadsheets/d/1AXc9EDXA6udZeGROtB5nuABjM33VluGY_V24tIzHaKc/edit?usp=sharing)

## Contributing code

Code-related contributions are welcome. Significant changes to the UI should be discussed in our Discord. Generally, we want to keep things clean and simple.

Visit the Lawnicons developer wiki for developer information regarding Lawnicons.

[Lawnicons developer wiki](https://github.com/LawnchairLauncher/lawnicons/wiki)

## Lawnicons design guidelines

The contributors who laid the foundations: [GrabsterTV](https://github.com/Grabstertv) and [Chefski](https://github.com/Chefski).

> [!TIP]
> The design guidelines are also available in Figma, you can practice there.  
> [View in Figma](https://www.figma.com/community/file/1227718471680779613)


### Approach

The Lawnicons style is built on strong fundamentals and a commitment to quality. To minimize the need for rework, please read these guidelines carefully. Our main goal is to create high-quality icons that clearly represent their respective apps. To achieve this goal, you may sometimes need to redesign an icon from scratch.

Tips
- Use the Lawnicons style, rather than trying to reproduce the original exactly.
- Practice on easy-to-make icons to understand the whole process.
- See how other contributors have made pull requests (PRs).
- Make no more than 5 icons at a time, as long as your PRs require rework.
- Prioritize quality.

[Common issues](https://github.com/LawnchairLauncher/lawnicons/blob/develop/docs/images/common-issues-to-fix.png) • [Merged PRs](https://github.com/LawnchairLauncher/lawnicons/pulls?q=is%3Apr+is%3Amerged+label%3Aicons)

### Fundamentals

**1 Canvas**  

![](docs/images/fundamentals-1-canvas.png)

`192 × 192 px`. Use the correct canvas size to create a safe zone around icons.  

**2 Abstract icons**  

![](docs/images/fundamentals-2-abstract-icons.png)

`160 × 160 px`. The long side of an abstract icon should be `160 px`, but the other side could be smaller. In the case of curved boundaries, the margin of error is `<0.1 px`.  

**3 Square icons**  

![](docs/images/fundamentals-3-square-icons.png)

`154 × 154 px`. These are icons with `50%` or more of the edges running along the square.  

**4 Color**  

![](docs/images/fundamentals-4-color.png)

All lines must be non-transparent black color: `#000000`.  

**5 Stroke weights**  

![](docs/images/fundamentals-5-stroke-weights.png)

Core weight: `12 px`  
Allowable weights: `14 px`, `10 px`, `8 px`  
Fine details: `6 px` 

The stroke weight should be kept at `12 px` in most cases. If an icon is too minimal or dense, you'll need other weights: `14 px` for the most minimal, and `8 px` for the densest. For fine details, you can use `6 px`.

No margin of error. Don’t use a fill. Remember to adjust the size of icons when you change the stroke weights.  

**6 Start and end caps, joints**  

![](docs/images/fundamentals-6-start-and-end-caps-joints.png)

Start and end caps, as well as joints, should be rounded.  

**7 Corner radius**  

![](docs/images/fundamentals-7-corner-radius.png)

Use `6–32 px` for `90°` angles. Refer to the original icon to select a value from the range. It's allowed to leave a `0 px` radius in cases when the others spoil the shape: for instance, when `90°` angles are formed of short lines.

### Quality

**1 Consistency**  

![](docs/images/quality-1-consistency.png)

All icons should be outlined. If necessary, you can use small shapes that mimic a fill but are made with a stroke. For instance, `8 × 8 px` ellipses with a `8 px` stroke. Ensure that any elements don’t overwhelm the overall icon design and align with the outlined style.  

**2 Visual balance**  

![](docs/images/quality-2-visual-balance.png)

Sharp contrast occurs when there is a drastic change in a stroke weight without a smooth transition. For instance, using a `12 px` stroke and suddenly decreasing it to `8 px` creates an unbalanced visual effect.

Tips
- Avoid large differences between adjacent lines.
- Use a gradual transition if it makes sense. For instance, `12 px` → `10 px` → `8 px`.
- Apply a `14 px` stroke for minimal icons.
- Reduce the main stroke weight depending on an icon density.  

**3 Black spots**  

![](docs/images/quality-3-black-spots.png)

Avoid black spots as much as possible.

Tips
- Trim lines.
- Reduce stroke weights.
- Simplify or redraw.  

**4 Excessive density**  

![](docs/images/quality-4-excessive-density.png)

Keep at least `8 px` between lines. Ideally, at least `12 px`.

Tips
- Move lines further apart.
- Combine parallel lines into one.
- Trim or extend lines.
- Reduce stroke weights.
- Enlarge original icons to make the main features easier to draw.
- Simplify or redraw.

**5 Alignment**  

![](docs/images/quality-5-alignment.png)

Icons should be centered, but shape-aware.

In most cases, you should place an icon so that the vertical and horizontal margins from the canvas borders are the same. Some icons will look misaligned because of their shape. You need to align them to the optical center as much as possible within the icon content area. The optical aligment is where your icon looks  and feels centered.

**6 Text icons**  

![](docs/images/quality-6-text-icons.png)

Text longer than `3` letters in `1` line usually don’t fit the Lawnicons style. Brands and apps with text icons often need to be studied in order to create a recognizable Lawnicons-style icon.

If you want to keep only a text, then it should be of high quality and occupy at least `¹⁄₃` of the icon content area.

**7 Complex icons**  

![](docs/images/quality-7-complex-icons.png)

Many complex icons can be made in the Lawnicons style, taking into account the original, so it's worth giving it a try first. When it’s clear that the original icon can’t be conveyed in the Lawnicons style, you need to study the visual part of an app or a game.

Whatever you come to, the result should be at least logical and high-quality.

Sources for creating a recognizable icon:
- Branding guidelines.
- UI or gameplay.
- Website’s favicons.
- In-app icons.
- Essence of an app or a game.
- Combination of recognizable features and your own ideas.  

**8 Minimal icons**  

![](docs/images/quality-8-minimal-icons.png)

Some minimal icons should be detailed based on an app design to become more recognizable. Add distinctive features to them when it makes sense.  

### Naming

**App name**  

The main app name should be in its native language. It can be found in app stores or primary sources.

Non-English apps require an additional name based on the English alphabet. At best it will be a localized official app name. If an app name is mostly made up of letters from the English alphabet, it doesn't need an additional one.

Tips
- Add localized names if available.
- Transliterate non-English names when there are no localized ones.
- Delete things that aren't part of an app name.
- Use the HTML character references for special symbols: for instance, `&amp;` instead of "&".

[Thousands of examples](app/assets/appfilter.xml)

```
Do
<item component="..." drawable="doviz" name="Döviz" />
<item component="..." drawable="gps_status_and_toolbox" name="GPS Status &amp; Toolbox" />
<item component="..." drawable="playstation" name="PlayStation" />
<item component="..." drawable="eromodo" name="Vágyaid ~~ Eromodo" />

Don't
<item component="..." drawable="doviz" name="Döviz ~~ Doviz" />
<item component="..." drawable="gps_status_and_toolbox" name="GPS Status & Toolbox" />
<item component="..." drawable="playstation" name="PlayStation App" />
<item component="..." drawable="eromodo" name="Eromodo" />
```

Separate app names using `~~`. First, the main app name, then the additional one.

```
Do • Considering the origin of the Hulu app
<item component="..." drawable="hulu" name="Hulu ~~ フールー" />

Don't
<item component="..." drawable="hulu" name="フールー ~~ Hulu" />
```

**Icon name (drawable)**  

Repeat the app name if possible. Use `a–z`, `0–9`, and `_` for spaces.  

Tips
- When multiple apps are linked to `1` icon, choose the most popular app name for it.
- Replace non-English letters with English letters.

```
Do
<item component="..." drawable="a_and_w" name="A&amp;W" />
<item component="..." drawable="blade_player" name="Blade Player" />
<item component="..." drawable="lansforsakringar" name="Länsförsäkringar" />
<item component="..." drawable="yahoo_news" name="Yahoo!ニュース ~~ Yahoo! News" />

Don't
<item component="..." drawable="aw" name="A&amp;W" />
<item component="..." drawable="bladeplayer" name="Blade Player" />
<item component="..." drawable="länsförsäkringar" name="Länsförsäkringar" />
<item component="..." drawable="yahoo!_news" name="Yahoo!ニュース ~~ Yahoo! News" />
```

Insert `_` before a digit at the beginning of an icon name.

```
Do
<item component="..." drawable="_9gag" name="9GAG" />

Don't
<item component="..." drawable="9gag" name="9GAG" />
<item component="..." drawable="ninegag" name="9GAG" />
```

## Icon contribution tools

### Vector graphics editor

To create icons, you need a vector graphics editor, which allows you to save icons in SVG format. Mobile vector editors won't work. We recommend Figma because it has easier quality control. You can use Advanced SVG Export to save optimized SVGs in Figma.

[Figma](https://www.figma.com/) • [Advanced SVG Export](https://www.figma.com/community/plugin/782713260363070260) 

### GitHub Desktop

You can use it to create a local copy of your repository on GitHub and upload all the changes. Before getting into your repository, the changes must appear in your local copy.

[GitHub Desktop](https://github.com/apps/desktop)

### App components search tool

You can use it to find app components. If you fulfill icon requests from our table, all the app components are there.

[How to find app components](#how-to-find-app-components)

### Other tools

**File explorer**. It will help you copy icons to a local copy of your repository.

**Text editor**. It will help you to link icons and app components in `appfilter.xml`. This is how icon packs work.

**Terminal (command line)**. It will add convenience if you regularly contribute dozens of icons.

## How to find app components

An app component is a record consisting of a package and an activity, separated by `/`. App components allow you to link icons and apps. 

Sample (Lawnicons)  
Package: `app.lawnchair.lawnicons`  
Activity: `app.lawnchair.lawnicons.MainActivity`  
App component: `app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity`  

**Lawnicons**  

This method is suitable if you are interested in installed apps that aren't supported in Lawnicons.
1. Install and open Lawnicons.
2. Long press our logo.
3. Swipe down.
4. Copy missing app components to clipboard.
5. Save it wherever it's convenient.

[Download Lawnicons](https://github.com/LawnchairLauncher/lawnicons#download)

**Icon Request**  

1. Download and launch Icon Request.
2. Tap one of the options:
- UPDATE EXISTING — to copy app components.
- REQUEST NEW — to save icon images and app components. This option is better if you are creating icons.
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

## Adding icons and missing app components to Lawnicons

You need to link SVGs and app components correctly, create a PR to our repository through your fork, and wait for it to be reviewed.

Tips
- Avoid name conflicts.
- Add missing app components to icons that are identical to the originals.
- Make sure your icons or missing app components haven't been added earlier: search the `appfilter.xml` and check PRs.

[View on YouTube](https://youtu.be/EAvYelOK5Nw) • [How to find app components](#how-to-find-app-components) • [Icon contribution tools](#icon-contribution-tools) • [appfilter.xml](app/assets/appfilter.xml) • [PRs](https://github.com/LawnchairLauncher/lawnicons/pulls)

### Manual process

Let's imagine that you have an icon in SVG format, an app name and an app component.  

Icon: `lawnicons.svg`  
App name: `Lawnicons`  
App component: `app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity`

1. Fork our repository so that you have your own copy to work with. Your repository will be a bridge between our repository and your contribution.
2. Clone your repository in GitHub Desktop and open it with a file explorer. This is your local copy.
3. Сopy `lawnicons.svg` to `svgs/` folder. Memorize the icon name so that you can link the app component to it.
4. Open `app/assets/appfilter.xml` and add a new line based on your information. Take into account the alphabetical sorting by the app name.

```
Do
<item component="ComponentInfo{app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity}" drawable="lawnicons" name="Lawnicons" />

Template
<item component="ComponentInfo{APP_COMPONENT}" drawable="ICON_NAME" name="APP_NAME" />
```

5. Save all your changes and push it to your repository via GitHub Desktop.
6. Open your repository in a web browser and create a PR: `Contribute → Open pull request`. Describe your PR according to our templates.
7. Make sure that the build went without errors. Wait for a review or do a self-review.
8. We will merge your PR, fix the little things, or leave a comment asking you to rework.

**Clean commit history**  

A commit history appears after your PR is merged. Please keep your repository up to date if you plan to create more than one PR, otherwise you may drag the commit history through all your PRs. There are two main ways to do this:
- Open `Terminal` on the local copy of your repository via GitHub Desktop. Run `git reset --hard upstream/develop`. Overwrite your repository with your local copy via GitHub Desktop: `Force push origin`.
- Or delete your repository and start the contribution process from scratch.

### icontool.py

This tool will help you if you regularly contribute icons or missing app components.

[icontool.py guide](/docs/icontool_guide.md)
