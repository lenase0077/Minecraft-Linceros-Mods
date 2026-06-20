# Couples Mod - Changelog

## [1.0.3] - 2026-06-19

### ✨ New Features
* **Configurable Effects**: Marriage and dating effects (like Haste, Speed, Regeneration) are no longer hardcoded! You can now freely configure, add, or disable these buffs. On NeoForge, edit the `couplesmod-common.toml` config file. On Fabric, the mod now fully integrates with **MidnightLib** and **ModMenu**, meaning you can configure all effects directly from an intuitive in-game menu!
* **Spanish Translation**: Added support for Spanish (`es_es`). Currently, all items and advancements are translated. 🇪🇸🇦🇷🇲🇽

### ⚙️ Under the Hood
* Ported to Minecraft 26.1.2 (NeoForge & Fabric).
## [1.0.1] - 2026-06-14

### 🐛 Bug Fixes
* **Curios API Dependency**: Fixed a critical crash where the mod would fail to load if the Curios API was not installed. Curios is now properly configured as a soft dependency. Engagement Rings will still use Curios slots if the mod is present!
* **Relationship Synchronization**: Removed the experimental "global sync" feature that tracked relationship status across different servers. Relationship progression is now strictly tied to the individual server/world you are playing on, fixing multiple bugs and edge cases when playing in different communities.

### ⚙️ Under the Hood
* Built for NeoForge `21.1.229` (Minecraft 1.21.1)
