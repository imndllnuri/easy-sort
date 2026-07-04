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
└── neoforge/                  // Gradle module - NeoForge-specific, mirrors fabric/
    └── com.easysort.platform.neoforge
        ├── client/              // keybind, config, screen, widget, mixins
        ├── server/              // packet handlers, container mutation
        └── network/             // networking payload definitions
```

**Hard rule:** `common/` must never import `net.minecraft.*` or `net.fabricmc.*`/
`net.neoforged.*` outside of `platform.common` (which may use vanilla `net.minecraft.*`
but never loader-specific classes). This is what keeps the sorting algorithm
unit-testable without a Minecraft runtime, and what made NeoForge support a new
sibling module instead of a rewrite - verified in practice twice: `core/` and
`platform.common/` moved into `common/` unchanged when the multi-module split
happened, and `neoforge/` was built entirely by re-implementing the thin platform
glue against NeoForge's own APIs without touching a single line of `common/` or
`fabric/`.

`fabric/` and `neoforge/` are deliberately **not** unified via Architectury's
cross-platform networking/event API - each was written directly against its own
loader's APIs. This trades some duplication in the platform-glue layer (network
payload registration, the button-injection mixin, keybind registration) for zero
regression risk to the already-shipped Fabric code. All actual sorting/transfer
logic lives in `common/` and is identical on both loaders regardless.

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

Both platforms use the same vanilla `CustomPacketPayload`/`StreamCodec` types for
the wire format (payloads carry a container reference and a sort preset id, never
a full inventory snapshot), but register them through each loader's own API:
Fabric via `PayloadTypeRegistry`/`ServerPlayNetworking`, NeoForge via
`RegisterPayloadHandlersEvent`/`PayloadRegistrar` (which runs handlers on the main
thread by default - no manual `context.server().execute()` needed there, unlike
Fabric's handler).

## Events, registries, mixins

Prefer each loader's own event hooks over mixins wherever a clean one exists
(Fabric's `ScreenEvents`/tick events; NeoForge's `ScreenEvent`/`ClientTickEvent`
on `NeoForge.EVENT_BUS`, plus `RegisterKeyMappingsEvent` for keybinds). Mixins are
reserved for cases with no clean hook - adding a widget to an existing vanilla
screen, on both loaders - and live under `platform/fabric/mixin` and
`platform/neoforge/mixin` respectively for easy audit on each Minecraft version
port. Mixin itself works identically on both loaders via Architectury Loom; the
two platforms' mixin classes differ only in how they re-check the inventory
button's per-frame visibility (Fabric: `ScreenEvents.beforeRender`, scoped
per-screen-instance by Fabric API; NeoForge: a second `@Inject` into
`AbstractContainerScreen.render()` HEAD, since `AbstractWidget.render()` is
`final` and NeoForge's screen render event is a global bus listener that would
otherwise leak one registration per screen opened).

## Testing

- Unit tests (JUnit 5) cover all of `core/` with zero Minecraft dependency —
  fast, run on every CI build. Identical on both platforms since `core/` is shared.
- GameTest (`fabric-gametest-api-v1`, `gametest` source set) covers
  `ContainerAdapter`/`MenuContainers` against real block entities, players,
  and item registries - not just the hand-built `SortableItem` test doubles
  the unit tests use. Runs automatically as part of `./gradlew build`/CI. This is
  Fabric-only (NeoForge has its own, separate test framework) - not yet ported.
  Does not cover the networking round-trip or button/mixin UI on either platform;
  those remain manual QA. See [TESTING.md](TESTING.md).

## Versioning of this document

Update this file whenever the package boundary or client/server split changes.
It should always reflect the actual code, not the aspirational design.
