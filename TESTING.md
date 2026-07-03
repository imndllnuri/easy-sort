# Testing

## Unit tests

Everything under `core/` (sort engine, comparators, config model) has zero
Minecraft dependency and is tested with plain JUnit 5:

```
./gradlew test
```

These run on every CI build and should stay fast (no Minecraft runtime).

## Integration tests

Container-sorting scenarios that need a real `ScreenHandler`/world (chest sorting,
double chests, nested shulkers) use Fabric's GameTest framework
(`fabric-gametest-api-v1`), added starting at Milestone M3. GameTest runs are
slower and are not required to gate every PR — see the CI workflow.

## Manual QA checklist (pre-release)

Run before every tagged release:

- [ ] Singleplayer: sort player inventory, verify stable/idempotent ordering
- [ ] Singleplayer: sort a chest and a double chest
- [ ] Dedicated server: same checks with two connected clients
- [ ] Config: change sort order, restart client, verify it persisted
- [ ] Config: corrupt/delete config file, verify the game doesn't crash on load
