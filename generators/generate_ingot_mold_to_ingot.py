import json
import os
from .paths import deploying_path

def generate_ingot_mold_to_ingot(name):
    """Generates a deploying recipe to get an ingot from a filled ingot mold."""
    data = {
        'type': 'create:deploying',
        'ingredients': [
            {
                'type': 'tfc:heatable',
                'max_temp': 200,
                'ingredient': {
                    'type': 'forge:nbt',
                    'item': 'tfc:ceramic/ingot_mold',
                    'nbt': {
                        'tank': {
                            'Amount': 100,
                            'FluidName': f'tfc:metal/{name}'
                        }
                    }
                }
            },
            {
                'tag': 'tfc:chisels'
            }
        ],
        'results': [
            {
                'item': f'tfc:metal/ingot/{name}'
            },
            {
                'item': 'tfc:ceramic/ingot_mold',
                'chance': 0.75
            }
        ]
    }
    with open(os.path.join(deploying_path, f'mold_to_ingot_{name}.json'), 'w') as f:
        json.dump(data, f, indent=4)
