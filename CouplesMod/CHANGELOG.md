# Couples Mod - Changelog

## [1.0.1] - 2026-06-14

### 🐛 Bug Fixes
* **Curios API Dependency**: Fixed a critical crash where the mod would fail to load if the Curios API was not installed. Curios is now properly configured as a soft dependency. Engagement Rings will still use Curios slots if the mod is present!
* **Relationship Synchronization**: Removed the experimental "global sync" feature that tracked relationship status across different servers. Relationship progression is now strictly tied to the individual server/world you are playing on, fixing multiple bugs and edge cases when playing in different communities.

### ⚙️ Under the Hood
* Built for NeoForge `21.1.229` (Minecraft 1.21.1)
