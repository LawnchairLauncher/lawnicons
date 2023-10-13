#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import argparse
import shutil
import os
import re

#
# Global Variables
#
# See https://regex101.com/r/xC9Kh3/2
PATTERN = re.compile(r"([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)\/([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)", re.IGNORECASE)

APPFILTER_PATH = "app/assets/appfilter.xml"
SVGS_FOLDER = "svgs/"
CALENDAR_REGEX = r"(?s)  <!-- Dynamic Calendars -->.*?  <!-- Lawnicons -->"

# Helper Functions
def print_error(msg):
    print("\033[91merror:\033[0m " + msg + "\n")
    exit()

def print_success():
    print("\033[96msuccessfully completed task(s)\033[0m")
    exit()

#
# Logic
#
def sort_components(xml_file):
    """Sorts XML data by 'name' attribute."""
    def check_calendar_components():
        calendar_stuff = re.findall(CALENDAR_REGEX, xml_file)
        return True if calendar_stuff == None else False

    def get_calendar_components():
        # Always get the original file for safety
        original_file = open(APPFILTER_PATH, "r", encoding="utf-8").read()
        calendar_stuff = re.findall(CALENDAR_REGEX, original_file)
        return calendar_stuff
        
    def remove_calendar_components(has_calendar):
        return re.sub(CALENDAR_REGEX, "", xml_file) if has_calendar else xml_file

    def read_calendar_components(xml_data, calendar_stuff):
        return xml_data[:52] + calendar_stuff[0] + "\n" + xml_data[52:] + "\n"

    calendar_stuff = get_calendar_components()
    pure_xml_file = remove_calendar_components(check_calendar_components())

    # Sort the XML by the name tag, thanks https://stackoverflow.com/a/25339725
    xml_data = ET.fromstring(pure_xml_file)
    xml_data[:] = sorted(xml_data, key=lambda child: child.get("name").casefold())

    sorted_xml_data = ET.tostring(xml_data, encoding="unicode")
    sorted_xml_data = '<?xml version="1.0" encoding="UTF-8"?>\n\n' + sorted_xml_data

    sorted_xml_data = read_calendar_components(sorted_xml_data, calendar_stuff)

    return sorted_xml_data

def check_lawnicons_corruption():
    reply_reason = "this may be due to a broken local copy of lawnicons. please clone lawnicons again."

    if not os.path.exists(SVGS_FOLDER):
        print_error(f"svgs folder does not exist. {reply_reason}")

    if not os.path.isfile(APPFILTER_PATH):
        print_error(f"appfilter.xml file does not exist. {reply_reason}")

def find_logic(mode):
    root = ET.parse(APPFILTER_PATH).getroot()
    svgs = os.listdir(SVGS_FOLDER)

    def find_duplicates():
        packages = [i.attrib["component"] for i in root]
        duplicates = {i for i in packages if (packages.count(i) > 1) and not ("calendar" in i)}
        
        if len(duplicates) == 0:
            print("no duplicates found")
            return

        print("duplicates:")
        for i in duplicates:
            print("* " + i)

    def find_unused_icons():
        drawables = []

        for i in root:
            drawable = str(i.attrib.get("drawable", None)) + ".svg"
            drawables.append(drawable)

        unused_list = []

        for i in svgs:
            if i not in drawables:
                if not i.startswith("themed_icon_calendar_"):
                    unused_list.append(i)
        
        if len(unused_list) == 0:
            print("no unused svg files found")
            return
        
        print("unused svg files:")
        for i in unused_list: print("* " + i)
    
    match mode:
        case "duplicates":
            find_duplicates()
        
        case "unused":
            find_unused_icons()
        
        case _:
            print_error("you must specify a mode {duplicates,unused}")

def sort_logic():
    print("sorting icons...")
    xml_file = open(APPFILTER_PATH, "r", encoding="utf-8").read()
    sorted_data  = sort_components(xml_file)

    with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
        f.write(sorted_data)

def parse_component(link_mode, svg, component, name, show_message):
    #
    # Initialization
    #
    if not svg.endswith(".svg"):
        svg += ".svg"
    
    basename = os.path.basename(svg)

    if not PATTERN.match(component):
        print_error("invalid component entry. must be in format \033[4m[PACKAGE_NAME]/[APP_ACTIVITY_NAME]\033[0m, i.e: package.name/component.name")

    # Link mode true
    if link_mode:
        if not os.path.isfile(SVGS_FOLDER + svg):
            print_error(f"svg '{svg}' doesn't exist in the svgs directory.")

    # Link mode false
    else:
        if not os.path.isfile(svg):
            path = f"directory `{os.path.dirname(svg)}/`"
            print(path)
            if path == "directory `.`" or path == "directory ``":
                path = "current directory"

            print_error(f"svg '{basename}' doesn't exist in {path}. check if the file exists and try again.")
        
        if os.path.isfile(SVGS_FOLDER + basename):
            user_input = input(f"\033[93mwarning\033[0m: svg \033[4m{basename}\033[0m already exists in the svgs directory. replace? (Yes/No/Link) [N] ").lower()
            if  user_input == "" or user_input[0] == "n":
                exit()
    
            if user_input[0] == "l":
                parse_component(True, basename, component, name, show_message)
                return
                
            if user_input[0] != "y": exit()
            
            print(f"replacing {basename} in svgs folder...")

        svg_in_svgs_folder = SVGS_FOLDER + basename

        try:
            shutil.copyfile(svg, svg_in_svgs_folder)
        except shutil.SameFileError:
            print_error(f"\033[4m{basename}\033[0m has the same contents as \033[4m{svg_in_svgs_folder}\033[0m. cannot continue")

    #
    # Writing to file
    #

    # Remove .svg extension
    drawable = basename[:-4]

    # xml_file        the actual xml file
    # line           the actual xml element in string form
    # pure_xml_file    the xml file without the calendar declarations
    xml_file = open(APPFILTER_PATH, "r", encoding="utf-8").read()

    line = f'  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />'
    pure_xml_file = re.sub(CALENDAR_REGEX, "", xml_file)

    # Add the line
    edited_xml_file = pure_xml_file[:52] + line + pure_xml_file[52:]

    # Sort
    sorted_xml_data = sort_components(edited_xml_file)

    with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
        f.write(sorted_xml_data)

    #
    # Print success message
    #
    with open(APPFILTER_PATH, encoding="utf-8") as file:
        lines = file.readlines()

    for number, line in enumerate(lines, 1):
        if component in line:
            if link_mode:
                action = "linked"
            else:
                action = "added"
            print(
                f"{action} \033[92m{name}\033[0m app to \033[92m`@drawable/{drawable}`\033[0m in line \033[92m{number}\033[0m of appfilter.xml"
            )

            if show_message:
                if link_mode:
                    print(
                        f"* {name} (linked `{component}` to `@drawable/{drawable}`)"
                    )
                    continue

                print(
                    f"* {name} (`{component}`)"
                )

def remove_component(component, do_delete, message):
    pattern1 = re.compile(r"([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)", re.IGNORECASE)
    pattern2 = PATTERN
    error_msg = "invalid component name. format must be either \033[4m[PACKAGE_NAME]\033[0m or \033[4m[PACKAGE_NAME]/[APP_ACTIVITY_NAME]\033[0m"

    if not pattern1.match(component):
        print_error(error_msg)
    elif not pattern2.match(component):
        if ("/" in component):
            print_error(error_msg)

    with open(APPFILTER_PATH, "r", encoding="utf-8") as file:
        lines = file.readlines()

    with open(APPFILTER_PATH, "w", encoding="utf-8") as f:
        for linenumber, line in enumerate(lines, 1):
            if component not in line:
                f.write(line)

            elif component in line:
                deleted_line = line
                number = linenumber
                print(
                    f"removed \033[92m{component}\033[0m icon in line \033[92m{number}\033[0m"
                )

                if message:
                    print(f"* {component} (removed)")

    if do_delete:
        deleted_file = ET.fromstring(deleted_line).get("drawable") + ".svg"
        os.remove(SVGS_FOLDER + deleted_file)
        print(f"deleted \033[92m{deleted_file}\033[0m")

# Parser Logic
def add_parser(args):
    parse_component(False, args.svg, args.component, args.name, args.message)

def link_parser(args):
    parse_component(True, args.svg, args.component, args.name, args.message)

def remove_parser(args):
    remove_component(args.component, args.delete, args.message)

def sort_parser():
    sort_logic()

def find_parser(args):
    find_logic(args.mode)

#
# Parser Initialization
#
parser = argparse.ArgumentParser(
    prog="icontool", description="a CLI tool to help contributors with adding icons. requires the use of subcommands {add,link,remove,sort,find}. for help with a specific subcommand, type 'icontool.py <subcommand> -h'")
parser.add_argument(
    "-m", "--message", action="store_true", help="shows a list item for use in a pull request")

subparsers = parser.add_subparsers(
    help='what action icontool should use', dest="subcommand")

parser_add = subparsers.add_parser(
    "add", help='adds an icon SVG and an entry to appfilter.xml, links SVG to component', aliases=['a'])
parser_link = subparsers.add_parser(
    "link", help='adds an entry to appfilter.xml, links SVG to component', aliases=['l'])
parser_remove = subparsers.add_parser(
    "remove", help='removes an entry on appfilter.xml, can optionally delete SVG', aliases=['d', 'r'])
parser_sort = subparsers.add_parser(
    "sort", help='sorts the appfilter.xml file by component name', aliases=['s'])
parser_find = subparsers.add_parser(
    "find", help='find either duplicates in appfilter.xml or unlinked SVGs', aliases=['f'])

# Remove parser
parser_remove.add_argument(
    "component", help="the component to remove. can be [PACKAGE_NAME]/[APP_ACTIVITY_NAME] or [PACKAGE_NAME]")
parser_remove.add_argument(
    "-d", "--delete", help="enables SVG deletion. SVG derived from `drawable` entry in each `<item>`", action="store_true")

# Add parser
parser_add.add_argument(
    "svg", help="the path of the SVG file that will be added")
parser_add.add_argument(
    "component", help="the component name. format must be `[PACKAGE_NAME]/[APP_ACTIVITY_NAME]`")
parser_add.add_argument(
    "name", help="the displayed name of the app. if multiple lines, use a string like ``\"App Name\"`")

# Link parser
parser_link.add_argument("svg", help="the file name of the SVG icon.")
parser_link.add_argument(
    "component", help="the component name. format must be `[PACKAGE_NAME]/[APP_ACTIVITY_NAME]`")
parser_link.add_argument(
    "name", help="the displayed name of the app. if multiple lines, use a string like ``\"App Name\"`")

# No additional arguments for `sort` parser

# Find parser
parser_find.add_argument("mode", help="the mode to use {duplicates, unused}.`duplicates` = find duplicates in appfilter.xml.`unused` = find unused files in svgs/")

args = parser.parse_args()

#
# Main Functions
#
check_lawnicons_corruption()

# Match proper subcommand
match args.subcommand:
    case "add" | "a":
        add_parser(args)

    case "link" | "l":
        link_parser(args)

    case "remove" | "r" | "d":
        remove_parser(args)

    case "sort" | "s":
        sort_parser()
    
    case "find" | "f":
        find_parser(args)

    case _:
        print_error("you must specify a subcommand {add,link,remove,sort,find}")

print_success()
