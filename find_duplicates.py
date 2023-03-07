from xml.etree.ElementTree import parse

root = parse("app/assets/appfilter.xml").getroot()
packages = [i.attrib["component"] for i in root]
duplicates = {i for i in packages if packages.count(i) > 1}

for i in duplicates:
    print(i)