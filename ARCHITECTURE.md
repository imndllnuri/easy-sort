# Architecture

## Package structure

```
com.easysort
├── core/            // pure Java, no Minecraft/Fabric imports
│   ├── sort/         // SortEngine, SortKey, comparators, SortResult
│   ├── transfer/       // TransferEngine, TransferResult (quick-stack / restock)
│   ├── config/        // config data model (POJOs)
│   └── model/         // loader-agnostic item/slot abstractions (SortableItem)
├── platform/         // the only package allowed to depend on net.minecraft.* / net.fabricmc.*
│   ├── fabric/         // Fabric entrypoints, mixins, registry glue
│   │   ├── client/      // client-only: keybinds, screens, rendering, config, widgets
│   │   ├── server/       // server-side packet handlers, container mutation
│   │   └── network/      // networking payload definitions (shared client+server)
│   └── common/         // Minecraft-touching logic that stays loader-neutral in spirit
│       (ContainerAdapter bridges core to a live Container; MenuContainers
│       generically locates a menu's backing Container - both vanilla-only,
│       no Fabric imports)
└── api/              // (future) public extension surface for other mods
```

**Hard rule:** `core/` must never import `net.minecraft.*` or `net.fabricmc.*`. This is
what keeps the sorting algorithm unit-testable without a Minecraft runtime, and what
makes a future NeoForge module addition a mechanical package move instead of a rewrite.

## Client/server separation

Sorting and transfers (quick-stack, restock) mutate server-authoritative
inventory state:

1. Client sends an intent packet (e.g. "sort this open container using
   preset X") on a button click or hotkey press.
2. Server re-validates against the player's actual currently-open menu -
   never trusts the client's claim about which container is open - and
   performs the mutation via the shared `core/sort` or `core/transfer` engine.
3. The server forces a full menu resync (`broadcastFullState()`) back to the
   client, since these mutations bypass the normal slot-click sync path.

Singleplayer runs an internal server and goes through the exact same path — there is
no separate client-only "just sort locally" shortcut, since that would reopen the
class of desync/dupe bugs this design avoids.

## Config management

- `core/config`: plain data classes — sort order, per-feature toggles.
- `platform/fabric` config binding: a config-screen library generates the in-game
  UI from the data class and handles persistence. UI-framework annotations stay out
  of `core/config` (or are confined to a thin adapter) so the data model stays
  loader-agnostic.

## Networking

Fabric API's `Payload`/`PacketCodec` pattern. Payloads carry a container reference
and a sort preset id — never a full inventory snapshot.

## Events, registries, mixins

Prefer Fabric API hooks (`ScreenEvents`, `ScreenHandlerEvents`, tick events) over
mixins wherever an official hook exists. Mixins are reserved for cases with no
clean hook and live under `platform/fabric/mixin` for easy audit on each Minecraft
version port.

## Testing

- Unit tests (JUnit 5) cover all of `core/` with zero Minecraft dependency —
  fast, run on every CI build.
- GameTest (`fabric-gametest-api-v1`, `gametest` source set) covers
  `ContainerAdapter`/`MenuContainers` against real block entities, players,
  and item registries - not just the hand-built `SortableItem` test doubles
  the unit tests use. Runs automatically as part of `./gradlew build`/CI.
  Does not cover the networking round-trip or button/mixin UI; those remain
  manual QA. See [TESTING.md](TESTING.md).

## Versioning of this document

Update this file whenever the package boundary or client/server split changes.
It should always reflect the actual code, not the aspirational design.
