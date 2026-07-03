# Roadmap

## MVP (targeting v1.0)

- [ ] Player inventory sorting (hotkey + optional in-screen button)
- [x] Chest sorting, including double chests (button only - also works on
      barrels and ender chests for free, since they share vanilla's `ChestMenu`)
- [ ] Configurable hotkey
- [x] Partial-stack merging
- [x] Stable, deterministic sort algorithm
- [ ] Configurable sort order (config screen + file) - currently fixed to the
      default mod id / item id order, not yet user-configurable in-game

## Post-MVP (v1.x)

- [ ] Shulker boxes (including nested inside player inventory)
- [ ] Barrels
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
| M3 | Player inventory sorting (hotkey + button) | Not started |
| M4 | Config screen + persisted config | Not started |
| M5 | v1.0 public release (Modrinth + CurseForge) | Not started |
| M6+ | Post-MVP features | Not started |
| v2 | Extensibility, possible NeoForge split | Not started |

Full rationale and trade-offs behind this roadmap live in the original project
planning doc; this file is the living, updated summary.
