from xml.dom import minidom
import xml.etree.ElementTree as ET
import sys
import time
from pathlib import Path

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

    # Used to get the name of the pokemon and the total count
    pokeapi_species = "https://pokeapi.co/api/v2/pokemon-species/"
    # Used to get the sprite and the cries of the pokemon
    pokeapi_pokemon = "https://pokeapi.co/api/v2/pokemon/"
    # Total count of pokemon
    total = requests.get(pokeapi_species).json()["count"]
    loading_bar(1, total + 1)

    # Start of the XML file
    root = ET.Element("resources")
    pokedex_arr = ET.SubElement(root, "string-array", {"name": "pokemons"})

    Path("cries").mkdir(parents=True, exist_ok=True)
    Path("sprites").mkdir(parents=True, exist_ok=True)

    n = 0
    for i in range(1, total + 1):
        # Get the name of the pokemon
        json_data = requests.get(f"{pokeapi_species}{i}/").json()["names"]
        # Check for the name in French
        for j in range(len(json_data)):
            if json_data[j]["language"]["name"] == "fr":
                fr = json_data[j]["name"]
                break
        else:
            # Not found
            fr = "Unknown"

        # Add it to the XML
        ET.SubElement(pokedex_arr, "item").text = fr

        json_data = requests.get(f"{pokeapi_pokemon}{i}/").json()
        # Get the cries
        cries_url = json_data["cries"]["latest"]

        try:
            cries = requests.get(cries_url, stream=True)
            cries.raise_for_status()  # raise error if download failed

            with open(f"./cries/{i}.ogg", "wb") as f:
                for chunk in cries.iter_content(chunk_size=8192):
                    f.write(chunk)
        except Exception as e:
            print(e)

        # Get the sprite
        sprites_url = json_data["sprites"]["front_default"]

        try:
            sprites = requests.get(sprites_url, stream=True)
            sprites.raise_for_status()  # raise error if download failed

            with open(f"./sprites/{i}.png", "wb") as f:
                for chunk in sprites.iter_content(chunk_size=8192):
                    f.write(chunk)
        except Exception as e:
            print(e)

        n += 1
        num = f"{'0' if n < 100 else ''}{'0' if n < 10 else ''}{n}"
        loading_bar(n, total, flavour=f"- {num}. {fr}" + " "*10)

    # Save file
    xml_bytes = ET.tostring(root, encoding="utf-8")
    pretty = minidom.parseString(xml_bytes).toprettyxml(indent="    ")

    with open("./values/output.xml", "w", encoding="utf-8") as f:
        f.write(pretty)

    print(f"\n{n} pokémon loaded.")
