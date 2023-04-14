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
    print("\033[92msuccessfully completed task\033")
    exit()

def parse_component(linkmode, svg, component, name):    
    # linkmode true
    if linkmode:
        if not os.path.isfile(f"svgs/{svg}.svg"): 
            printerror("svg doesn't exist in the svg directory")
    
    # linkmode false
    else:
        if not svg.endswith(".svg"):
            svg += ".svg"

        if os.path.isfile(svg):
            printerror("svg doesn't exist")
        
        addedsvg = "svgs/" + os.path.basename(svg)
        shutil.copyfile(svg, addedsvg)
        
    print(svg)
    if svg.endswith(".svg"):
        drawable = svg[:-4]
    else:
        drawable = svg
    
    # generate the line
    line = f'  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />'

    # xml without calendar stuff
    purexmlfile = re.sub(
        r"(?s)  <!--  Dynamic Calendars-->.*?  <!--  Lawnicons -->", "", xmlfile
    )

    # calendar stuff
    calendarstuff = re.findall(
        "(?s)  <!--  Dynamic Calendars-->.*?  <!--  Lawnicons -->", xmlfile
    )

    # add the line
    editedxmlfile = purexmlfile[:52] + line + purexmlfile[52:]

    # sort the xml by the name tag, thx https://stackoverflow.com/a/25339725
    xmldata = ET.fromstring(editedxmlfile)

    xmldata[:] = sorted(xmldata, key=lambda child: child.get("name").casefold())

    sortedxmldata = ET.tostring(xmldata, encoding="unicode")
    sortedxmldata = (
        '<?xml version="1.0" encoding="UTF-8"?>\n\n' + sortedxmldata
    )  # readd XML declaration
    sortedxmldata = (
        sortedxmldata[:52] + calendarstuff[0] + "\n" + sortedxmldata[52:] + "\n"
    )  # readd the calendar stuff

    # save to the xml file
    f = open(appfilter, "w")
    f.write(sortedxmldata)
    f.close()

    # print success message
    with open(appfilter) as file:
        lines = file.readlines()

    for number, line in enumerate(lines, 1):
        if args.component in line:
            if linkmode:
                action = "linkmode"
            
            action = "linked"

            print(
                f"{action} \033[92m{name}\033[0m icon to appfilter.xml in line \033[92m{number}\033[0m"
            )

            if True:
                if linkmode:
                    print(
                        f"* {name} (linked `{component}` to `@drawable/{drawable}`)"
                    )
                
                print(
                    f"* {name} (`{component}`)"
                )
                    

# parser logic
def default_parser(args):
    if args.help:
        print("usage: icontool [-h] {add,link,remove} [...]\n\na cli tool to help contributors with adding icons. requires the use of subcommands {add,link,remove}.\n\nfor help with a specific subcommand, type 'icontool.py <subcommand> -h'")
        exit()

    printerror("you must specify a subcommand {add,link,remove}")

def add_parser(args):
    print(args.svg + args.component + args.name)
    parse_component(False, args.svg, args.component, args.name)

def link_parser(args):
    print(args.svg + args.component + args.name)
    parse_component(True, args.svg, args.component, args.name)

def remove_parser(args, appfilter):
    component = args.component
    doDelete = args.delete

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

    # delete the icon when asked to
    if doDelete:
        deletedfile = ET.fromstring(deletedline).get("drawable") + ".svg"
        os.remove("svgs/" + deletedfile)
        print(f"deleted \033[92m{deletedfile}\033[0m")
    
    printsuccess()

#
# parser init and such
#
parser = argparse.ArgumentParser(prog="icontool", add_help=False)
parser.add_argument(
    "-h", "--help",
    help="show this help message and exit",
    action="store_true",
)
parser.add_argument(
    "-m", "--message",
    help="Enable generating a message to use in pr",
    action="store_true",
)
parser.set_defaults(func=default_parser)

subparsers = parser.add_subparsers(help='sub-command help', dest="subcommand")

parser_add = subparsers.add_parser("add")
parser_link = subparsers.add_parser("link")
parser_remove = subparsers.add_parser("remove")

# remove parser
parser_remove.add_argument("component")
parser_remove.add_argument("-d", "--delete", action="store_true")

# add parser
parser_add.add_argument("svg")
parser_add.add_argument("component")
parser_add.add_argument("name")

# link parser
parser_link.add_argument("svg")
parser_link.add_argument("component")
parser_link.add_argument("name")

# parse args
args = parser.parse_args()


# debug
# print(args)

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
        default_parser(args)

