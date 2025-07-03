import json
import os
from .paths import tfc_paths, cutting_path

def generate_chisel_crafts():
    """Generates cutting recipes from TFC chisel recipes."""
    for type in ['slab', 'smooth', 'stair']:
        chisel_crafts = [f for f in os.listdir(os.path.join(tfc_paths, 'chisel', type)) if f.endswith('.json')]
        for file in chisel_crafts:
            with open(os.path.join(tfc_paths, 'chisel', type, file)) as f:
                json_data = json.load(f)
            
            craft = {
                'type': 'create:cutting',
                'ingredients': [
                    {
                        'item': json_data['ingredient']
                    }
                ],
                'results': [
                    {
                        'item': json_data['result']
                    }
                ]
            }

            if json_data.get('extra_drop', {}).get('item'):
                craft['results'].append({'item': json_data['extra_drop']['item']})

            with open(os.path.join(cutting_path, type, file), 'w') as f:
                json.dump(craft, f, indent=4)
