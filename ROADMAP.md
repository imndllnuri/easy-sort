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

- [x] Restock ("R"): tops up existing partial stacks in the player's
      inventory from the open container - never creates a new stack in an
      empty slot, so it can't hand you item types you weren't already
      carrying.
- [x] Quick Stack ("Q"): moves everything in the player's inventory that the
      open container already has at least one stack of into it, filling
      existing stacks first then the container's empty slots. Won't dump a
      brand-new item type into the container.
- [x] Shulker boxes: all four buttons (S/G/R/Q) now work on placed shulker
      boxes. Scoped to placed boxes only - vanilla has no way to open an
      unplaced shulker box item directly, so "nested inside player
      inventory" from the original wording here didn't correspond to a real
      vanilla interaction.
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

## Milestones

| Milestone | Scope | Status |
|---|---|---|
| M0 | Project scaffold, CI, empty mod loads in dev client | Done |
| M1 | Core sort engine, unit-tested, no Minecraft dependency | Done |
| M2 | Chest-type container sorting (sort button, no hotkey yet) | Done |
| M3 | Player inventory sorting (hotkey + button) | Done |
| M4 | Config screen + persisted config (wires up the Settings/"G" placeholder) | Done |
| M5 | v1.0 public release (Modrinth + CurseForge) | Done (`v1.0.0`) |
| M6+ | Post-MVP features | In progress - Restock/Quick Stack/Shulker boxes done, Bundles next |
| — | NeoForge support (module split + full feature port, see [ARCHITECTURE.md](ARCHITECTURE.md)) | Done - merged, manually QA'd, released alongside Fabric since `v0.4.0-alpha` |
| v2 | Extensibility | Not started |

**Done, not previously tracked here:**
- GameTest integration coverage for `ContainerAdapter` (chest merge/sort,
  max-stack-size capping, and - the one that mattered most - proving an
  inventory sort can never touch equipped armor/offhand). Runs automatically
  as part of `./gradlew build`/CI. Does not cover the networking round-trip
  or button/mixin UI - those remain manual QA, see [TESTING.md](TESTING.md).

**Also done, not previously tracked here:**
- Release automation (`.github/workflows/release.yml`): tag push builds the
  jar, creates a GitHub Release, and publishes to Modrinth + CurseForge.
  Confirmed working end to end on `v0.1.1-alpha`. Real project icon (Sparkle
  Chest) shipped in `v0.4.1-beta`, replacing the placeholder.

Full rationale and trade-offs behind this roadmap live in the original project
planning doc; this file is the living, updated summary.
