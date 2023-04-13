eximport xml.etree.ElementTree as ET
import argparse
import shutil
import os
import re

parser = argparse.ArgumentParser(
    prog="icontool",
    description="A cli tool to help contributors with adding, linking, or removing icons",
)
parser.add_argument(
    "-m", "--message",
    help="Enable generating a message to use in PR.",
    action="store_true"
)

#
#https://docs.python.org/3/library/argparse.html#sub-commands
subparser = parser.add_subparsers(help='sub-command help', metavar=[
"a","v","c"
])

# Add parser
parser_add = subparser.add_parser(
    "add",
    help="Adds an svg and links it to an app. You must specify the path of the said svg.",
)

parser_add.add_argument(
    "-s", "--svg", "-p", "--path",
    help="Path of the said svg.",
    metavar="\"path/to/icon.svg\"",
)

parser_add.add_argument(
    "-c", "--component",
    help="Information about the said app, i.e the package and activity name.",
    metavar="[PACKAGE_NAME]/[APP_ACIVITY_NAME]",
)

parser_add.add_argument(
    "-n",
    "--name",
    help="The name of the app.",
    metavar='"App name"',
)

# Link parser
parser_link = subparser.add_parser(
    "link",
    help="Links an svg from the svgs/ folder to an app.",
)

parser_link.add_argument(
    "-s", "--svg",
    help="The name of the svg file.",
    metavar="\"icon.svg\"",
)

parser_link.add_argument(
    "-c", "--component",
    help="Information about the said app, i.e the package and activity name.",
    metavar="[PACKAGE_NAME]/[APP_ACIVITY_NAME]",
)

parser_link.add_argument(
    "-n",
    "--name",
    help="The name of the app.",
    metavar='"App name"',
)

# Remove parser
parser_remove = subparser.add_parser(
    "remove",
    help="Removes the link of an svg to an app.",
    
)
parser_remove.add_argument(
    "-c", "--component",
    help="The component to remove.",
    metavar="[PACKAGE_NAME] or [PACKAGE_NAME]/[APP_ACIVITY_NAME]",
)
parser_remove.add_argument(
    "-d", "--delete-file",
    help="Delete the said icon file on the svgs/ folder."
)

# open the appfilter file
appfilter = "app/assets/appfilter.xml"
xmlfile = open(appfilter, "r").read()

# parse args


add_args = parser_add.parse_args()
link_args = parser_link.parse_args()
rm_args = parser_remove.parse_args()

# Helper functions
def printerror(msg):
    print("\033[91mError:\033[0m " + msg + "\n")
    exit()

def p_add():
    print('a')


def p_link():
    print('b')

def p_rm():
    print('c')


parser_add.set_defaults(func=p_add)
parser_link.set_defaults(func=p_link)
parser_remove.set_defaults(func=p_rm)

"""
with open(appfilter, "r") as file:
        lines = file.readlines()
with open(appfilter, "w") as f:
for linenumber, line in enumerate(lines, 1):
    if args.remove not in line:
        f.write(line)
    elif args.remove in line:
        deletedline = line
        number = linenumber
        print(
            f"removed \033[92m{args.remove}\033[0m icon in line \033[92m{number}\033[0m"
        )
if args.delete:
deletedfile = ET.fromstring(deletedline).get("drawable") + ".svg"
os.remove("svgs/" + deletedfile)
print(f"deleted \033[92m{deletedfile}\033[0m")
exit()




# removing an icon from appfilter.xml
if args.remove:
    # check for unnecessary arguments
    for i in [
        [args.svg, "svg"],
        [args.link, "link"],
        [args.component, "component"],
        [args.name, "name"],
    ]:
        if i[0]:
            if i[1][0] == "s":
                errormsgarticle = "an "
            else:
                errormsgarticle = "a "
            printerror(
                "don't specify " + errormsgarticle + i[1] + " when removing an icon"
            )
    # remove the line
    with open(appfilter, "r") as file:
        lines = file.readlines()
    with open(appfilter, "w") as f:
        for linenumber, line in enumerate(lines, 1):
            if args.remove not in line:
                f.write(line)
            elif args.remove in line:
                deletedline = line
                number = linenumber
                print(
                    f"removed \033[92m{args.remove}\033[0m icon in line \033[92m{number}\033[0m"
                )
    # delete the icon when asked to
    if args.delete:
        deletedfile = ET.fromstring(deletedline).get("drawable") + ".svg"
        os.remove("svgs/" + deletedfile)
        print(f"deleted \033[92m{deletedfile}\033[0m")
    exit()

# check if it's an addition or a link
if args.svg != args.link:
    if args.svg != None:
        linkmode = False
    else:
        linkmode = True
else:
    printerror("you must specify either adding an icon (-s) or linking (-l)")


# check if the component and name are specified
for i in [[args.component, "component (-c)"], [args.name, "name (-n)"]]:
    if i[0] == None:
        printerror("you must specify a " + i[1])

if not linkmode:
    addedsvg = "svgs/" + os.path.basename(args.svg)
    # add drawable name to args
    vars(args)["drawable"] = os.path.basename(args.svg[:-4])
    # check if the svg exists, exits if it doesn't
    if os.path.isfile(args.svg) == False:
        printerror("svg doesn't exist")

else:
    if args.link.endswith(".svg"):
        vars(args)["drawable"] = args.link[:-4]
    else:
        vars(args)["drawable"] = args.link

# check if the entry exists
if args.component in xmlfile:
    printerror("entry already exists")

# check if the svg exists in the svg directory
if not linkmode:
    if os.path.isfile(addedsvg) == True:
        printerror("svg exists in the svg directory")

# check if svg exists in svgs when linking
if linkmode == True:
    if os.path.isfile("svgs/" + args.drawable + ".svg") == False:
        printerror("svg doesn't exist in the svg directory")

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


#def sortchildrenby(parent, attr):
#    parent[:] = sorted(parent, key=lambda child: child.get(attr))

#sortchildrenby(xmldata, "name")
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
        if linkmode == False:
            action = "added"
        else:
            action = "linked"
        print(
            f"{action} \033[92m{args.name}\033[0m icon to appfilter.xml in line \033[92m{number}\033[0m"
        )
        if args.message == True:
            if not linkmode:
                print(
                    f"* {args.name} (`{args.component}`)"
                )
            elif linkmode:
                print(
                    f"* {args.name} (linked `{args.component}` to `@drawable/{args.drawable}`)"
                )"""
