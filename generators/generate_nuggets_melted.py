import json
import os
from .paths import heated_mixing_path

def generate_nuggets_melted(name, result, min_temp):
    """Generates heated mixing recipes to melt nuggets into fluid."""
    types = [
        {'type': 'small', 'quantity': 10},
        {'type': 'poor', 'quantity': 15},
        {'type': 'normal', 'quantity': 25},
        {'type': 'rich', 'quantity': 35},
    ]
    for type_info in types:
        data = {
            'type': 'woodencog:heated_mixing',
            'ingredients': [
                {
                  'ingredient': { 'item': f'tfc:ore/{type_info["type"]}_{name}' },
                  'min_temp': min_temp,
                  'max_temp': 3000
                }
            ],
            'results': [
                {
                    'fluid': f'tfc:metal/{result}',
                    'nbt': {},
                    'amount': type_info['quantity']
                }
            ],
            'heatRequirement': min_temp
        }
        with open(os.path.join(heated_mixing_path, f'nugget_{type_info["type"]}_to_liquid_{name}.json'), 'w') as f:
            json.dump(data, f, indent=4)
