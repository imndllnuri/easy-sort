# Versioning

## Mod version

Easy Sort follows [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`,
independent of the Minecraft version it targets.

- **PATCH** — bug fixes, no new features, no behavior changes.
- **MINOR** — new features, backwards compatible (e.g. `0.2.0` adds inventory
  sorting, `0.3.0` adds chest sorting, `1.1.0` adds a post-MVP feature).
- **MAJOR** — breaking changes to config format, save data, or public API
  (`2.0.0`). `0.x.y` is pre-MVP/unstable; `1.0.0` is the first stable public
  release.

The mod version is **not** the same as:

- **Minecraft version** (`1.21.10`) — the game version the build targets.
- **Fabric Loader version** — the mod loader version required at runtime.
- **Fabric API version** — a separate mod dependency, versioned independently.

All of these are tracked separately in `gradle.properties` and in each release's
compatibility table.

## Git branching

- `main` always tracks the single, latest actively developed Minecraft
  version and gets all new features.
- When bumping to a new Minecraft version, first cut a branch named
  `mc/<old-version>.x` (e.g. `mc/1.21.x`) from `main`'s last commit at the old
  version, *then* bump `main` to the new version. The branch is a frozen
  snapshot of the whole multi-module repo at that version - `common/`,
  `fabric/`, and `neoforge/` all travel together, so it always contains
  whichever loaders were supported at the time it was cut.
- `mc/*.x` branches only ever receive critical-fix backports, cherry-picked
  from `main` after the fix lands there - never new features. Tag and release
  from the branch itself (e.g. `v0.4.1-alpha` off `mc/1.21.x`), independent of
  `main`'s version numbering.
- Avoid maintaining more than one `mc/*.x` branch alongside `main` unless
  demand clearly justifies it - each one is ongoing maintenance load, not a
  one-time cost.

Run `scripts/check-mc-update.py` any time to check whether the toolchain
(Fabric API, NeoForge, Parchment, Architectury Loom) supports building
against a newer Minecraft version than the one in `gradle.properties` -
it checks the live upstream sources instead of needing a manual re-check
each time.

## Porting to new Minecraft versions

Each Minecraft version bump is its own milestone: update mappings/loader/
Fabric API/NeoForge versions in `gradle.properties`, run the full test suite
(unit tests in `common/core` should pass unchanged, by design — see
[ARCHITECTURE.md](ARCHITECTURE.md)), then manually re-verify both
`platform/fabric` and `platform/neoforge`'s mixins and hooks, which are what
typically break across versions.

## Config compatibility

The config file includes a `configVersion` field from the first release so that
future format changes can migrate automatically instead of requiring users to
delete their config.
