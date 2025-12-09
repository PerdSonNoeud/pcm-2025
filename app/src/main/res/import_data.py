from xml.dom import minidom
import xml.etree.ElementTree as ET
import sys
import time

import requests


def loading_bar(iteration, total, length=30, flavour=""):
    """
    function that create loading bar in the console.
    Might need it later
    """
    percent = iteration / total
    bar = "━" * int(length * percent) + "-" * (length - int(length * percent))
    timer = time.time() - start
    sys.stdout.write(f"\r{timer:.2f}s - [{bar}] {percent * 100:.1f}% {flavour if flavour != '' else ' '*10}")
    sys.stdout.flush()
    time.sleep(0.01)


if __name__ == "__main__":
    start = time.time()

    pokeapi = "https://pokeapi.co/api/v2/pokemon-species/"
    total = requests.get(pokeapi).json()["count"]
    loading_bar(1, total + 1)

    root = ET.Element("resources")
    str_arr = ET.SubElement(root, "string-array", {"name": "pokemons"})

    n = 0
    for i in range(1, total + 1):
        json_data = requests.get(f"{pokeapi}{i}/").json()["names"]
        for i in range(len(json_data)):
            if json_data[i]["language"]["name"] == "fr":
                fr = json_data[i]["name"]
                break
        else:
            fr = "Unknown"

        ET.SubElement(str_arr, "item", {"id": str(n + 1)}).text = fr

        n += 1
        num = f"{'0' if n < 100 else ''}{'0' if n < 10 else ''}{n}"
        loading_bar(n, total, flavour=f"- {num}. {fr}" + " "*10)

    xml_bytes = ET.tostring(root, encoding="utf-8")
    pretty = minidom.parseString(xml_bytes).toprettyxml(indent="    ")

    with open("values/output.xml", "w", encoding="utf-8") as f:
        f.write(pretty)

    print(f"\n{n} pokémon loaded.")
