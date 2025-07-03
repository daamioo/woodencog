import json
import os
from .paths import heated_compacting_path

def generate_ingots_welding(name, welding):
    """Generates a heated compacting recipe to weld two ingots into a double ingot."""
    data = {
        'type': 'woodencog:heated_compacting',
        'ingredients': [
            {
                'ingredient': { 'item': f'tfc:metal/ingot/{name}' },
                'min_temp': welding+welding*0.1,
                'max_temp': 3000
            },
            {
                'ingredient': { 'item': f'tfc:metal/ingot/{name}' },
                'min_temp': welding+welding*0.1,
                'max_temp': 3000
            },
            {
                'ingredient': { 'item': 'tfc:powder/flux' },
                'min_temp': 0,
                'max_temp': 3000
            }
        ],
        'results': [
            {
              'item': f'tfc:metal/double_ingot/{name}',
              'temperature': 0,
              'copy_heat': True,
              'cooling': 0
            }
        ],
        'heatRequirement': welding+welding*0.1
    }
    with open(os.path.join(heated_compacting_path, f'double_{name}.json'), 'w') as f:
        json.dump(data, f, indent=4)
