# Changelog - Phase 2 Update

This log documents the recent implementation of sector-specific persistence and the sector selector UI, as well as currently known regression issues.

## Implemented Features

### 1. Persistence & Sector Overrides
- **Per-Sector Configs**: Ore configurations can now be customizable per sector.
- **Inheritance**: Sectors default to Global settings unless "Override" is enabled.
- **Persistence**: Settings are saved using `Core.settings` with sector-specific keys.

### 2. Main Menu Sector Selector
- **Target Context**: Added a selector in the "Ore Config" menu to switch between "Global" mode and specific "Sector" modes.
- **Access**: Users can configure any sector's ore settings directly from the main menu without entering the game.

### 3. In-Game Regeneration
- **Regenerate Button**: Added a button to manually trigger ore regeneration for the current sector to apply changes immediately.

## Bug Fixes
- **UI Duplication**: Fixed the issue where the "Ore Config" button appeared twice on the menu.

---

## Known Issues (To Be Fixed)

> [!WARNING]
> The following issues have been identified in the latest build and are currently being debugged:

1.  **Global Configuration Uneditable**:
    - *Status*: **Detected**.
    - *Symptoms*: When "Global" is selected in the menu, all sliders are grayed out/disabled, preventing edits to the default configuration.
    - *Cause*: Likely a logic error in the `disabled()` condition where it expects an override flag even for global context.

2.  **Generation Failure**:
    - *Status*: **Detected**.
    - *Symptoms*: Ore generation appears to not function correctly or produces unexpected results in the latest version.
    - *Investigation*: Reviewing `IrionPlanetGenerator` integration with `OreConfigManager`.

---

## Technical Details for Commit
- Refactored `OreConfigMenu.java` for context switching.
- Updated `OreConfigManager.java` for scoped persistence.
- Modified `IrionPlanetGenerator.java` to load scoped configs.
