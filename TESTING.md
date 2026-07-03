# Testing

## Unit tests

Everything under `core/` (sort engine, comparators, config model) has zero
Minecraft dependency and is tested with plain JUnit 5:

```
./gradlew test
```

These run on every CI build and should stay fast (no Minecraft runtime).

## Integration tests

`ContainerAdapter` is tested against real block entities, players, and item
registries (not the hand-built `SortableItem` test doubles `core.sort`'s unit
tests use) via Fabric's GameTest framework, in the `gametest` source set:

```
./gradlew runGameTest
```

Fabric Loom wires `runGameTest` into `check`, so it also runs as part of
`./gradlew build` / CI on every PR - the actual test execution is only ~1-2
seconds, the overhead is Minecraft's own startup, which the build already
pays for regardless.

Not covered by GameTest: the actual client→server networking round-trip and
the button/mixin UI (creative-tab visibility, positioning). Those remain
manual QA - see the checklist below.

## Manual QA checklist (pre-release)

Run before every tagged release:

- [ ] Singleplayer: sort player inventory, verify stable/idempotent ordering
- [ ] Singleplayer: sort a chest and a double chest
- [ ] Dedicated server: same checks with two connected clients
- [ ] Config: change sort order, restart client, verify it persisted
- [ ] Config: corrupt/delete config file, verify the game doesn't crash on load
