import sys
import re
import argparse
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Dict, Set
from unidecode import unidecode


def slugify_app_name(app_name: str) -> str:
    """Converts an app name from appfilter.xml to a valid drawable filename."""
    # 1. Handle dual names: "Beijing Card ~~ 北京一卡通" -> "beijing_card"
    primary_name = app_name.split('~~')[0].strip()

    # 2. Handle HTML entities: "A&amp;W" -> "A&W"
    # A simple replacement is safe enough for this specific use case.
    primary_name = primary_name.replace('&amp;', '&')

    # 3. Transliterate to ASCII: "Habitação Caixa" -> "Habitacao Caixa"
    slug = unidecode(primary_name)

    # 4. Standard slugification
    slug = slug.lower()
    slug = re.sub(r'[^a-z0-9]+', '_', slug)  # Replace non-alphanumeric with _
    slug = slug.strip('_')

    # 5. Handle leading digits
    if slug and slug[0].isdigit():
        slug = f"_{slug}"

    return slug


def main():
    parser = argparse.ArgumentParser(
        description="Appfilter naming consistency filter")
    parser.add_argument("appfilter", help="Path to appfilter.xml")
    parser.add_argument(
        "drawables_dir", help="Path to the directory containing SVGs")
    args = parser.parse_args()

    appfilter_path = Path(args.appfilter)
    drawables_path = Path(args.drawables_dir)

    # 1. Parse XML and get all declared drawables
    try:
        tree = ET.parse(appfilter_path)
        root = tree.getroot()
        xml_items: Dict[str, str] = {
            item.get('drawable'): item.get('name')
            for item in root.findall('.//item') if item.get('drawable')
        }  # type: ignore
    except Exception as e:
        print(f"appfilter.xml: XML_PARSE_ERROR ({e})")
        sys.exit(1)

    # 2. Get all SVG files from the directory
    disk_svgs: Set[str] = {p.stem for p in drawables_path.glob("*.svg")}

    xml_drawables = set(xml_items.keys())

    errors_found = False

    # 3. Check for mismatches
    orphaned_svgs = disk_svgs - xml_drawables
    missing_svgs = xml_drawables - disk_svgs

    for orphan in sorted(orphaned_svgs):
        print(f"{orphan}.svg: ORPHAN_FILE (Not declared in appfilter.xml)")
        errors_found = True

    for missing in sorted(missing_svgs):
        print(
            f"{missing}.svg: MISSING_FILE (Declared in appfilter.xml but file not found)")
        errors_found = True

    # 4. Check for naming convention violations
    for drawable, name in xml_items.items():
        if drawable not in disk_svgs:
            continue  # Skip if file is missing

        expected_drawable = slugify_app_name(name)
        if drawable != expected_drawable:
            print(
                f"{drawable}.svg: NAME_MISMATCH (Expected '{expected_drawable}' based on app name '{name}')")
            errors_found = True

    if errors_found:
        sys.exit(1)
    else:
        print("Name consistency check passed.", file=sys.stderr)
        sys.exit(0)


if __name__ == "__main__":
    main()
