## LifeCycle
A plugin that adds aging and trait system to Minecraft
### ⚠️ Disclaimer ⚠️
Plugin is still in beta stage and will possibly break, use it at your own risk!
### Features
- Age system and configurable age stages and aging interval/lifespan
- Trait system
- Family system (toggleable) (WIP - Not avaiable yet)
### Commands
- `/lifecycle` (`/life`)
  - `check [player?]`
    - Check a player's age and their traits
    - Permission:
      - `lifecycle.check.others` (default: `False`)
      - `lifecycle.check.self` (default: `True`)
  - `age [set/subtract/add] [player] [value]`
    - Edits target player's age
    - Permission: `lifecycle.age.[set/subtract/add]` (default: `False`)
  - `trait`
    - `list`
      - List all avaiable traits
      - Permission: `lifecycle.trait.list` (default: `True`)
    - `set [player] [trait] [slot]`
      - Set a player's trait
      - ⚠️ `[slot]`: The current implementation will add the inputed trait to the highest avaiable slot in a player's life. For example: A player has 2 traits which are in slot 0 and slot 1 respectively. Attempting to add a trait to slot >=3 will result in the trait being added to slot 2 instead.
      - Permission: `lifecycle.trait.set` (default: `False`)
### Planned
- ~~PlaceholderAPI support~~ (added)
- Family system
- Toggleable trait system
