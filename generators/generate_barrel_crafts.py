import json
import os
from .paths import tfc_paths, mixing_path

def generate_barrel_crafts():
    """Generates mixing recipes from TFC barrel recipes."""
    barrel_output_path = os.path.join(mixing_path, 'barrel')
    os.makedirs(barrel_output_path, exist_ok=True)

    tfc_barrel_path = os.path.join(tfc_paths, 'barrel')
    if not os.path.isdir(tfc_barrel_path):
        return

    barrel_crafts = [f for f in os.listdir(tfc_barrel_path) if f.endswith('.json')]
    for file in barrel_crafts:
        with open(os.path.join(tfc_barrel_path, file)) as f:
            json_data = json.load(f)

        if json_data.get('type') == 'tfc:barrel_sealed':
            input_item = json_data.get('input_item')
            input_fluid = json_data.get('input_fluid')
            output_fluid = json_data.get('output_fluid')
            output_item = json_data.get('output_item')
            duration = max(1, json_data.get('duration', 0) // 10)

            if not output_fluid and not output_item:
                continue

            ingredient_item = None
            is_item_tag = False
            if input_item:
                item_ing_spec = input_item.get('ingredient', {})
                if 'ingredient' in item_ing_spec:
                    item_ing_spec = item_ing_spec['ingredient']
                
                if 'tag' in item_ing_spec:
                    is_item_tag = True
                    ingredient_item = item_ing_spec['tag']
                elif 'item' in item_ing_spec:
                    ingredient_item = item_ing_spec['item']

            if input_item and not ingredient_item:
                continue

            ingredient_fluid = None
            is_fluid_tag = False
            if input_fluid:
                fluid_ing_spec = input_fluid.get('ingredient')
                if isinstance(fluid_ing_spec, dict) and 'tag' in fluid_ing_spec:
                    is_fluid_tag = True
                    ingredient_fluid = fluid_ing_spec['tag']
                elif isinstance(fluid_ing_spec, str):
                    ingredient_fluid = fluid_ing_spec

            if input_fluid and not ingredient_fluid:
                continue

            output_item_item = output_item.get('item') if output_item else None
            output_item_count = output_item.get('count', 1) if output_item else 1

            output_fluid_fluid = output_fluid.get('fluid') if output_fluid else None
            output_fluid_count = output_fluid.get('amount') if output_fluid else None

            if not output_fluid_fluid and not output_item_item:
                continue

            craft = {
                'type': 'create:mixing',
                'ingredients': [],
                'results': [],
                'processingTime': duration
            }

            if ingredient_item:
                craft['ingredients'].append({'tag': ingredient_item} if is_item_tag else {'item': ingredient_item})
            
            if ingredient_fluid:
                if is_fluid_tag:
                    craft['ingredients'].append({'fluidTag': ingredient_fluid, 'amount': input_fluid['amount']})
                else:
                    craft['ingredients'].append({'fluid': ingredient_fluid, 'amount': input_fluid['amount']})
            
            craft['ingredients'] = [ing for ing in craft['ingredients'] if ing]
            if not craft['ingredients']:
                continue

            if output_item_item:
                craft['results'].append({'item': output_item_item, 'count': output_item_count})
            
            if output_fluid_fluid:
                craft['results'].append({'fluid': output_fluid_fluid, 'amount': output_fluid_count})

            with open(os.path.join(barrel_output_path, file), 'w') as f:
                json.dump(craft, f, indent=4)

        elif json_data.get('type') == 'tfc:barrel_instant_fluid':
            primary_fluid = json_data.get('primary_fluid')
            added_fluid = json_data.get('added_fluid')
            output_fluid = json_data.get('output_fluid')

            craft = {
                'type': 'create:mixing',
                'ingredients': [],
                'results': [{'fluid': output_fluid['fluid'], 'amount': output_fluid['amount']}],
                'processingTime': 1
            }

            primary_ing_spec = primary_fluid.get('ingredient')
            if isinstance(primary_ing_spec, dict) and 'tag' in primary_ing_spec:
                craft['ingredients'].append({'fluidTag': primary_ing_spec['tag'], 'amount': primary_fluid['amount']})
            elif isinstance(primary_ing_spec, str):
                craft['ingredients'].append({'fluid': primary_ing_spec, 'amount': primary_fluid['amount']})

            added_ing_spec = added_fluid.get('ingredient')
            if isinstance(added_ing_spec, dict) and 'tag' in added_ing_spec:
                craft['ingredients'].append({'fluidTag': added_ing_spec['tag'], 'amount': added_fluid['amount']})
            elif isinstance(added_ing_spec, str):
                craft['ingredients'].append({'fluid': added_ing_spec, 'amount': added_fluid['amount']})

            if len(craft['ingredients']) < 2:
                continue

            with open(os.path.join(barrel_output_path, file), 'w') as f:
                json.dump(craft, f, indent=4)
