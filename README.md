# Necrosis – Text-Based Adventure Engine (Java)

## University Project

This project was developed as part of a university assignment focused on designing and implementing a text-based adventure engine in Java.

The goal was not only to create a playable game, but to build a reusable and extensible system capable of supporting multiple games through external configuration.

---

## Overview

Necrosis is a survival-horror text-based game built on top of a custom Java engine.  
The player explores a failed biomedical facility, interacts with the environment, solves puzzles, and survives encounters with infected entities.

The game emphasizes:
- exploration
- environmental storytelling
- decision-making
- system-driven gameplay

---

## Core Objective of the Assignment

The project required:

- Implementation of a text-based adventure system in Java
- Use of Object-Oriented Programming (OOP)
- Separation between engine logic and game content
- External data loading (JSON-based world definition)
- Command parsing system for player interaction
- Management of game state (player, rooms, items, etc.)
- Extensibility and clean architecture

---

## Architecture

The project follows a modular architecture with clear separation of responsibilities.

### Model Layer
Defines the structure of the game world:
- `Room`
- `Item`
- `Player`
- `Enemy`
- `Npc`
- `Interactable`

---

### Game State
`CurrentGameState` manages:
- all rooms
- player state
- global flags
- progression
- boss phases

---

### Command System
Commands are implemented using a command-based architecture:

- Each command is a separate class
- All commands implement a common interface
- Commands are registered dynamically in the parser

Examples:
- Movement: `go`
- Interaction: `take`, `use`, `read`
- Combat: `stab`, `flash`, `strike`
- Dialogue: `talk`
- Decisions: `choose`

---

### Command Parser
`CommandCutter`:
- Parses user input into tokens
- Matches commands using keywords and aliases
- Executes the corresponding command object
- Avoids hardcoded conditional logic

---

### Game Loader (JSON)
`GameLoader` loads the game world from a JSON file.

Loaded data includes:
- Intro narrative
- Rooms and descriptions
- Exits and navigation
- Items
- Enemies
- NPCs and dialogues
- Interactables and puzzles
- Boss encounter phases

This enables modification of the game without changing Java code.

---

## Gameplay Features

The game includes:

### Exploration
- Room navigation system
- Descriptive environments

### Inventory System
- Item collection
- Item usage

### Interaction System
- Interactables with conditions (flags/items)
- Environmental puzzles

### Combat System
Different enemy types require different strategies:
- Standard infected → melee (stab)
- Clickers → light-based (flashlight)
- Rippers → heavy weapon (scrap metal)
- Brute → crafted weapon required

### Crafting System
- Combine items to create new tools
- Example: combine broken bottle with scrap metal

### Dialogue System
- NPC interaction with conditional dialogue
- Flag-based branching

### Progression System
- Global flags control story progression
- Puzzle unlocking logic

### Boss Encounter
- Multiphase encounter system
- Enemies spawned dynamically per phase

### Multiple Endings
- Player decisions affect the outcome
- Different narrative conclusions

---

## User Interface

A graphical interface was implemented using Java Swing:

- Terminal-style window
- Scrollable output log
- Command input field (ENTER-based)
- Styled UI (dark theme, monospace font)

The UI acts as a wrapper and does not modify the engine logic.

---

## Extra Features

Beyond the assignment requirements, the project includes:

- Rich narrative writing and atmosphere
- Multi-stage boss system
- Context-based combat mechanics
- Crafting mechanics
- Multiple endings
- JSON-driven storytelling (intro + dialogue)
- GUI implementation instead of plain console
- Command aliases and flexible parsing

---

## How to Run

### Requirements
- Java 17+
- Gson library (included)

### Steps

```bash
git clone https://github.com/PantelisZara/necrosis.git
```

## Example Output




https://github.com/user-attachments/assets/7e119797-9bb0-4cac-b23e-e28f005a9680





## Contributors
- Pantelis Zarakis - Developer & Graphic Designer

## License

This project is licensed under the [MIT License](./LICENSE).  
See the [LICENSE](./LICENSE) file for more details.
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
