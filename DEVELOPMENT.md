# Development Setup

## Prerequisites

- JDK 21
- No local Gradle install needed — use the bundled wrapper (`./gradlew`)

## Building

```
./gradlew build
```

## Running in a dev client

```
./gradlew runClient
```

## Running unit tests only

```
./gradlew test
```

Tests under `core/` have no Minecraft dependency and run fast in isolation.
Integration-level coverage (GameTest) is added starting at Milestone M3 — see
[ROADMAP.md](ROADMAP.md).

## Project layout

See [ARCHITECTURE.md](ARCHITECTURE.md) for the package structure and the
loader-agnostic `core` / loader-specific `platform` boundary. New code that
touches `net.minecraft.*` or `net.fabricmc.*` belongs under `platform/fabric`,
never under `core`.

## Version matrix

Minecraft, mappings, loader, and Fabric API versions are all defined in
`gradle.properties` — check https://fabricmc.net/develop/ before bumping any
of them.
