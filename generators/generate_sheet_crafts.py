import json
import os
from .paths import heated_pressing_path

def generate_sheet_crafts(name, working):
    """Generates a heated pressing recipe to create a sheet from a double ingot."""
    data = {
        'type': 'woodencog:heated_pressing',
        'ingredients': [
            {
                'ingredient': { 'item': f'tfc:metal/double_ingot/{name}' },
                'min_temp': working+working*0.1,
                'max_temp': 3000
            }
        ],
        'results': [
            {
              'item': f'tfc:metal/sheet/{name}',
              'temperature': 0,
              'copy_heat': True,
              'cooling': 0
            }
        ]
    }
    with open(os.path.join(heated_pressing_path, f'sheet_{name}.json'), 'w') as f:
        json.dump(data, f, indent=4)
