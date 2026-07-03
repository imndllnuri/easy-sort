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

- `main` tracks the latest actively developed Minecraft version.
- Older but still-supported Minecraft versions get a long-lived `mc/1.21.x`
  branch for critical-fix backports only — no new features. Avoid maintaining
  more than one such branch alongside `main` unless demand clearly justifies it.

## Porting to new Minecraft versions

Each Minecraft version bump is its own milestone: update mappings/loader/Fabric
API versions in `gradle.properties`, run the full test suite (unit tests in
`core/` should pass unchanged, by design — see [ARCHITECTURE.md](ARCHITECTURE.md)),
then manually re-verify `platform/fabric` mixins and hooks, which are what
typically break across versions.

## Config compatibility

The config file includes a `configVersion` field from the first release so that
future format changes can migrate automatically instead of requiring users to
delete their config.
