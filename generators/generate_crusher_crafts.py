import json
import os
from .paths import tfc_paths, crushing_path

def generate_crusher_crafts():
    """Generates crushing and milling recipes from TFC quern recipes."""
    crushing_crafts = [f for f in os.listdir(os.path.join(tfc_paths, 'quern')) if f.endswith('.json')]
    for file in crushing_crafts:
        with open(os.path.join(tfc_paths, 'quern', file)) as f:
            json_data = json.load(f)
        
        is_tag = False
        ingredient = json_data.get('ingredient', {}).get('item')
        if not ingredient:
            ingredient = json_data.get('ingredient', {}).get('ingredient', {}).get('item')
        if not ingredient:
            ingredient = json_data.get('ingredient', {}).get('tag')
            is_tag = True
        
        result = json_data.get('result', {}).get('item')
        if not result:
            result = json_data.get('result', {}).get('stack', {}).get('item')
            
        quantity = json_data.get('result', {}).get('count', 1)

        if not ingredient or not result:
            continue

        data_crushing = {
            'type': 'create:crushing',
            'ingredients': [
                {'tag': ingredient} if is_tag else {'item': ingredient}
            ],
            'results': [{'item': result}] * quantity,
            'processingTime': 400
        }

        with open(os.path.join(crushing_path, f'crushing_{file}'), 'w') as f:
            json.dump(data_crushing, f, indent=4)

        data_milling = {
            'type': 'create:milling',
            'ingredients': [
                {'tag': ingredient} if is_tag else {'item': ingredient}
            ],
            'results': [{'item': result}] * quantity,
            'processingTime': 400
        }

        with open(os.path.join(crushing_path, f'milling_{file}'), 'w') as f:
            json.dump(data_milling, f, indent=4)
