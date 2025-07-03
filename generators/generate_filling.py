import json
import os
from .data import moldable_metals, moldables, metals
from .paths import advanced_filling_path

def generate_filling():
    """Generates filling recipes for ingot and item molds."""
    for metal in metals:
        craft = {
            'type': 'woodencog:filling',
            'ingredients': [
                {
                    'item': 'tfc:ceramic/ingot_mold'
                },
                {
                    'fluid': f'tfc:metal/{metal["name"]}',
                    'nbt': {},
                    'amount': 100
                }
            ],
            'results': [
                {
                    'stack': {
                        'item': 'tfc:ceramic/ingot_mold',
                        'count': 1
                    }
                }
            ]
        }
        with open(os.path.join(advanced_filling_path, f'{metal["name"]}_to_mold.json'), 'w') as f:
            json.dump(craft, f, indent=4)

    for metal in moldable_metals:
        for moldable in moldables:
            craft = {
                'type': 'woodencog:filling',
                'ingredients': [
                    {
                        'item': f'tfc:ceramic/{moldable["name"]}_mold'
                    },
                    {
                        'fluid': f'tfc:metal/{metal}',
                        'nbt': {},
                        'amount': moldable['unit']
                    }
                ],
                'results': [
                    {
                        'stack': {
                            'item': f'tfc:ceramic/{moldable["name"]}_mold',
                            'count': 1
                        }
                    }
                ]
            }
            with open(os.path.join(advanced_filling_path, f'{metal}_to_{moldable["name"]}_mold.json'), 'w') as f:
                json.dump(craft, f, indent=4)
