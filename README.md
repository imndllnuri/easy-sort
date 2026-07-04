# Easy Sort

A lightweight, vanilla-feel inventory and chest sorting mod for Minecraft.

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

Post-MVP, in active development (`0.3.0-alpha`). MVP (chest + player
inventory sorting, config) is complete; see [ROADMAP.md](ROADMAP.md) for
what's shipped and what's next.

## Supported versions

| Minecraft | Loader | Status |
|---|---|---|
| 1.21.10 | Fabric | Alpha |
| 1.21.10 | NeoForge | In development (not yet released) |

Forge support is not currently planned (see [ROADMAP.md](ROADMAP.md)).

## Installation

Published on [Modrinth](https://modrinth.com/mod/tNAlPFb9) and CurseForge
(project ID `1596721`; link TODO once its slug is confirmed - both listings
are pending initial platform review as of this writing anyway). See
[GitHub Releases](https://github.com/imndllnuri/easy-sort/releases) for
built jars in the meantime.

Requires [Fabric Loader](https://fabricmc.net/) and
[Fabric API](https://modrinth.com/mod/fabric-api).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) to report bugs, suggest features, or
submit pull requests, and [DEVELOPMENT.md](DEVELOPMENT.md) to set up a local
development environment.

## License

[MIT](LICENSE)
