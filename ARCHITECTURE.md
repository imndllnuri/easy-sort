# Architecture

## Module & package structure

Multi-module Gradle build (Architectury Loom), so the `core`/`platform`
boundary is enforced by the build system, not just convention - `common`
physically cannot depend on Fabric classes, since they're not even on its
compile classpath.

```
easy-sort/
├── common/                    // Gradle module - zero net.minecraft.*/net.fabricmc.* imports
│   └── com.easysort
│       ├── core/                // SortEngine, TransferEngine, SortConfig, SortableItem, ...
│       │   ├── sort/
│       │   ├── transfer/          // quick-stack / restock
│       │   ├── config/
│       │   └── model/
│       └── platform.common/     // Minecraft-touching but loader-neutral (ContainerAdapter,
│                                    MenuContainers - vanilla-only, no Fabric imports)
├── fabric/                    // Gradle module - Fabric-specific
│   └── com.easysort.platform.fabric
│       ├── client/              // keybinds, screens, rendering, config, widgets, mixins
│       ├── server/              // packet handlers, container mutation
│       └── network/             // networking payload definitions
└── neoforge/                  // (planned) mirrors fabric/ using NeoForge's own APIs
```

**Hard rule:** `common/` must never import `net.minecraft.*` or `net.fabricmc.*` outside
of `platform.common` (which may use vanilla `net.minecraft.*` but never Fabric-specific
classes). This is what keeps the sorting algorithm unit-testable without a Minecraft
runtime, and what makes NeoForge support a new sibling module instead of a rewrite -
verified in practice: `core/` and `platform.common/` moved into `common/` unchanged
when the multi-module split happened.

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
