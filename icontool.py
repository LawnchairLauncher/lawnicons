import xml.etree.ElementTree as ET
import argparse
import shutil
import os
import re

parser = argparse.ArgumentParser(
    prog="icon tool",
    description="A cli tool to help contributors with adding icons",
)

parser.add_argument(
    "-s", "--svg", help="Path to the svg", metavar='"svg path"', required=False
)
parser.add_argument(
    "-c",
    "--component",
    help="Component information",
    metavar="[PACKAGE_NAME]/[APP_ACIVITY_NAME]",
    required=True,
)
parser.add_argument(
    "-n", "--name", help="App name", metavar='"App name"', required=True
)
parser.add_argument(
    "-l", "--link", help="Icon to link", metavar='"icon name"', required=False
)

# parse args
args = parser.parse_args()

# open the appfilter file
appfilter = "app/assets/appfilter.xml"
xmlfile = open(appfilter, "r").read()

def printerror(msg):
    print("\033[91mError:\033[0m " + msg)

# Check if it's an addition or a link
if (args.svg != None) != (args.link != None):
    if args.svg != None:
        linkmode = False
    else:
        linkmode = True
else:
    printerror("you must specify either adding an icon (-s) or linking (-l)")
    exit()

if linkmode == False:
    addedsvg = "svgs/" + os.path.basename(args.svg)
    # add drawable name to args
    vars(args)["drawable"] = os.path.basename(args.svg[:-4])
    # check if the svg exists, exits if it doesn't
    if os.path.isfile(args.svg) == False:
        printerror("svg doesn't exist")
        exit()
else:
    if args.link.endswith(".svg"):
        vars(args)["drawable"] = args.link[:-4]
    else:
        vars(args)["drawable"] = args.link

# check if the entry exists
if args.component in xmlfile:
    printerror("entry already exists")
    exit()

# check if the svg exists in the svg directory
if linkmode == False:
    if os.path.isfile(addedsvg) == True:
        printerror("svg exists in the svg directory")
        exit()

# check if svg exists in svgs when linking
if linkmode == True:
    if os.path.isfile("svgs/" + args.drawable + ".svg") == False:
        printerror("svg doesn't exist in the svg directory")
        exit()

# add the svg to the svg directory
if linkmode == False:
    shutil.copyfile(args.svg, addedsvg)

# generate the line
line = f'  <item component="ComponentInfo{{{args.component}}}" drawable="{args.drawable}" name="{args.name}" />'

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


def sortchildrenby(parent, attr):
    parent[:] = sorted(parent, key=lambda child: child.get(attr))


sortchildrenby(xmldata, "name")

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
        if linkmode == False:
            action = "added"
        else:
            action = "linked"
        print(
            f"{action} \033[92m{args.name}\033[0m icon to appfilter.xml in line \033[92m{number}\033[0m"
        )

# todo: command to remove existing entry/svg from the folder
