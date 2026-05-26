# Necrosis - Text-Based Adventure Engine (Java)

<img width="1774" height="887" alt="necrosis_logo" src="https://github.com/user-attachments/assets/d0f67522-bae5-4c40-bbed-a2f6cdbc4875" />

## University Project

Necrosis was developed for the Programming Methodology Lab semester assignment: **Text-Based Adventure Engine**.

The assignment description from the teacher requires more than a single hardcoded game. It asks for a reusable Java engine for text-based adventure games, where different games can be created through external data files without modifying the engine code.

This project follows that goal by separating:

- **Engine logic**: Java classes for loading, parsing, command execution, models, state, saving, and UI.
- **Game content**: JSON files that define rooms, items, NPCs, enemies, interactables, intro text, start room, ending flags, and command aliases.

## Overview

Necrosis is a survival-horror text-based adventure built on top of a reusable Java engine.

The player explores a failed biomedical facility, interacts with objects and NPCs, solves environmental puzzles, survives infected enemies, and reaches different endings depending on their choices.

The game emphasizes:

- exploration
- environmental storytelling
- decision-making
- system-driven gameplay
- data-driven game definition

## Assignment Compliance

The teacher's PDF description defines the core goal as a reusable text-based adventure engine in Java. The following table shows how this project satisfies the main requirements.

| Assignment requirement | Project implementation |
| --- | --- |
| Reusable engine, not only one game | The engine can load `resources/gameData.json` or `resources/demoGameData.json` without changing Java code. |
| Game content must not be hardcoded in Java | Rooms, items, exits, enemies, NPCs, dialogues, interactables, intro text, start room, ending flags, and command aliases are defined in JSON. |
| External game definition | `GameLoader` loads game data from JSON and builds the runtime world dynamically. |
| Rooms / scenes | Implemented by `Room`, loaded from JSON with descriptions, exits, items, enemies, interactables, and NPCs. |
| Items | Implemented by `Item`, with inventory support and JSON definitions. |
| Player | Implemented by `Player`, with current room, inventory, injury state, and alive/dead state. |
| Game state management | `CurrentGameState` stores rooms, player, flags, Zaun phases, item templates, and command history. |
| Extensible command system | Every command implements `InterfaceCommand`. Commands are separate classes and registered dynamically. |
| Synonyms / aliases | Aliases such as `take`, `grab`, `pick up`, `look at`, and `talk to` are loaded from JSON. |
| Commands with multiple objects | Commands such as `combine broken bottle with scrap metal` pass all remaining words as arguments to command classes. |
| Prepositional phrases | Words such as `with`, `to`, and `on` are preserved in the argument list. |
| No hardcoded parser commands like `if (command.equals("go north"))` | `CommandCutter` matches only aliases registered from JSON and delegates behavior to command classes. |
| Open/Closed Principle | New commands can be added as new `InterfaceCommand` classes and JSON entries without editing `Main` or the parser registration logic. |
| Second game demo | `resources/demoGameData.json` is a separate small game that runs on the same engine. |
| NPC bonus feature | NPCs exist through `Npc` and `DialogueEntry`, with flag-based dialogue staging. |
| Change scenario support | `CombineCommand`, save/load/history, and JSON command registration demonstrate that features can be added without rewriting the engine. |

## Architecture

The project follows a modular architecture with clear separation of responsibilities.

### Model Layer

`engine.model` defines the structure of the game world:

- `Room`
- `Item`
- `Player`
- `Enemy`
- `Npc`
- `Interactable`
- `Exit`
- `DialogueEntry`
- `ZaunPhase`

### Loader Layer

`engine.loader.GameLoader` loads JSON game files and constructs the game world.

Loaded data includes:

- intro narrative
- rooms and descriptions
- exits and navigation
- items
- enemies
- NPCs and dialogues
- interactables and puzzles
- boss encounter phases
- game configuration
- command definitions

Important loader classes:

- `LoadedGameData`
- `GameConfig`
- `CommandDefinition`

### Game State

`CurrentGameState` manages:

- all loaded rooms
- player state
- global flags
- progression state
- boss phases
- item templates for restored saves
- command history

### Command System

Commands are implemented using a command-based architecture:

- Each command is a separate class.
- All commands implement `InterfaceCommand`.
- Command aliases are defined in JSON.
- Commands are registered dynamically by `CommandRegistryLoader`.

Examples:

- Movement: `GoCommand`
- Inventory: `InventoryCommand`
- Interaction: `TakeCommand`, `UseCommand`, `ReadCommand`
- Combat: `StabCommand`, `FlashCommand`, `StrikeCommand`
- Dialogue: `TalkCommand`
- Decisions: `ChooseCommand`
- Persistence: `SaveCommand`, `LoadCommand`
- Utility: `HistoryCommand`, `QuitCommand`

### Command Parser

`CommandCutter`:

- tokenizes player input
- matches the longest registered alias
- supports one-word aliases and multi-word aliases
- passes all remaining tokens to the command class
- avoids hardcoded full-command comparisons

Example:

```text
combine broken bottle with scrap metal
```

The parser matches:

```text
combine
```

and passes:

```text
broken bottle with scrap metal
```

to `CombineCommand`.

### User Interface

A graphical interface was implemented using Java Swing:

- terminal-style window
- scrollable output log
- command input field
- dark theme
- monospace font

The UI is only a wrapper around the engine. It does not contain game rules or game content.

## JSON Game Configuration

The main game is defined in:

```text
resources/gameData.json
```

The second demo game is defined in:

```text
resources/demoGameData.json
```

Each game can define a `gameConfig` section:

```json
{
  "gameConfig": {
    "title": "NECROSIS",
    "startRoomId": "exam_room",
    "endingFlags": [
      "ending_survival",
      "ending_cure",
      "ending_evolution"
    ],
    "commands": [
      {
        "className": "engine.commands.GoCommand",
        "aliases": ["go", "move", "run"]
      }
    ]
  }
}
```

This means `Main.java` does not manually register commands and does not hardcode the starting room.

## Available Commands

Necrosis defines these aliases in `resources/gameData.json`:

- Movement: `go`, `move`, `run`
- Inventory: `inv`, `inventory`
- Take items: `take`, `grab`, `hold`, `pick up`
- Look: `look`, `view`, `look at`, `inspect`, `examine`
- Interaction: `use`, `read`, `enter`, `talk`, `talk to`, `choose`
- Combat and crafting: `flash`, `strike`, `stab`, `combine`
- Persistence: `save`, `load`
- Utility: `history`, `command history`, `quit`, `exit`

Example commands:

```text
look
look at notebook
take broken bottle
pick up flashlight
go north
talk to emily
use generator
enter code 7314
combine broken bottle with scrap metal
strike ripper
stab brute
save
load
history
```

## Gameplay Features

### Exploration

- Room navigation system
- Descriptive environments
- Direction-based exits

### Inventory System

- Item collection
- Item usage
- Portable and non-portable item support

### Interaction System

- Interactables with required items
- Interactables with required flags
- Success and failure messages loaded from JSON

### Combat System

Different enemy types require different strategies:

- Standard infected: use `stab`
- Clicker: use `flash` with the flashlight
- Ripper: use `strike` with scrap metal or the improvised blade
- Brute: use `stab` with the improvised blade

### Crafting System

The player can combine items:

```text
combine broken bottle with scrap metal
```

This creates:

```text
improvised blade
```

### Dialogue System

- NPC interaction through `talk` and `talk to`
- Conditional dialogue through flags
- Dialogue content loaded from JSON

### Progression System

- Global flags control puzzles, locked doors, endings, and story progression.
- Example flags include `power_restored`, `terminal_unlocked`, and ending flags.

### Boss Encounter

- Multi-phase encounter system
- Enemy waves loaded from JSON through `zaun_phases`
- Encounter progression controlled by game state

### Multiple Endings

The game supports multiple endings through JSON-defined ending flags:

- `ending_survival`
- `ending_cure`
- `ending_evolution`

## Save, Load, And History

The engine includes session persistence:

- `save` writes to `saves/savegame.json`
- `load` restores from `saves/savegame.json`
- `history` prints previous valid commands in order

The save file stores:

- current room ID
- inventory item IDs
- flags
- Zaun phase
- command history
- room item state
- enemy state
- exit lock state
- player injury state

The `saves` folder is created automatically if it does not exist.

## How To Run

### Requirements

- Java 17+
- Gson library, included in `libs/gson-2.10.1.jar`
- IntelliJ IDEA or a Java compiler

### IntelliJ

Open the project and run:

```text
Main
```

### Terminal

Compile:

```bash
javac -cp libs/gson-2.10.1.jar -d out $(find src -name "*.java")
```

Run the main Necrosis game:

```bash
java -cp out:libs/gson-2.10.1.jar Main
```

Run the second demo game:

```bash
java -cp out:libs/gson-2.10.1.jar Main resources/demoGameData.json
```

If no JSON path is provided, the engine loads:

```text
resources/gameData.json
```

## Second Game Demo

`resources/demoGameData.json` is intentionally small. It contains:

- 2 rooms
- 2 items
- different intro text
- different `startRoomId`
- its own command aliases
- the same engine code

This demonstrates the assignment requirement that a second game can run on the same engine without modifying the engine.

Try:

```text
look
take old note
go east
use signal bell
```

## Example Output

https://github.com/user-attachments/assets/7e119797-9bb0-4cac-b23e-e28f005a9680

## Project Structure

```text
src/
  Main.java
  engine/
    commands/
    core/
    loader/
    model/
    parser/
    save/
    systems/
  ui/
resources/
  gameData.json
  demoGameData.json
libs/
  gson-2.10.1.jar
```

## Extra Features

Beyond the base assignment requirements, the project includes:

- Swing terminal UI
- JSON-driven command aliases
- save/load support
- command history
- NPC dialogue
- flag-based branching
- multi-stage boss encounter
- enemy-specific combat rules
- crafting through `combine`
- multiple endings
- second demo game

## Contributors

- Pantelis Zarakis - Developer & Graphic Designer

## License

This project is licensed under the [MIT License](./LICENSE).  
See the [LICENSE](./LICENSE) file for more details.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
