# Easy Sort

A lightweight, vanilla-feel inventory and chest sorting mod for Minecraft,
for both Fabric and NeoForge.

One hotkey sorts a container using a stable, configurable ordering and merges
partial stacks — no inventory-management suite bloat, no forced GUI overhaul.

## Features

- **Sort** ("S") any chest-type container — chest, double chest, barrel,
  trapped chest, ender chest, minecart with chest, and shulker box
- **Sort your inventory** ("I", on any container screen, plus a rebindable
  hotkey) — only ever touches your 36 hotbar+storage slots, never equipped
  armor or your offhand item
- **Quick Stack** ("Q") — moves everything in your inventory that the open
  container already has into it
- **Restock** ("R") — tops up your existing partial stacks from the open
  container, without handing you item types you weren't already carrying
- **Settings** ("G") — choose your sort order (mod / item ID / count / name);
  persisted per-player, follows you across servers

## Status

Stable (`1.4.1`). MVP and all post-MVP-so-far features (Restock, Quick
Stack, shulker boxes) are complete on both Fabric and NeoForge; see
[ROADMAP.md](ROADMAP.md) for what's shipped and what's next.

## Screenshots

| Before | After |
|---|---|
| ![Chest before sorting](media/gallery/02-chest-before.png) | ![Chest after sorting](media/gallery/01-chest-after.png) |
| ![Player inventory before sorting](media/gallery/04-player-inventory-before.png) | ![Player inventory after sorting](media/gallery/03-player-inventory-after.png) |
| ![Shulker box before sorting](media/gallery/06-shulker-box-before.png) | ![Shulker box after sorting](media/gallery/05-shulker-box-after.png) |
| ![Barrel before sorting](media/gallery/08-barrel-before.png) | ![Barrel after sorting](media/gallery/07-barrel-after.png) |

Settings screen, choosing a sort order:

![Settings screen: Sort by Mod](media/gallery/10-settings-sort-by-mod.png)
![Settings screen: Sort by Item ID](media/gallery/11-settings-sort-by-item-id.png)

## Supported versions

Fabric and NeoForge can target different Minecraft versions independently -
each uses its own native toolchain, so one platform isn't held back waiting
for the other to catch up.

| Minecraft | Loader | Status |
|---|---|---|
| 26.2 | Fabric | Release |
| 26.2 | NeoForge | Not yet - NeoForge has no stable (non-beta) build for 26.2 |
| 26.1.2 | NeoForge | Release |
| 26.1, 26.1.1, 26.1.2 | Fabric | Frozen on [`mc/26.1.x-fabric`](https://github.com/imndllnuri/easy-sort/tree/mc/26.1.x-fabric) (last release: `v1.3.1`) |
| 1.21.11 | Fabric | Frozen on [`mc/1.21.11`](https://github.com/imndllnuri/easy-sort/tree/mc/1.21.11) (last release: `v1.2.0`) |
| 1.21.11 | NeoForge | Frozen on [`mc/1.21.11`](https://github.com/imndllnuri/easy-sort/tree/mc/1.21.11) (last release: `v1.2.0`) |
| 1.21.10 | Fabric | Frozen on [`mc/1.21.x`](https://github.com/imndllnuri/easy-sort/tree/mc/1.21.x) (last release: `v1.1.0`) |
| 1.21.10 | NeoForge | Frozen on [`mc/1.21.x`](https://github.com/imndllnuri/easy-sort/tree/mc/1.21.x) (last release: `v1.1.0`) |

Forge support is not currently planned (see [ROADMAP.md](ROADMAP.md)).

## Installation

Published on [Modrinth](https://modrinth.com/mod/easy-sort) and
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/easy-sort). See
[GitHub Releases](https://github.com/imndllnuri/easy-sort/releases) for
built jars directly.

Requires [Fabric Loader](https://fabricmc.net/) and
[Fabric API](https://modrinth.com/mod/fabric-api) on Fabric; just
[NeoForge](https://neoforged.net/) itself on NeoForge, no extra API mod
needed. Minecraft 26.1 requires Java 25 - this follows the same requirement.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) to report bugs, suggest features, or
submit pull requests, and [DEVELOPMENT.md](DEVELOPMENT.md) to set up a local
development environment.

## License

[MIT](LICENSE)
