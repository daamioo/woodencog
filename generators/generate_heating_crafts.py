import json
import os
from .paths import tfc_paths, heating_path

def generate_heating_crafts():
    """Generates smelting and smoking recipes from TFC heating recipes."""
    heating_crafts = [f for f in os.listdir(os.path.join(tfc_paths, 'heating')) if f.endswith('.json')]
    for file in heating_crafts:
        with open(os.path.join(tfc_paths, 'heating', file)) as f:
            json_data = json.load(f)
        
        is_smoking = json_data.get('temperature', 300) <= 200
        ingredient = json_data.get('ingredient', {}).get('ingredient', {}).get('item') or json_data.get('ingredient', {}).get('item')
        result = json_data.get('result_item', {}).get('stack', {}).get('item') or json_data.get('result_item', {}).get('item')

        if not ingredient or not result:
            continue

        data = {
            'type': 'minecraft:smoking' if is_smoking else 'minecraft:smelting',
            'ingredient': {
                'item': ingredient
            },
            'result': result,
            'experience': 0.0,
            'cookingtime': 200
        }

        with open(os.path.join(heating_path, f'{file}.json'), 'w') as f:
            json.dump(data, f, indent=4)
