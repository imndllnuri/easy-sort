# Building

## Requirements

- A full JDK 25 - not a JRE-only install (no local Gradle install needed —
  use `./gradlew`). Minecraft 26.x requires Java 25, and NeoForge's build
  tooling needs the Java compiler, which a JRE does not include.
  - If Gradle fails with `release version 25 not supported`, your `java` is
    JRE-only (e.g. Ubuntu's `openjdk-25-jre-headless`). Install the JDK
    package, or point `JAVA_HOME` at a full JDK 25.
  - If you only have a different JDK, Gradle downloads the ones it needs
    itself (foojay toolchain resolver): JDK 25 for the mod and JDK 21 for the
    build scripts in `build-logic/`. This needs network access once. Gradle
    itself can run on JDK 21 or newer.

## Build a distributable jar

```
./gradlew build
```

Output: `fabric/build/libs/easy-sort-<version>.jar` (plus a `-sources.jar`) -
multi-module Gradle build (Fabric Loom for Fabric, ModDevGradle for NeoForge), see [ARCHITECTURE.md](ARCHITECTURE.md).

## Version matrix

Minecraft, mappings, Fabric Loader, and Fabric API versions are all defined in
`gradle.properties` — this is the single place to bump when porting to a new
Minecraft version. See [VERSIONING.md](VERSIONING.md) for the porting process.

For local development workflow (running a dev client, running tests), see
[DEVELOPMENT.md](DEVELOPMENT.md). For how releases are cut and published, see
[RELEASE_PROCESS.md](RELEASE_PROCESS.md).
