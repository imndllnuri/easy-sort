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

## Running integration tests (GameTest)

```
./gradlew runGameTest
```

Tests `ContainerAdapter` against real block entities/players/registries in
the `gametest` source set — see [TESTING.md](TESTING.md). Also runs
automatically as part of `./gradlew build`.

## Project layout

The project is a multi-module Gradle build (Architectury Loom): `common/`
holds loader-agnostic code (`core/`, `platform/common/`), `fabric/` holds
Fabric-specific code and resources. Bare Gradle task names (`build`, `test`,
`runClient`, `runGameTest`) work from the repo root - Gradle delegates to
whichever module actually defines them - but you can target a module
explicitly too, e.g. `./gradlew :fabric:build`.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the package structure and the
loader-agnostic `core` / loader-specific `platform` boundary. New code that
touches `net.minecraft.*` or `net.fabricmc.*` belongs under `fabric/`, never
under `common/`.

## Version matrix

Minecraft, mappings, loader, and Fabric API versions are all defined in
`gradle.properties` — check https://fabricmc.net/develop/ before bumping any
of them.
