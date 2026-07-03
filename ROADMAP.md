# Roadmap

## MVP (targeting v1.0)

- [x] Player inventory sorting (hotkey + in-screen button, "I", available on
      any container screen - only touches the main 36 hotbar+storage slots,
      never equipped armor/offhand)
- [x] Chest sorting, including double chests (button only - also works on
      barrels and ender chests for free, since they share vanilla's `ChestMenu`)
- [x] Configurable hotkey (default `R`, rebindable in vanilla's Controls
      menu under Inventory - only fires while a container screen is open)
- [x] Partial-stack merging
- [x] Stable, deterministic sort algorithm
- [x] Configurable sort order (config screen + file) - a "Sort by" preference
      (mod / item ID / count / name) persisted client-side, sent with every
      sort request. No drag-and-drop multi-key reordering yet, just choosing
      the primary key (see ROADMAP note below).

## Post-MVP (v1.x)

- [ ] Shulker boxes (including nested inside player inventory) - confirmed
      NOT covered yet, unlike barrels/ender chests: shulker boxes use their
      own `ShulkerBoxMenu`, not `ChestMenu`
- [ ] Bundles (data-component sort, not slot sort)
- [ ] Locked / ignored slots
- [ ] Favorites
- [ ] Opt-in auto-sort on container close / interval

## v2 / stretch

- [ ] Inventory profiles
- [ ] Custom sorting rule builder
- [ ] In-container search
- [ ] Additional sort keys: durability, enchantments, rarity, tags, custom comparator
- [ ] Public extension API for other mods
- [ ] NeoForge support (module split, see [ARCHITECTURE.md](ARCHITECTURE.md))

## Milestones

| Milestone | Scope | Status |
|---|---|---|
| M0 | Project scaffold, CI, empty mod loads in dev client | Done |
| M1 | Core sort engine, unit-tested, no Minecraft dependency | Done |
| M2 | Chest-type container sorting (sort button, no hotkey yet) | Done |
| M3 | Player inventory sorting (hotkey + button) | Done |
| M4 | Config screen + persisted config (wires up the Settings/"G" placeholder) | Done |
| M5 | v1.0 public release (Modrinth + CurseForge) | Queued next |
| M6+ | Post-MVP features | Not started |
| v2 | Extensibility, possible NeoForge split | Not started |

**Done, not previously tracked here:**
- GameTest integration coverage for `ContainerAdapter` (chest merge/sort,
  max-stack-size capping, and - the one that mattered most - proving an
  inventory sort can never touch equipped armor/offhand). Runs automatically
  as part of `./gradlew build`/CI. Does not cover the networking round-trip
  or button/mixin UI - those remain manual QA, see [TESTING.md](TESTING.md).

**Queued, not yet started:**
- Publishing prerequisites: a real mod icon (placeholder needed - not
  something that can be generated here), and the release GitHub Actions
  workflow to auto-publish tagged builds to Modrinth/CurseForge (documented
  in [RELEASE_PROCESS.md](RELEASE_PROCESS.md) but not yet built).

Full rationale and trade-offs behind this roadmap live in the original project
planning doc; this file is the living, updated summary.
