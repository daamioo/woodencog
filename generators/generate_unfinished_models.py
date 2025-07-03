import json
import os
from .paths import assets_path
from .data import metals, moldables

def generate_unfinished_models():
    """Generates item model files for unfinished metal items."""
    item_types = [
        "ingot", "double_ingot", "double_sheet", "fishing_rod", "pickaxe", "propick",
        "axe", "shovel", "hoe", "chisel", "hammer", "saw", "javelin", "sword",
        "mace", "knife", "scythe", "shears", "helmet", "chestplate", "greaves",
        "boots", "horse_armor"
    ]

    for metal in metals:
        for item_type in item_types:
            model_path = os.path.join(assets_path, 'woodencog', 'models', 'item', 'metal', item_type, metal['name'])
            os.makedirs(model_path, exist_ok=True)
            model_file = os.path.join(model_path, 'unfinished.json')

            if not os.path.exists(model_file):
                model_content = {
                    "parent": "tfc:item/template_unfinished_item"
                }
                with open(model_file, 'w') as f:
                    json.dump(model_content, f, indent=4)
