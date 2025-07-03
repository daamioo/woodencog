import json
import os
from .paths import tfc_paths, compacting_path, cutting_path

def generate_knapping_crafts():
    """Generates compacting and cutting recipes from TFC knapping recipes."""
    knapping_types = [
        {'name': 'clay_knapping', 'unit': 4, 'type': 'create:compacting', 'path': compacting_path},
        {'name': 'fire_clay_knapping', 'unit': 4, 'type': 'create:compacting', 'path': compacting_path},
        {'name': 'leather_knapping', 'unit': 1, 'type': 'create:cutting', 'path': cutting_path},
        {'name': 'rock_knapping', 'unit': 1, 'type': 'create:cutting', 'path': cutting_path},
    ]
    for type_info in knapping_types:
        knapping_crafts = [f for f in os.listdir(os.path.join(tfc_paths, type_info['name'])) if f.endswith('.json')]
        for file in knapping_crafts:
            with open(os.path.join(tfc_paths, type_info['name'], file)) as f:
                json_data = json.load(f)
            
            ingredients = [{'tag': f'tfc:{type_info["name"]}'}] * type_info['unit']
            
            craft = {
                'type': type_info['type'],
                'ingredients': ingredients,
                'results': [
                    json_data['result']
                ]
            }

            with open(os.path.join(type_info['path'], file), 'w') as f:
                json.dump(craft, f, indent=4)
