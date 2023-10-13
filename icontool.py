#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import argparse
import shutil
import os
import re

#
# Global Vars
#
# see https://regex101.com/r/xC9Kh3/2
_pattern = re.compile(r"([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)\/([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)", re.IGNORECASE)

_appfilter = "app/assets/appfilter.xml"
_svgs_folder = "svgs/"
_calendarregex = r"(?s)  <!-- Dynamic Calendars -->.*?  <!-- Lawnicons -->"

# helper functions
def _printerror(msg):
    print("\033[91merror:\033[0m " + msg + "\n")
    exit()


def printsuccess():
    print("\033[96msuccessfully completed task(s)\033[0m")
    exit()

#
# Logic
#
def _sort_components(xmlfile):
    def _checkCalendarComponents():
        calendarstuff = re.findall(
            _calendarregex, xmlfile
        )

        return True if calendarstuff == None else False

    def _getCalendarComponents():
        # always get original file for safety
        originalfile = open(_appfilter, "r").read()

        calendarstuff = re.findall(
            _calendarregex, originalfile
        )
        return calendarstuff
        
    def _removeCalendarComponents(hascalendar):
        return (
            re.sub(_calendarregex, "", xmlfile)
        ) if hascalendar else xmlfile

    def _readdCalendarComponents(xmldata, calendarstuff):
        return (
            xmldata[:52] + calendarstuff[0] + "\n" + xmldata[52:] + "\n"
        )

    calendarstuff = _getCalendarComponents()
    purexmlfile = _removeCalendarComponents(_checkCalendarComponents())


    # sort the xml by the name tag, thx https://stackoverflow.com/a/25339725
    xmldata = ET.fromstring(purexmlfile)
    xmldata[:] = sorted(
        xmldata, key=lambda child: child.get("name").casefold())

    sortedxmldata = ET.tostring(xmldata, encoding="unicode")
    sortedxmldata = (
        '<?xml version="1.0" encoding="UTF-8"?>\n\n' + sortedxmldata)

    sortedxmldata = _readdCalendarComponents(sortedxmldata, calendarstuff)

    return sortedxmldata

def check_lawnicons_corruption():
    replyreason = "this may be due to a broken local copy of lawnicons. please clone lawnicons again."

    if not os.path.exists(_svgs_folder):
        _printerror(f"svgs folder does not exist. {replyreason}")

    if not os.path.isfile(_appfilter):
        _printerror(f"appfilter.xml file does not exist. {replyreason}")

def find_logic(mode):
    root = ET.parse(_appfilter).getroot()
    svgs = os.listdir(_svgs_folder)

    def _find_duplicates():
        packages = [i.attrib["component"] for i in root]
        duplicates = {i for i in packages if (packages.count(i) > 1) and not ("calendar" in i)}
        
        if len(duplicates) == 0:
            print("no duplicates found")
            return

        print("duplicates:")
        for i in duplicates:
            print("* " + i)

    def _find_unused_icons():
        drawables = []

        for i in root:
            drawable = str(i.attrib.get("drawable", None)) + ".svg"
            drawables.append(drawable)

        _list = []

        for i in svgs:
            if i not in drawables:
                if not i.startswith("themed_icon_calendar_"):
                    _list.append(i)
        
        if len(_list) == 0:
            print("no duplicates found")
            return
        
        print("unused svg files:")
        for i in _list: print("* " + i)
    
    match mode:
        case "duplicates":
            _find_duplicates()
        
        case "unused":
            _find_unused_icons()
        
        case _:
            _printerror("you must specify a mode {duplicates,unused}")

def sort_logic():
    print("sorting icons...")
    xmlfile = open(_appfilter, "r").read()
    sorted_data  = _sort_components(xmlfile)

    f = open(_appfilter, "w")
    f.write(sorted_data)
    f.close()

def parse_component(linkmode, svg, component, name, showMessage):
    #
    # initialization
    #
    if not svg.endswith(".svg"):
        svg += ".svg"
    
    basename = os.path.basename(svg)

    if not _pattern.match(component):
        _printerror("invalid component entry. must be in format \033[4m[PACKAGE_NAME]/[APP_ACIVITY_NAME]\033[0m, i.e: package.name/component.name")

    # linkmode true
    if linkmode:
        if not os.path.isfile(_svgs_folder + svg):
            _printerror(f"svg '{svg}' doesn't exist in the svgs directory.")

    # linkmode false
    else:
        if not os.path.isfile(svg):
            path = f"directory `{os.path.dirname(svg)}/`"
            print(path)
            if path == "directory `.`" or path == "directory ``":
                path = "current directory"

            _printerror(
                f"svg '{basename}' doesn't exist in {path}. check if the file exists and try again.")
        
        if os.path.isfile(_svgs_folder + basename):
            _input = input(f"\033[93mwarning\033[0m: svg \033[4m{basename}\033[0m already exists in the svgs directory. replace? (Yes/No/Link) [N] ").lower()
            if  _input == "" or _input[0] == "n":
                exit()
    
            if _input[0] == "l":
                parse_component(True, basename, component, name, showMessage)
                return
                
            if _input[0] != "y": exit()
            
            print(f"replacing {basename} in svgs folder...")

        svg_in_svg_folder = _svgs_folder + basename

        try:
            shutil.copyfile(svg, svg_in_svg_folder)
        except shutil.SameFileError:
            _printerror(
                f"\033[4m{basename}\033[0m has the same contents of \033[4m{svg_in_svg_folder}\033[0m. can not continue")

    #
    # writing to file
    #

    # remove .svg extension
    drawable = basename[:-4]

    # xmlfile        the actual xml file
    # line           the actual xml element in string form
    # purexmlfile    the xml file without the calendar declarations
    xmlfile = open(_appfilter, "r").read()

    line = f'  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />'
    purexmlfile = re.sub(
        _calendarregex, "", xmlfile
    )

    # add the line
    editedxmlfile = purexmlfile[:52] + line + purexmlfile[52:]

    # sort
    sortedxmldata = _sort_components(editedxmlfile)

    f = open(_appfilter, "w")
    f.write(sortedxmldata)
    f.close()

    #
    # print success message
    #
    with open(_appfilter) as file:
        lines = file.readlines()

    for number, line in enumerate(lines, 1):
        if component in line:
            if linkmode:
                action = "linked"
            else:
                action = "added"
            print(
                f"{action} \033[92m{name}\033[0m app to \033[92m`@drawable/{drawable}`\033[0m in line \033[92m{number}\033[0m of appfilter.xml"
            )

            if showMessage:
                if linkmode:
                    print(
                        f"* {name} (linked `{component}` to `@drawable/{drawable}`)"
                    )
                    continue

                print(
                    f"* {name} (`{component}`)"
                )

def remove_component(component, doDelete, message):
    pattern1 = re.compile(r"([A-Za-z0-9_]+(\.[A-Za-z0-9_]+)+)", re.IGNORECASE)
    pattern2 = _pattern
    errormsg = "invalid component name. format must be either \033[4m[PACKAGE_NAME]\033[0m or \033[4m[PACKAGE_NAME]/[APP_ACIVITY_NAME]\033[0m"

    if not pattern1.match(component):
        _printerror(errormsg)
    elif not pattern2.match(component):
        if ("/" in component):
            _printerror(errormsg)

    with open(_appfilter, "r") as file:
        lines = file.readlines()

    with open(_appfilter, "w") as f:
        for linenumber, line in enumerate(lines, 1):
            if component not in line:
                f.write(line)

            elif component in line:
                deletedline = line
                number = linenumber
                print(
                    f"removed \033[92m{component}\033[0m icon in line \033[92m{number}\033[0m"
                )

                if message:
                    print(f"* {component} (removed)")

    if doDelete:
        deletedfile = ET.fromstring(deletedline).get("drawable") + ".svg"
        os.remove(_svgs_folder + deletedfile)
        print(f"deleted \033[92m{deletedfile}\033[0m")

# parser logic
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
# parser initialization
#
parser = argparse.ArgumentParser(
    prog="icontool", description="a cli tool to help contributors with adding icons. requires the use of subcommands {add,link,remove,sort,find}. for help with a specific subcommand, type 'icontool.py <subcommand> -h'")
parser.add_argument(
    "-m", "--message", action="store_true", help="shows a list item for use in a pull request")

subparsers = parser.add_subparsers(
    help='what action icontool should use', dest="subcommand")

parser_add = subparsers.add_parser(
    "add", help='adds an icon svg and an entry to appfilter.xml, links svg to component', aliases=['a'])
parser_link = subparsers.add_parser(
    "link", help='adds an entry to appfilter.xml, links svg to component', aliases=['l'])
parser_remove = subparsers.add_parser(
    "remove", help='removes an entry on appfilter.xml, can optionally delete svg', aliases=['d', 'r'])
parser_sort = subparsers.add_parser(
    "sort", help='sorts the appfilter.xml file by component name', aliases=['s'])
parser_find = subparsers.add_parser(
    "find", help='find either duplicates in appfilter.xml or unlinked svgs', aliases=['f'])

# remove parser
parser_remove.add_argument(
    "component", help="the component to remove. can be [PACKAGE_NAME]/[APP_ACIVITY_NAME] or [PACKAGE_NAME]")
parser_remove.add_argument(
    "-d", "--delete", help="enables svg deletion. svg derived from `drawable` entry in each `<item>`", action="store_true")

# add parser
parser_add.add_argument(
    "svg", help="the path of the svg file that will be added")
parser_add.add_argument(
    "component", help="the component name. format must be `[PACKAGE_NAME]/[APP_ACIVITY_NAME]")
parser_add.add_argument(
    "name", help="the displayed name of the app. if multiple lines, use a string like ``\"App Name\"`")

# link parser
parser_link.add_argument("svg", help="the file name of the svg icon.")
parser_link.add_argument(
    "component", help="the component name. format must be `[PACKAGE_NAME]/[APP_ACIVITY_NAME]")
parser_link.add_argument(
    "name", help="the displayed name of the app. if multiple lines, use a string like ``\"App Name\"`")

# no additional args for `sort` parser

# find parser
parser_find.add_argument("mode", help="the mode to use {duplicates, unused}.`duplicates` = find duplicates in appfilter.xml.`unused` = find unused files in svgs/")

args = parser.parse_args()

#
# Main Functions
#
check_lawnicons_corruption()

# match proper subcommand
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
        _printerror("you must specify a subcommand {add,link,remove,sort,find}")

printsuccess()
