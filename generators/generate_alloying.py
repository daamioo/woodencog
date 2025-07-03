
import json
import os
from .data import heated_alloys
from .paths import heated_mixing_path

def generate_alloying():
    """Generates heated mixing recipes for alloys."""
    for alloy in heated_alloys:
        craft = {
            "type": "woodencog:heated_mixing",
            "ingredients": alloy["input"],
            "results": [
                {
                    "fluid": alloy["result"],
                    "nbt": {},
                    "amount": 100
                }
            ],
            "heatRequirement": alloy["temp"],
            "processingTime": 400
        }
        
        with open(os.path.join(heated_mixing_path, f'create_mixing_alloying_{alloy["name"]}.json'), 'w') as f:
            json.dump(craft, f, indent=4)
