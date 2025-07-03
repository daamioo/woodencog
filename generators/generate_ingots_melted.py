import json
import os
from .paths import heated_mixing_path

def generate_ingots_melted(name, welding):
    """Generates a heated mixing recipe to melt an ingot into a fluid."""
    data = {
        'type': 'woodencog:heated_mixing',
        'ingredients': [
            {
              'ingredient': { 'item': f'tfc:metal/ingot/{name}' },
              'min_temp': welding + welding*0.2,
              'max_temp': 3000
            }
        ],
        'results': [
            {
                'fluid': f'tfc:metal/{name}',
                'nbt': {},
                'amount': 100
            }
        ],
        'heatRequirement': welding + welding*0.2
    }
    with open(os.path.join(heated_mixing_path, f'ingot_to_liquid_{name}.json'), 'w') as f:
        json.dump(data, f, indent=4)
