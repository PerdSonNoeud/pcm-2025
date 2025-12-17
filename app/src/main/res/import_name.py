import requests
import csv
import time
from pathlib import Path

pokeapi_species = "https://pokeapi.co/api/v2/pokemon-species/"
pokeapi_pokemon = "https://pokeapi.co/api/v2/pokemon/"

Path("output").mkdir(exist_ok=True)
csv_file = Path("output/pokemons_stats.csv")

# Récupérer le nombre total de Pokémon
total = requests.get(pokeapi_species).json()["count"]
print(f"Total Pokémon : {total}")

with open(csv_file, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["id","name","hp","attack","defense","special-attack","special-defense","speed"])

    for i in range(1, total+1):
        try:
            # Pokémon species pour nom français
            species_data = requests.get(f"{pokeapi_species}{i}/").json()
            fr_name = next(
                (item["name"] for item in species_data["names"] if item["language"]["name"] == "fr"),
                "Unknown"
            )

            # Pokémon data pour stats
            poke_data = requests.get(f"{pokeapi_pokemon}{i}/").json()
            stats = {stat["stat"]["name"]: stat["base_stat"] for stat in poke_data["stats"]}

            # Récupérer les stats brutes
            base_stats = [stats.get(stat,0) for stat in
                          ["hp","attack","defense","special-attack","special-defense","speed"]]

            writer.writerow([i, fr_name, *base_stats])
            print(f"{i}: {fr_name} - {base_stats}")

            time.sleep(0.1)  # pause pour ne pas surcharger l'API

        except Exception as e:
            print(f"Erreur pour Pokémon {i}: {e}")
            writer.writerow([i, "Unknown", 0,0,0,0,0,0])

