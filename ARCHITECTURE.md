# Architecture

## Package structure

```
com.easysort
├── core/            // pure Java, no Minecraft/Fabric imports
│   ├── sort/         // SortEngine, SortKey, comparators, SortResult
│   ├── config/        // config data model (POJOs)
│   └── model/         // loader-agnostic item/slot abstractions (SortableItem)
├── platform/         // the only package allowed to depend on net.minecraft.* / net.fabricmc.*
│   ├── fabric/         // Fabric entrypoints, mixins, registry glue
│   │   ├── client/      // client-only: keybinds, screens, rendering
│   │   ├── server/       // server-side packet handlers, container mutation
│   │   └── network/      // networking payload definitions (shared client+server)
│   └── common/         // Minecraft-touching logic that stays loader-neutral in spirit
└── api/              // (future) public extension surface for other mods
```

**Hard rule:** `core/` must never import `net.minecraft.*` or `net.fabricmc.*`. This is
what keeps the sorting algorithm unit-testable without a Minecraft runtime, and what
makes a future NeoForge module addition a mechanical package move instead of a rewrite.

## Client/server separation

Sorting mutates server-authoritative inventory state:

1. Client sends an intent packet ("sort this open container using preset X") on
   hotkey press.
2. Server validates (is this container actually open for this player? are locked
   slots respected?) and performs the mutation via the shared `core/sort` engine.
3. Vanilla slot-sync propagates the result back to the client.

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
- GameTest (`fabric-gametest-api-v1`) covers real container-sorting scenarios once
  container sorting lands (Milestone M3). See [TESTING.md](TESTING.md).

## Versioning of this document

Update this file whenever the package boundary or client/server split changes.
It should always reflect the actual code, not the aspirational design.
