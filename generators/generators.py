"""This script runs all the recipe generators for the WoodenCog mod."""
import os
from .generate_ingots_melted import generate_ingots_melted
from .generate_ingots_welding import generate_ingots_welding
from .generate_sheet_crafts import generate_sheet_crafts
from .generate_ingot_mold_to_ingot import generate_ingot_mold_to_ingot
from .generate_nuggets_melted import generate_nuggets_melted
from .data import metals, nuggets
from .generate_alloying import generate_alloying
from .generate_barrel_crafts import generate_barrel_crafts
from .generate_chisel_crafts import generate_chisel_crafts
from .generate_knapping_crafts import generate_knapping_crafts
from .generate_heating_crafts import generate_heating_crafts
from .generate_crusher_crafts import generate_crusher_crafts
from .generate_filling import generate_filling
from .generate_mold_to_item import generate_mold_to_item
from .generate_unfinished_models import generate_unfinished_models

from . import paths

os.makedirs(paths.mixing_path, exist_ok=True)
os.makedirs(os.path.join(paths.mixing_path, 'barrel'), exist_ok=True)
os.makedirs(paths.heated_mixing_path, exist_ok=True)
os.makedirs(paths.heated_compacting_path, exist_ok=True)
os.makedirs(paths.heated_pressing_path, exist_ok=True)
os.makedirs(paths.advanced_filling_path, exist_ok=True)
os.makedirs(paths.deploying_path, exist_ok=True)
os.makedirs(paths.cutting_path, exist_ok=True)
os.makedirs(os.path.join(paths.cutting_path, 'slab'), exist_ok=True)
os.makedirs(os.path.join(paths.cutting_path, 'smooth'), exist_ok=True)
os.makedirs(os.path.join(paths.cutting_path, 'stair'), exist_ok=True)
os.makedirs(paths.compacting_path, exist_ok=True)
os.makedirs(paths.heating_path, exist_ok=True)
os.makedirs(paths.crushing_path, exist_ok=True)

for metal in metals:
    generate_ingots_melted(metal['name'], metal['welding'])
    generate_ingots_welding(metal['name'], metal['welding'])
    generate_ingot_mold_to_ingot(metal['name'])
    generate_sheet_crafts(metal['name'], metal['working'])

for nugget in nuggets:
    generate_nuggets_melted(nugget['name'], nugget['result'], nugget['min_temp'])

# generate_anvil_crafts()
generate_alloying()
generate_barrel_crafts()
generate_chisel_crafts()
generate_knapping_crafts()
generate_heating_crafts()
generate_crusher_crafts()
generate_filling()
generate_mold_to_item()
generate_unfinished_models()
