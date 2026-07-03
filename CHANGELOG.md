# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

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
