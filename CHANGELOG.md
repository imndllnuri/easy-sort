# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.4.0-alpha] - 2026-07-04

### Added

- NeoForge support (1.21.10): full feature parity with Fabric - Sort, Settings,
  Restock, Quick Stack, the "I" inventory-sort button/hotkey, and shulker box
  support all work identically. Built as a new `neoforge/` Gradle module via
  Architectury Loom, implemented directly against NeoForge's own networking,
  event, and keybind APIs. Manually verified in-game on both platforms.

### Fixed

- Inventory sort ("I" button/hotkey) no longer reorders the hotbar - only the
  27 main storage slots get sorted now, on both Fabric and NeoForge. Reordering
  the hotbar mid-use was jarring (e.g. your currently-held tool could jump to
  a different slot).

## [0.3.1-alpha] - 2026-07-04

### Fixed

- Corrected the "I" (inventory sort) button's vertical position on shulker
  box screens - it was off by a pixel relative to chest-type containers due
  to `ShulkerBoxScreen` adjusting `imageHeight` after the base class had
  already computed the label position from it.

### Changed

- Shrunk the button row from 12x12 to 10x10 and reordered it to read
  left-to-right as S, Q, R, G.

## [0.3.0-alpha] - 2026-07-03

### Added

- Shulker box support: Sort, Settings, Restock, and Quick Stack all now work
  on placed shulker boxes, not just chest-type containers.

### Changed

- Generalized how the mod finds a menu's backing `Container`: `ShulkerBoxMenu`
  has no public getter for it (unlike `ChestMenu`), so `MenuContainers` reads
  it off the menu's own slot list instead - a technique that works for any
  vanilla container-backed menu, not just the two handled today, so adding
  more container types later shouldn't need new per-type plumbing.

## [0.2.0-alpha] - 2026-07-03

First post-MVP feature release: Restock and Quick Stack.

### Added

- Restock ("R" button): tops up the player's existing partial stacks from
  the open container - never creates a new stack in an empty inventory slot.
- Quick Stack ("Q" button): moves everything in the player's inventory that
  the open container already contains into it, filling existing stacks
  first and then the container's empty slots.
- New `core.transfer.TransferEngine`, tested independently of Minecraft
  (unit tests) and against real containers/players (GameTest), including
  confirming neither operation can ever touch equipped armor/offhand.

## [0.1.1-alpha] - 2026-07-03

No functional changes from 0.1.0-alpha. Republished to activate Modrinth and
CurseForge publishing in the release workflow, now that both projects exist.

## [0.1.0-alpha] - 2026-07-03

First public alpha. Chest-type and player inventory sorting work end to end;
config/settings and shulker box support are still in progress.

### Added

- Core sort engine: merges partial stacks (capped at each item's max stack
  size) and sorts deterministically via a configurable order (mod ID / item
  ID / count / display name).
- Sort button ("S") on chest-type containers: chest, double chest, barrel,
  trapped chest, ender chest, and minecart with chest - all share vanilla's
  `ChestMenu`. Server re-validates the player's currently open menu before
  touching anything.
- Player inventory sorting via an "I" button (available on any container
  screen) and a rebindable hotkey (default `R`, Controls > Inventory) - only
  ever touches the 36 hotbar+storage slots, never equipped armor/offhand.
- Sort settings screen (Settings/"G" button): choose the primary sort key,
  persisted per-player to `config/easy-sort.json` and sent with every sort
  request, so it follows you across servers rather than being a per-server
  setting.
- Compact flat button row (custom `MiniButton` widget, no vanilla bevel
  texture) positioned inline with the container title / "Inventory" label.

### Fixed

- The inventory-sort button no longer appears on Creative mode's
  item-browsing tabs (Building Blocks, Natural Blocks, etc.) - only on
  Creative's actual Inventory tab.

### Known limitations

- Shulker boxes are not supported yet (separate `ShulkerBoxMenu` class) -
  planned post-MVP.
- The Restock and Quick Stack buttons are visible but not yet functional
  (disabled placeholders).
- The config screen lets you choose one primary sort key, not a full
  drag-and-drop multi-key ordering.

### Testing

- GameTest integration coverage (`gametest` source set) for
  `ContainerAdapter` against real block entities/players/registries: chest
  merge+sort, max-stack-size capping, and confirming an inventory sort can
  never touch equipped armor/offhand. Runs automatically as part of
  `./gradlew build`/CI.
