#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import argparse
import shutil
import os
import re

# helper functions
def printerror(msg):
    print("\033[91merror:\033[0m " + msg + "\n")
    exit()


def printsuccess():
    print("\033[96msuccessfully completed task\033")
    exit()


def check_lawnicons_corruption():
    replyreason = "this may be due to a broken local copy of lawnicons. please clone lawnicons again."

    if not os.path.exists("svgs/"):
        printerror(f"svgs folder does not exist. {replyreason}")

    if not os.path.isfile("app/assets/appfilter.xml"):
        printerror(f"appfilter.xml file does not exist. {replyreason}")


def parse_component(linkmode, svg, component, name, showMessage):
    #
    # init
    #

    # general checks
    if not svg.endswith(".svg"):
        svg += ".svg"
    
    # see https://regex101.com/r/xC9Kh3/1
    pattern = re.compile(r"([A-Za-z0-9]+(\.[A-Za-z0-9]+)+)\/([A-Za-z0-9]+(\.[A-Za-z0-9]+)+)", re.IGNORECASE)
    if not pattern.match(component):
        printerror("invalid component entry. must be in format \033[4m[PACKAGE_NAME]/[APP_ACIVITY_NAME]\033[0m, i.e: package.name/component.name")

    # linkmode true
    if linkmode:
        if not os.path.isfile(f"svgs/{svg}"):
            printerror(f"svg '{svg}' doesn't exist in the svgs directory.")

    # linkmode false
    else:
        if not os.path.isfile(svg):
            path = f"directory `{os.path.dirname(svg)}/`"
            print(path)
            if path == "directory `.`" or path == "directory ``":
                path = "current directory"

            printerror(
                f"svg '{os.path.basename(svg)}' doesn't exist in {path}. check if the file exists or try again.")

        print("note: ensure that your icon follows the lawnicons guidelines found in CONTRIBUTING.md")

        addedsvg = f"svgs/{os.path.basename(svg)}"
        try:
            shutil.copyfile(svg, addedsvg)
        except shutil.SameFileError:
            printerror(
                f"\033[4m{os.path.basename(svg)}\033[0m has the same contents of \033[4m{addedsvg}\033[0m. ensure that you actually saved your changes in \033[4m{svg}\033[0m")

    #
    # writing to file
    #

    # remove .svg extension
    drawable = svg[:-4]

    # line           the actual xml element in string form
    # purexmlfile    the xml file without the calendar declarations
    # calendarstuff  the lines of the calendar declerations
    line = f'  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />'
    purexmlfile = re.sub(
        r"(?s)  <!--  Dynamic Calendars-->.*?  <!--  Lawnicons -->", "", xmlfile
    )
    calendarstuff = re.findall(
        "(?s)  <!--  Dynamic Calendars-->.*?  <!--  Lawnicons -->", xmlfile
    )

    # add the line
    editedxmlfile = purexmlfile[:52] + line + purexmlfile[52:]

    # sort the xml by the name tag, thx https://stackoverflow.com/a/25339725
    xmldata = ET.fromstring(editedxmlfile)
    xmldata[:] = sorted(
        xmldata, key=lambda child: child.get("name").casefold())

    sortedxmldata = ET.tostring(xmldata, encoding="unicode")
    sortedxmldata = (
        '<?xml version="1.0" encoding="UTF-8"?>\n\n' + sortedxmldata)
    sortedxmldata = (
        sortedxmldata[:52] + calendarstuff[0] +
        "\n" + sortedxmldata[52:] + "\n")

    f = open(appfilter, "w")
    f.write(sortedxmldata)
    f.close()

    #
    # print success message
    #
    with open(appfilter) as file:
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


# parser logic
def add_parser(args):
    parse_component(False, args.svg, args.component, args.name, args.message)
    printsuccess()


def link_parser(args):
    parse_component(True, args.svg, args.component, args.name, args.message)
    printsuccess()


def remove_parser(args, appfilter):
    component = args.component
    doDelete = args.delete
    message = args.message

    pattern1 = re.compile(r"([A-Za-z0-9]+(\.[A-Za-z0-9]+)+)", re.IGNORECASE)
    pattern2 = re.compile(r"([A-Za-z0-9]+(\.[A-Za-z0-9]+)+)\/([A-Za-z0-9]+(\.[A-Za-z0-9]+)+)", re.IGNORECASE)
    errormsg = "invalid component name. format must be either \033[4m[PACKAGE_NAME]\033[0m or \033[4m[PACKAGE_NAME]/[APP_ACIVITY_NAME]\033[0m"

    if not pattern1.match(component):
        printerror(errormsg)
    elif not pattern2.match(component):
        if ("/" in component):
            printerror(errormsg)

    with open(appfilter, "r") as file:
        lines = file.readlines()

    with open(appfilter, "w") as f:
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
        os.remove("svgs/" + deletedfile)
        print(f"deleted \033[92m{deletedfile}\033[0m")

    printsuccess()

#
# parser initialization
#
parser = argparse.ArgumentParser(
    prog="icontool", description="a cli tool to help contributors with adding icons. requires the use of subcommands {add,link,remove}. for help with a specific subcommand, type 'icontool.py <subcommand> -h'")
parser.add_argument(
    "-m", "--message", action="store_true", help="shows a list item for use in a pull request")

subparsers = parser.add_subparsers(
    help='what action icontool should use', dest="subcommand")

parser_add = subparsers.add_parser(
    "add", help='adds an icon svg and an entry to appfilter.xml, links svg to component', aliases=['a'])
parser_link = subparsers.add_parser(
    "link", help='adds an entry to appfilter.xml, links svg to component', aliases=['l'])
parser_remove = subparsers.add_parser(
    "remove", help='removes an entry on appfilter.xml, can optionally delete svg', aliases=['d'])

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

args = parser.parse_args()

check_lawnicons_corruption()

# open appfilter file
appfilter = "app/assets/appfilter.xml"
xmlfile = open(appfilter, "r").read()

# match proper subcommand
match args.subcommand:
    case "add":
        add_parser(args)

    case "link":
        link_parser(args)

    case "remove":
        remove_parser(args, appfilter)

    case _:
        printerror("you must specify a subcommand {add,link,remove}")
