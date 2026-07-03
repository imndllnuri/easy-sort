# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- Initial project scaffold: Gradle/Loom build, package skeleton, CI workflow,
  repository documentation.
- Core sort engine (`core.sort.SortEngine`): merges partial stacks (capped at
  each item's max stack size), sorts via a configurable `SortConfig`
  (mod id / item id / count), deterministic and idempotent by construction.
  Not yet wired into any in-game container - unit-tested only.
- Sort button on chest-like containers (chest, double chest, barrel, ender
  chest - all share vanilla's `ChestMenu`). Client sends a container-scoped
  request; the server re-validates the player's currently open menu and
  mutates only the container's own slots via `SortEngine` + `ContainerAdapter`.
  Player inventory sorting and a hotkey are not implemented yet.
- Restyled the sort button to a compact "S" glyph with a hover tooltip,
  positioned as the first slot in a right-to-left button row so future
  buttons (reverse order, quick-stack, etc.) can be added without
  repositioning this one.
- Moved the button row inside the container panel (previously it sat above
  the window). Added disabled placeholder buttons for Settings (G), Restock
  (R), and Quick Stack (Q) - not wired to any behavior yet, exact
  position/order pending a design reference.
- Replaced vanilla's beveled `Button` texture with a custom flat `MiniButton`
  widget (12x12, plain fill + thin border) so the row reads as a compact
  inline toolbar next to the container title instead of large 3D buttons
  sitting in their own row. Sort tooltip reworded to "Sort this container".
- Player inventory sorting: an "I" button next to the "Inventory" label on
  every container screen, plus a rebindable hotkey (default `R`, under
  Controls > Inventory) that fires while any container screen is open. Only
  ever sorts the player's own 36 hotbar+storage slots - armor and offhand
  are outside that range and are never touched.

### Fixed

- The "I" button no longer shows on Creative mode's item-browsing tabs
  (Building Blocks, Natural Blocks, etc.) - it's only visible on Creative's
  own "Inventory" tab, which is the one actually showing the player's items.
  CreativeModeInventoryScreen reuses the same widgets across all of its tabs
  without re-running screen init, so visibility is now re-checked every
  frame instead of decided once.
