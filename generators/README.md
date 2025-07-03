# Recipe Generators

This directory contains a set of Python scripts that automatically generate recipe files for the WoodenCog mod.

## Overview

The main script is `generators.py`, which serves as the entry point for all recipe generation. It imports and runs the other generator scripts, each of which is responsible for a specific type of recipe (e.g., alloying, barrel crafts, etc.).

The data used by the generator scripts is stored in `data.py`, which contains information about metals, alloys, and other materials.

The generated recipe files are placed in the `src/main/resources/data/woodencog/recipes` directory.

## Usage

To run the recipe generators, execute the following command from the root of the project:

```bash
python3 -m generators.generators
```

This will run all the generator scripts and create the corresponding recipe files.
