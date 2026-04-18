#!/usr/bin/env python3
import argparse
import os
import re
import shutil
import xml.etree.ElementTree as ElementTree

#####
# Global variables
#####
PATTERN = re.compile(r"([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)/([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)",
                     re.IGNORECASE)
APPFILTER_PATH = "./app/assets/appfilter.xml"
SVGS_FOLDER = "./svgs/"
CALENDARS_PATTERN = r'(  <!-- Dynamic Calendars -->.*?<!-- Lawnicons -->)'
LAWNICONS_PATTERN = r'<!-- Lawnicons -->\s*(.*?)\s*</resources>'


#####
# Helper functions
#####
def print_error(msg):
    print("\033[91m" + "error:\033[0m " + msg + "\n")
    exit()


def check_file_existence():
    if not os.path.exists(SVGS_FOLDER):
        print_error("svgs folder does not exist. Please ensure the path is correct.")
    if not os.path.isfile(APPFILTER_PATH):
        print_error("appfilter.xml file does not exist. Please ensure the path is correct.")


def validate_component_format(component):
    if not PATTERN.match(component):
        print_error(
            "Invalid component entry. Must be in format [PACKAGE_NAME]/[APP_ACTIVITY_NAME], e.g., package.name/component.name")


#####
# Primary logic
#####
def sort_xml_file(file, new_item):
    # The use of strings instead of XML parsers/ElementTree is to preserve all newlines and comments in the file.
    dynamic_calendars_section = re.search(CALENDARS_PATTERN, file, re.DOTALL)
    if dynamic_calendars_section:
        x = dynamic_calendars_section.group(0)
    else:
        x = ""

    lawnicons_section = re.search(LAWNICONS_PATTERN, file, re.DOTALL)
    lawnicons_items = lawnicons_section.group(1).strip().splitlines() if lawnicons_section else []

    if new_item:
        lawnicons_items.append(new_item)

    lawnicons_items_sorted = sorted(
        ["  " + line.strip() for line in lawnicons_items if '<item ' in line],
        key=lambda item: item.split('name="')[1].split('"')[0].lower()
    )

    result = '<?xml version="1.0" encoding="UTF-8"?>\n\n<resources>\n'
    result += x + '\n'
    result += '\n'.join(lawnicons_items_sorted) + '\n'
    result += '</resources>\n'

    return result


def add_or_link_component(link_mode, svg, component, name):
    validate_component_format(component)

    def is_readable(file_path):
        return os.path.isfile(file_path) and os.access(file_path, os.R_OK)

    def prompt_user_for_replacement(svg_file):
        options = ["y", "n", "l"]
        while True:
            user_input = input(
                f"\033[93mwarning\033[0m: svg \033[4m{svg_file}\033[0m already exists in the svgs directory. Replace? (Yes/No/Link) [N] ").lower()
            if user_input in options:
                return user_input
            print("Invalid input, please enter 'y', 'n', or 'l'.")

    def copy_svg_to_svgs_folder(svg_file, svg_file_in_folder):
        try:
            shutil.copyfile(svg_file, svg_file_in_folder)
        except shutil.SameFileError:
            print_error(
                f"The source and destination SVG are identical: '{os.path.basename(svg_file)}'")
        except (PermissionError, FileNotFoundError) as e:
            print_error(f"Error copying file: {str(e)}")
        except Exception as e:
            print_error(f"Unexpected error while copying file: {str(e)}")

    # SVG checks
    if not svg.endswith(".svg"):
        svg += ".svg"
    basename = os.path.basename(svg)

    if link_mode:
        if not is_readable(os.path.join(SVGS_FOLDER, svg)):
            print_error(f"SVG '{svg}' doesn't exist in the svgs directory.")
    else:
        if not is_readable(svg):
            print_error(f"SVG '{basename}' doesn't exist in the current directory.")

        # Handle file replacement
        if is_readable(os.path.join(SVGS_FOLDER, basename)):
            prompt_input = prompt_user_for_replacement(basename)
            if prompt_input == "n":
                exit()
            elif prompt_input == "l":
                add_or_link_component(True, basename, component, name)
                return
            elif prompt_input == "y":
                print(f"Replacing {basename} in svgs folder...")

        svg_in_svgs_folder = os.path.join(SVGS_FOLDER, basename)
        copy_svg_to_svgs_folder(svg, svg_in_svgs_folder)

    drawable = basename[:-4]

    line = f'  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />'

    with open(APPFILTER_PATH, "r", encoding="utf-8") as file:
        sorted_lines = sort_xml_file(file.read(), line)
        with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
            f.write(sorted_lines)

    with open(APPFILTER_PATH, "r", encoding="utf-8") as file:
        lines = file.readlines()
        for number, line in enumerate(lines, 1):
            if component in line:
                action = "Link" if link_mode else "Add"
                print(
                    f"{action}ed \033[92m{name}\033[0m app to \033[92m`@drawable/{drawable}`\033[0m in line \033[92m{number}\033[0m of appfilter.xml"
                )


def remove_component(component, delete_svg):
    validate_component_format(component)

    with open(APPFILTER_PATH, "r", encoding="utf-8") as file:
        lines = file.readlines()

    component_found = False
    with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
        for linenumber, line in enumerate(lines, 1):
            if component not in line:
                f.write(line)
            else:
                deleted_line = line
                component_found = True
                print(
                    f"Removed \033[92m{component}\033[0m icon in line \033[92m{linenumber}\033[0m")

    if not component_found:
        print_error(f"Component {component} not found.")

    if delete_svg:
        drawable = ElementTree.fromstring(deleted_line).get("drawable") + ".svg"
        try:
            os.remove(os.path.join(SVGS_FOLDER, drawable))
            print(f"Deleted \033[92m{drawable}\033[0m")
        except FileNotFoundError:
            print_error(f"SVG file \033[92m{drawable}\033[0m not found for deletion.")


def find_logic(mode):
    root = ElementTree.parse(APPFILTER_PATH).getroot()
    svgs = os.listdir(SVGS_FOLDER)

    def find_duplicates(root_file):
        packages = [item.attrib["component"] for item in root_file]
        duplicate_elements = {pkg for pkg in packages if
                              packages.count(pkg) > 1 and "calendar" not in pkg}
        return duplicate_elements

    def find_unused_icons(root_file, svgs_list):
        drawables = [f"{item.attrib.get('drawable', None)}.svg" for item in root_file]
        unused_list = [
            svg_item for svg_item in svgs_list
            if (svg_item not in drawables and not svg_item.startswith("themed_icon_calendar_")
                )
        ]
        return unused_list

    match mode:
        case "duplicates":
            duplicates = find_duplicates(root)
            if duplicates:
                print("Duplicates found:")
                for dup in duplicates:
                    print("* " + dup)
            else:
                print("No duplicates found.")

        case "unused":
            unused = find_unused_icons(root, svgs)
            if unused:
                print("Unused SVG files:")
                for svg in unused:
                    print("* " + svg)
            else:
                print("No unused SVG files found.")


def sort_logic():
    with open(APPFILTER_PATH, "r", encoding="utf-8") as file:
        sorted_lines = sort_xml_file(file.read(), False)
        with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
            f.write(sorted_lines)


#####
# Parser logic
#####

def parse_args():
    parser = argparse.ArgumentParser(prog="icontool", description="A CLI tool for managing icons.")
    subparsers = parser.add_subparsers(dest="command")

    add_parser = subparsers.add_parser("add", help="Add a new component.", aliases=['a'])
    add_parser.add_argument("svg", help="The name of the SVG file (without .svg extension).")
    add_parser.add_argument("component",
                            help="Component name in format PACKAGE_NAME/ACTIVITY_NAME.")
    add_parser.add_argument("name", help="Display name for the component.")

    link_parser = subparsers.add_parser("link", help="Link an existing SVG to a component.",
                                        aliases=['l'])
    link_parser.add_argument("svg", help="The name of the SVG file (without .svg extension).")
    link_parser.add_argument("component",
                             help="Component name in format PACKAGE_NAME/ACTIVITY_NAME.")
    link_parser.add_argument("name", help="Display name for the component.")

    remove_parser = subparsers.add_parser("remove", help="Remove a component.", aliases=['r', 'd'])
    remove_parser.add_argument("component", help="Component name to remove.")
    remove_parser.add_argument("--delete", action="store_true",
                               help="Delete the associated SVG file.")

    find_parser = subparsers.add_parser("find", help="Find duplicates or unused SVGs.",
                                        aliases=['f'])
    find_parser.add_argument("mode", choices=["duplicates", "unused"],
                             help="Mode to find duplicates or unused SVGs.")

    subparsers.add_parser("sort", help='sorts the appfilter.xml file by component name',
                          aliases=['s'])

    return parser.parse_args()


# Main Logic
check_file_existence()
args = parse_args()

match args.command:
    case "add" | "a":
        add_or_link_component(False, args.svg, args.component, args.name)
    case "link" | "l":
        add_or_link_component(True, args.svg, args.component, args.name)
    case "remove" | "r" | "d":
        remove_component(args.component, args.delete)
    case "find" | "f":
        find_logic(args.mode)
    case "sort" | "s":
        sort_logic()
    case _:
        print_error("Invalid command. Please use add, link, remove, sort, or find.")
