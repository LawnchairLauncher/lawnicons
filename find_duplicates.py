from xml.etree.ElementTree import parse

root = parse("app/src/main/res/xml/grayscale_icon_map.xml").getroot()
packages = [i.attrib["package"] for i in root]
duplicates = {i for i in packages if packages.count(i) > 1}
print(duplicates)
