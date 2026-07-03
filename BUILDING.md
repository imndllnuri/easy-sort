# Building

## Requirements

- JDK 21 (no local Gradle install needed — use `./gradlew`)

## Build a distributable jar

```
./gradlew build
```

Output: `build/libs/easy-sort-<version>.jar` (plus a `-sources.jar`).

## Version matrix

Minecraft, mappings, Fabric Loader, and Fabric API versions are all defined in
`gradle.properties` — this is the single place to bump when porting to a new
Minecraft version. See [VERSIONING.md](VERSIONING.md) for the porting process.

For local development workflow (running a dev client, running tests), see
[DEVELOPMENT.md](DEVELOPMENT.md). For how releases are cut and published, see
[RELEASE_PROCESS.md](RELEASE_PROCESS.md).
