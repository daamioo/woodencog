import json
import os
from .data import moldable_metals, moldables
from .paths import deploying_path

def generate_mold_to_item():
    """Generates deploying recipes to get items from filled molds."""
    for metal in moldable_metals:
        for moldable in moldables:
            craft = {
                'type': 'create:deploying',
                'ingredients': [
                    {
                        'type': 'tfc:heatable',
                        'max_temp': 200,
                        'ingredient': {
                            'type': 'forge:nbt',
                            'item': f'tfc:ceramic/{moldable["name"]}_mold',
                            'nbt': {
                                'tank': {
                                    'Amount': moldable['unit'],
                                    'FluidName': f'tfc:metal/{metal}'
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
                        'item': f'tfc:metal/{moldable["name"]}/{metal}'
                    },
                    {
                        'item': f'tfc:ceramic/{moldable["name"]}_mold',
                        'chance': 0.75
                    }
                ]
            }
            with open(os.path.join(deploying_path, f'{moldable["name"]}_mold_of_{metal}_to_item.json'), 'w') as f:
                json.dump(craft, f, indent=4)
