Thanks for your contribution!

We hope that you followed [the Lawnicons guidelines](https://github.com/LawnchairLauncher/lawnicons/blob/develop/CONTRIBUTING.md) and made sure that Lawnicons is built correctly.

> [!Tip]
> While waiting for a brief and strict review from our team, you can do a self-review to ensure that your icons are suitable for Lawnicons. Additionally, you can find and install [the debug build of your PR](https://github.com/LawnchairLauncher/lawnicons/actions/workflows/build_debug_apk.yml).

### Lawnicons guidelines
#### Quality
1. Ensure that icons are easily recognizable.
2. Align icons to [the visual center](https://crazybitsstudios.com/another-way-of-aligning-elements-when-creating-icons) as much as possible within the guidelines. The visual center is where your icon looks and feels centered.
3. Avoid noticable black spots by reducing the stroke width or simplifying the icons.
4. Avoid close distances between strokes. The icons on the phone screen will be smaller, so the small distances between the strokes will stick together.
5. Avoid drastic changes in stroke widths. When the strokes next to each other differ in width by 4px or more, the icon will look sloppy.
#### Canvas and sizes
1. Canvas: `192×192px`.
2. Non-square icons: the long side of the icons should be `160px`.
3. Square icons: `154×154px`.
#### Color, stroke width and rounding
1. Color: non-transparent black `#000`.
2. No fill. Base stroke width: `12px`. `14px`, `10px`, `8px` — depending on the shape of the icons. `6px` — for fine details.
3. Rounded ends and joins. 90° corners are rounded by `6-32px`.
#### Naming
1. Names should match the official app name and contain no additional text.
2. If the first `3` characters of the app name contain letters not from the English alphabet, then add a localized (or transliterated) name via `~~`. Example: `京东 ~~ JD`.
3. The names of the drawables should repeat the names of the apps if nothing prevents it.
