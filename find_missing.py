from xml.etree.ElementTree import parse
import os

root = parse("app/assets/appfilter.xml").getroot()
svgs = os.listdir("svgs/")
drawables = []
for i in root:
    drawable = str(i.attrib.get("drawable", None)) + ".svg"
    drawables.append(drawable)

for i in svgs:
    if i not in drawables:
        if not i.startswith("themed_icon_calendar_"):
            print(i)