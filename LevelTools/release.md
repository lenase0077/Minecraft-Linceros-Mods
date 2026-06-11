# Linceros LevelTools

**Linceros LevelTools** is an RPG-style progression mod for NeoForge 1.21.1 that completely transforms how you interact with your gear. Originally inspired by a Hytale mod concept, it breathes life into your tools, weapons, and armors by allowing them to gain Experience, level up, and even evolve into better materials!

---

## Features

### Tool & Armor Leveling
Your gear is no longer static! Every block you mine, every mob you hit, and **every time you take damage**, your equipped gear gains **XP**. As you gain XP, your items will level up.
- **Polynomial Scaling:** Each level requires more XP than the last, creating a balanced and rewarding RPG progression curve that doesn't become impossible in the end-game.
- **Armor Scaling:** Taking damage happens less frequently than mining stone. Armors have a configurable XP multiplier (5x by default) so they level up alongside your tools naturally!
- **Full Repair:** By default, leveling up fully repairs your item!
- **Visual & Audio Feedback:** Enjoy satisfying sound effects and particle bursts when leveling up, culminating in a spectacular Totem-like particle explosion when an item evolves!

### Dynamic Stat Bonuses
As your gear levels up, it becomes inherently stronger:
- **Max Durability:** Increases by a percentage with every level, making your favorite gear last much longer.
- **Mining Speed:** Pickaxes, axes, and shovels become noticeably faster.
- **Attack Damage:** Swords and weapons strike harder with each new tier.
- **Damage Reduction:** Worn armors provide a percentage-based incoming damage reduction per level (capped at 80% to prevent invincibility).

### Tool & Armor Evolutions
Why throw away a wooden pickaxe or a leather tunic when they can evolve?
Every 5 levels (configurable), your gear will **Evolve** into the next material tier! 
- **Vanilla Progression:**
  <br>![Wood](https://minecraft.wiki/images/thumb/Wooden_Pickaxe_JE1_BE1.png/32px-Wooden_Pickaxe_JE1_BE1.png?1fe5e) ➔ ![Stone](https://minecraft.wiki/images/thumb/Stone_Pickaxe_JE1_BE1.png/32px-Stone_Pickaxe_JE1_BE1.png?b5a4f) ➔ ![Iron](https://minecraft.wiki/images/thumb/Iron_Pickaxe_JE2_BE1.png/32px-Iron_Pickaxe_JE2_BE1.png?7fab9) ➔ ![Diamond](https://minecraft.wiki/images/thumb/Diamond_Pickaxe_JE1_BE1.png/32px-Diamond_Pickaxe_JE1_BE1.png?da26f) ➔ ![Netherite](https://minecraft.wiki/images/thumb/Netherite_Pickaxe_JE2_BE1.png/32px-Netherite_Pickaxe_JE2_BE1.png?cdaa4)
  <br><br>![Leather](https://minecraft.wiki/images/thumb/Steve_in_leather_armor.png/58px-Steve_in_leather_armor.png?e2e7e) ➔ ![Chainmail](https://minecraft.wiki/images/thumb/Steve_in_chainmail_armor.png/58px-Steve_in_chainmail_armor.png?0897d) ➔ ![Iron Armor](https://minecraft.wiki/images/thumb/Steve_in_iron_armor.png/58px-Steve_in_iron_armor.png?935c9) ➔ ![Diamond Armor](https://minecraft.wiki/images/thumb/Steve_in_diamond_armor.png/58px-Steve_in_diamond_armor.png?6f448) ➔ ![Netherite Armor](https://minecraft.wiki/images/thumb/Steve_in_netherite_armor.png/58px-Steve_in_netherite_armor.png?a8e5e)
- **Perfect Preservation:** When an item evolves, it keeps **all** of its enchantments, its custom anvil name, its damage, and its current level. 

### Sleek User Interface
- **Dynamic HUD:** A clean, unobtrusive XP bar appears on your screen when you switch to a tool or gain XP. It automatically fades out after 3 seconds of inactivity to keep your screen clear!
- **Tooltips:** Hover over any leveled tool or armor in your inventory to see its exact XP, Level, and active Stat Bonuses.

### Highly Configurable
Almost everything can be tweaked in the mod's configuration file (`run/config/leveltools-common.toml`):
- Base XP required for Level 1.
- Polynomial XP multiplier.
- Percentage bonuses for Speed, Damage, Durability, and Armor Reduction.
- Armor XP Gain Multipliers.
- Evolution interval (e.g., evolve every 5 levels, 10 levels, or disable entirely).
- HUD Position (Top Left, Bottom Right, Center, etc.).

---

## For Modpack Makers & Modders
Linceros LevelTools includes a built-in Java API that allows you to register custom evolutions for your own modded items in just one line of code!

```java
// Example: Make a Lead Pickaxe evolve into a Silver Pickaxe
LevelToolsAPI.registerEvolution(ModItems.LEAD_PICKAXE.get(), ModItems.SILVER_PICKAXE.get());
```

---

## Commands
Server admins and mapmakers can use the `/leveltools` command to manage progression:
- `/leveltools addxp <amount>`: Instantly grants XP to the held tool.
- `/leveltools setlevel <level>`: Forces a tool to a specific level and applies stats instantly.
