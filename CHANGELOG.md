# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [1.3.0] - 2026-07-13

### Changed

- Ported to Minecraft 26.1.2, Mojang's first fully unobfuscated release under
  the new year-based versioning scheme. Requires Java 25 at runtime (up from
  21) - Mojang bumped this for 26.1 itself. No user-facing behavior changes.
- Dropped Architectury Loom in favor of talking to each platform's own
  official tooling directly: `net.fabricmc.fabric-loom` for Fabric,
  `net.neoforged.moddev` (ModDevGradle) for NeoForge. Minecraft ships
  unobfuscated from 26.1 onward, so Mojang/Parchment mappings no longer
  exist at all - Architectury (a third-party wrapper) hadn't shipped support
  for the new build model at the time of this release, but Fabric and
  NeoForge's own tooling already had. `common/` never used Architectury's
  runtime API, so this was a toolchain swap, not a rewrite: a `build-logic`
  composite build now provides shared Gradle conventions, and `common/`
  exposes its source directly to `fabric/`/`neoforge/` instead of a
  compiled jar dependency.
- 1.21.11 (the previous target) is frozen on the `mc/1.21.11` branch; `main`
  no longer builds against it.

### Fixed (internal, source-only)

- Ported every real API break the 26.1.2 upgrade surfaced, confirmed via
  `javap`/decompiled sources against the actual jars rather than guessed:
  Fabric API renames (`PayloadTypeRegistry.playC2S()` ->
  `serverboundPlay()`, `KeyBindingHelper` -> `KeyMappingHelper`,
  `ScreenEvents.beforeRender()` -> `beforeExtract()`), and a genuine
  rendering-pipeline rewrite (`GuiGraphics` -> `GuiGraphicsExtractor`,
  `AbstractButton.renderContents()` -> `extractContents()`,
  `Screen.render()` -> `extractRenderState()`; `fill()`/`pose()` unchanged).
  Mixin `compatibilityLevel` bumped `JAVA_21` -> `JAVA_25`.

## [1.2.0] - 2026-07-13

### Changed

- Ported to Minecraft 1.21.11 (Fabric API `0.141.4+1.21.11`, NeoForge
  `21.11.42`, Parchment `2025.12.20`, Architectury API `19.0.1`). No user-
  facing behavior changes - this release only exists to track the new
  Minecraft version. `main` no longer builds against 1.21.10; that line is
  frozen on the `mc/1.21.x` branch (last released as `v1.1.0`).

### Fixed (internal, source-only)

- `net.minecraft.resources.ResourceLocation` was renamed to
  `net.minecraft.resources.Identifier` in 1.21.11 - updated every reference
  on both platforms (network payload channel IDs, the "Easy Sort" keybind
  category, the mod ID lookups).
- `AbstractButton.renderWidget(...)` became `final` in 1.21.11; the abstract
  method to implement is now `renderContents(...)` (identical signature).
  Renamed the override in `MiniButton` on both platforms - no behavior
  change, this is the same rename vanilla made internally.

## [1.1.0] - 2026-07-10

### Added

- A dedicated "Easy Sort" category in the vanilla Controls > Key Binds
  screen, and a new, separately configurable "Sort Container" keybind
  (unbound by default - "S" is movement, so there's no safe default). The
  existing "Sort Inventory" keybind (default `R`) now lives in the same
  category instead of being lumped into vanilla's "Inventory" section.

### Fixed

- **Both platforms:** the sort keybinds never actually fired while a
  container or inventory screen was open - the one situation they're meant
  to work in. Root cause: vanilla only calls `KeyMapping.click()` (which
  backs `KeyMapping.consumeClick()`) when `Minecraft.screen == null`; it's
  never called while any screen is displayed, so the previous per-tick
  `consumeClick()` poll could never detect the keypress. Fixed by hooking the
  per-screen keyboard event directly instead: Fabric's
  `ScreenKeyboardEvents.afterKeyPress`, NeoForge's
  `ScreenEvent.KeyPressed.Post`. Both keybinds keep their existing scoping -
  Sort Inventory on any container screen, Sort Container only on a supported
  one (chest, shulker box).

### Changed

- The `S`/`Q`/`R`/`G`/`I` in-screen button glyphs now render at 0.75x scale
  instead of full font size, reading less cramped inside the 10px buttons.

## [1.0.1] - 2026-07-10

### Fixed

- **Critical (Fabric):** the published jar was missing every class from the
  Fabric `client` source set - the client entrypoint, config screen, button
  widget, and both mixins (`AbstractContainerScreenMixin`,
  `CreativeModeInventoryScreenAccessor`) never made it into the shipped jar.
  Mixin's config-prepare pass failed on startup as soon as any class was
  transformed (visible as an `InvalidMixinException` crash, often blamed on
  whatever other mod happened to trigger the first mixin transform, e.g.
  Axiom's `preLaunch`), and even without that crash, none of the client-side
  UI ever actually shipped. Root cause: `fabric/build.gradle`'s `shadowJar`
  task only packaged `sourceSets.main.output`, never
  `sourceSets.client.output`. Fixed by adding `from sourceSets.client.output`
  to `shadowJar`. Affected every Fabric release since the `common`/`fabric`
  module split (`0.4.0-alpha` onward); NeoForge was never affected (it
  doesn't use split source sets).

## [1.0.0] - 2026-07-08

First stable release. No functional changes from 0.4.1-beta - every MVP and
post-MVP-so-far feature (sort, player inventory sort, Restock, Quick Stack,
shulker boxes, settings screen) is complete and working on both Fabric and
NeoForge. This release marks that milestone: Easy Sort is no longer alpha/
beta, it's a stable tool safe to depend on. Bundles, locked/ignored slots,
favorites, and auto-sort remain planned as post-1.0 minor releases (see
[ROADMAP.md](ROADMAP.md)).

## [0.4.1-beta] - 2026-07-04

First beta: MVP + all planned post-MVP features (Restock, Quick Stack,
shulker boxes) are done on both Fabric and NeoForge. No functional changes
from 0.4.0-alpha - this release marks a shift in confidence, not new
features, ahead of real dedicated-server testing.

### Changed

- Replaced the placeholder "ES" icon with a real one (chest + sparkle) on
  both Fabric and NeoForge.

## [0.4.0-alpha] - 2026-07-04

### Added

- NeoForge support (1.21.10): full feature parity with Fabric - Sort, Settings,
  Restock, Quick Stack, the "I" inventory-sort button/hotkey, and shulker box
  support all work identically. Built as a new `neoforge/` Gradle module via
  Architectury Loom, implemented directly against NeoForge's own networking,
  event, and keybind APIs. Manually verified in-game on both platforms.

### Fixed

- Inventory sort ("I" button/hotkey) no longer reorders the hotbar - only the
  27 main storage slots get sorted now, on both Fabric and NeoForge. Reordering
  the hotbar mid-use was jarring (e.g. your currently-held tool could jump to
  a different slot).

## [0.3.1-alpha] - 2026-07-04

### Fixed

- Corrected the "I" (inventory sort) button's vertical position on shulker
  box screens - it was off by a pixel relative to chest-type containers due
  to `ShulkerBoxScreen` adjusting `imageHeight` after the base class had
  already computed the label position from it.

### Changed

- Shrunk the button row from 12x12 to 10x10 and reordered it to read
  left-to-right as S, Q, R, G.

## [0.3.0-alpha] - 2026-07-03

### Added

- Shulker box support: Sort, Settings, Restock, and Quick Stack all now work
  on placed shulker boxes, not just chest-type containers.

### Changed

- Generalized how the mod finds a menu's backing `Container`: `ShulkerBoxMenu`
  has no public getter for it (unlike `ChestMenu`), so `MenuContainers` reads
  it off the menu's own slot list instead - a technique that works for any
  vanilla container-backed menu, not just the two handled today, so adding
  more container types later shouldn't need new per-type plumbing.

## [0.2.0-alpha] - 2026-07-03

First post-MVP feature release: Restock and Quick Stack.

### Added

- Restock ("R" button): tops up the player's existing partial stacks from
  the open container - never creates a new stack in an empty inventory slot.
- Quick Stack ("Q" button): moves everything in the player's inventory that
  the open container already contains into it, filling existing stacks
  first and then the container's empty slots.
- New `core.transfer.TransferEngine`, tested independently of Minecraft
  (unit tests) and against real containers/players (GameTest), including
  confirming neither operation can ever touch equipped armor/offhand.

## [0.1.1-alpha] - 2026-07-03

No functional changes from 0.1.0-alpha. Republished to activate Modrinth and
CurseForge publishing in the release workflow, now that both projects exist.

## [0.1.0-alpha] - 2026-07-03

First public alpha. Chest-type and player inventory sorting work end to end;
config/settings and shulker box support are still in progress.

### Added

- Core sort engine: merges partial stacks (capped at each item's max stack
  size) and sorts deterministically via a configurable order (mod ID / item
  ID / count / display name).
- Sort button ("S") on chest-type containers: chest, double chest, barrel,
  trapped chest, ender chest, and minecart with chest - all share vanilla's
  `ChestMenu`. Server re-validates the player's currently open menu before
  touching anything.
- Player inventory sorting via an "I" button (available on any container
  screen) and a rebindable hotkey (default `R`, Controls > Inventory) - only
  ever touches the 36 hotbar+storage slots, never equipped armor/offhand.
- Sort settings screen (Settings/"G" button): choose the primary sort key,
  persisted per-player to `config/easy-sort.json` and sent with every sort
  request, so it follows you across servers rather than being a per-server
  setting.
- Compact flat button row (custom `MiniButton` widget, no vanilla bevel
  texture) positioned inline with the container title / "Inventory" label.

### Fixed

- The inventory-sort button no longer appears on Creative mode's
  item-browsing tabs (Building Blocks, Natural Blocks, etc.) - only on
  Creative's actual Inventory tab.

### Known limitations

- Shulker boxes are not supported yet (separate `ShulkerBoxMenu` class) -
  planned post-MVP.
- The Restock and Quick Stack buttons are visible but not yet functional
  (disabled placeholders).
- The config screen lets you choose one primary sort key, not a full
  drag-and-drop multi-key ordering.

### Testing

- GameTest integration coverage (`gametest` source set) for
  `ContainerAdapter` against real block entities/players/registries: chest
  merge+sort, max-stack-size capping, and confirming an inventory sort can
  never touch equipped armor/offhand. Runs automatically as part of
  `./gradlew build`/CI.
