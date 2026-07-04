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
./start.sh fabric      # launch the Fabric dev client
./start.sh neoforge    # launch the NeoForge dev client
./start.sh both        # launch fabric, then neoforge, sequentially
```

`start.sh` runs pre-flight checks, does a scoped compile-only build, launches
the client, then summarizes the run log for you. Use it instead of a bare
`./gradlew runClient` - since both `fabric/` and `neoforge/` declare a
`runClient` task, running it bare from the root launches **both** clients at
once. `start.sh` always targets a module explicitly
(`./gradlew :fabric:runClient` / `:neoforge:runClient`) to avoid that.

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
holds loader-agnostic code (`core/`, `platform/common/`), `fabric/` and
`neoforge/` hold loader-specific code and resources. Bare Gradle task names
that only one module defines (e.g. `test`, `runGameTest`) work fine from the
repo root. For tasks both `fabric/` and `neoforge/` define (`build`,
`runClient`), a bare invocation runs it in *every* module that has it at
once - target a module explicitly instead, e.g. `./gradlew :fabric:build` or
`./gradlew :neoforge:runClient` (or just use `./start.sh fabric|neoforge`,
which does this for you).

See [ARCHITECTURE.md](ARCHITECTURE.md) for the package structure and the
loader-agnostic `core` / loader-specific `platform` boundary. New code that
touches `net.minecraft.*` or `net.fabricmc.*` belongs under `fabric/`, never
under `common/`.

## Version matrix

Minecraft, mappings, loader, and Fabric API versions are all defined in
`gradle.properties` — check https://fabricmc.net/develop/ before bumping any
of them.
