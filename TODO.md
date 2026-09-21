# EaglerTone Baritone Port TODO

**Project:** EaglerTone
**Author/branding:** plx
**Primary target:** Eaglercraft 1.12.2
**Secondary target:** none (1.8.9 is explicitly out of scope for this checkout)
**Policy:** Use normal Eaglercraft client behavior and protocol packets. Do not add anti-cheat bypasses, packet spoofing, server-state forgery, or desktop-only dependencies.

This checklist is the source of truth for the port. An item is only complete after implementation, compilation, and a relevant manual or automated verification pass.

## 0. Project and legal foundations

- [ ] Keep Eaglercraft source licenses and attribution notices.
- [ ] Include the complete Baritone LGPL-3.0 license and required notices for Baritone-derived code.
- [x] Record which files are original EaglerTone rewrites and which are derived from Baritone concepts/code.
- [ ] Keep the core independent of Forge, Fabric, NeoForge, desktop LWJGL, reflection, and Java desktop filesystem APIs.
- [ ] Define separate 1.12.2 and 1.8.9 adapters with a shared version-neutral core.
- [ ] Add reproducible build instructions for browser, desktop runtime, and WASM-GC targets.
- [ ] Add a compatibility matrix for Java version, Gradle version, TeaVM target, browser, and client version.
- [x] Add crash-safe shutdown and cleanup for pathing workers and cached chunks.

## 1. Shared EaglerTone core

- [x] Core branding and lifecycle object.
- [x] Version-neutral settings storage.
- [x] Version-neutral goals.
- [x] Core command parsing foundation.
- [x] World-read contract.
- [x] Movement/action contract.
- [x] Bounded initial A* pathfinder.
- [ ] Replace the initial pathfinder with Baritone’s full path calculation architecture.
- [ ] Add asynchronous path calculation with cancellation.
- [x] Add calculation thread limits and browser-safe budgets.
- [x] Add path calculation progress and timing statistics.
- [x] Add conservative walking, step-up, and safe-descent node expansion.
- [ ] Add path recalculation and failure recovery.
- [ ] Add path smoothing and segment invalidation.
- [ ] Add path-cost abstraction and configurable movement costs.
- [ ] Add deterministic/randomized tie-breaking options.
- [ ] Add path cache and cache invalidation.
- [ ] Add local-player, world, connection, input, inventory, interaction, render, and settings interfaces.
- [ ] Add event bus/lifecycle callbacks without reflection.
- [x] Add structured status/error messages for chat and debug HUD.
- [ ] Add persistent settings serialization compatible with browser storage.
- [x] Add safe defaults and validation for all settings.
- [x] Add setting reset and modified-setting reporting.

## 2. World and block access

- [x] Basic loaded-block access for 1.12.2.
- [x] Implement chunk-aware block caching.
- [x] Handle unloaded chunks without treating them as air.
- [x] Invalidate cached blocks on chunk updates.
- [x] Invalidate cached blocks on block-change packets.
- [ ] Support block state properties and metadata correctly.
- [ ] Support collision shapes and bounding boxes.
- [ ] Support passability, support blocks, hazards, fluids, doors, trapdoors, ladders, vines, slabs, stairs, and snow layers.
- [ ] Support water/lava flow and fluid movement costs.
- [ ] Support webs, powder snow equivalent behavior where available, fire, cactus, magma, and fall hazards.
- [ ] Support tile entities and container block classification.
- [ ] Support world height and dimension-specific limits.
- [ ] Support Nether coordinate behavior.
- [ ] Support unloaded-world and respawn transitions.
- [ ] Add chunk-load readiness checks before planning.
- [ ] Add world seed/dimension context only where exposed safely by the client.

## 3. Movement and physics

- [x] Basic path-node steering through ordinary player input.
- [ ] Implement walking and sprinting control.
- [ ] Implement diagonal movement and sprint rules.
- [ ] Implement jump movement.
- [ ] Implement safe step-up movement.
- [ ] Implement safe descent and fall-height checks.
- [ ] Implement parkour movement.
- [ ] Implement head-bonking detection.
- [ ] Implement swimming and water exits.
- [ ] Implement lava safety behavior.
- [ ] Implement ladders and vines.
- [ ] Implement doors, trapdoors, fence gates, and buttons where permitted.
- [ ] Implement slab/stair geometry.
- [ ] Implement scaffolding/temporary-block movement.
- [ ] Implement sneaking at edges and while bridging.
- [x] Implement sprint toggling and hunger checks.
- [ ] Implement jump boost and other potion-effect movement changes.
- [ ] Implement levitation and slow-falling behavior where supported.
- [ ] Implement riding entities and mounts.
- [ ] Implement boat movement.
- [ ] Implement Elytra movement and landing.
- [ ] Implement firework use for Elytra only through normal inventory/action rules.
- [ ] Implement movement correction after server position corrections.
- [x] Stop automation immediately when the player changes direction manually.
- [x] Stop automation when a screen, chat box, pause menu, or disconnect is active.
- [x] Add stuck detection and recovery.
- [ ] Add movement simulation tests against representative block layouts.

## 4. Pathing engine

- [ ] Implement node expansion for all legal movement types.
- [ ] Implement diagonal and parkour node transitions.
- [ ] Implement jump, descend, swim, climb, and fall transitions.
- [ ] Implement break/place movement transitions.
- [ ] Implement temporary-block transitions.
- [ ] Implement hazard avoidance.
- [ ] Implement mob/entity collision costs where available.
- [x] Implement goal heuristic framework.
- [x] Implement `GoalXZ`.
- [x] Implement `GoalBlock`.
- [x] Implement `GoalYLevel`.
- [x] Implement `GoalNear`.
- [x] Implement `GoalComposite`.
- [x] Implement `GoalInverted`.
- [x] Implement `GoalRunAway`.
- [x] Implement `GoalGetToBlock`.
- [x] Implement `GoalTwoBlocks`.
- [ ] Implement goal equality and serialization.
- [ ] Implement primary, custom, explore, follow, and builder processes.
- [x] Implement path cancellation hooks.
- [x] Implement path recalculation after block changes.
- [ ] Implement path recalculation after player damage/knockback.
- [ ] Implement path recalculation after dimension changes.
- [ ] Implement path failure reasons.
- [x] Implement path selection and segment execution.
- [x] Implement path rendering/debugging.
- [ ] Add performance limits for browser frame time and memory.
- [ ] Add deterministic pathfinder unit tests.
- [ ] Add regression tests for stairs, water, doors, gaps, falls, and narrow corridors.

## 5. Command system

- [x] `#goto` foundation.
- [x] `#goal block` foundation.
- [x] `#stop` / `#cancel` foundation.
- [x] `#pause` foundation.
- [x] `#resume` foundation.
- [x] Add command registry and aliases.
- [x] Add command help and usage errors.
- [x] Add `#set` settings command.
- [x] Add `#unset` settings command.
- [x] Add `#get` settings command.
- [x] Add `#modified` settings command.
- [x] Add `#path` status command.
- [ ] Add path display controls.
- [ ] Add `#cleararea`.
- [ ] Add `#come`.
- [ ] Add `#follow`.
- [ ] Add `#explore`.
- [ ] Add `#find`.
- [ ] Add `#mine`.
- [ ] Add `#build`.
- [ ] Add `#unbuild`.
- [ ] Add `#farm`.
- [ ] Add `#tunnel`.
- [ ] Add `#axis`.
- [ ] Add `#surface`.
- [ ] Add `#top`.
- [ ] Add `#thisway`.
- [ ] Add `#repack`.
- [ ] Add `#reload`.
- [x] Add `#version`.
- [x] Add `#status`.
- [x] Add `#eta`.
- [ ] Add `#echo`.
- [ ] Add `#forcecancel` with an explicit confirmation requirement.
- [ ] Add command prefix configuration.
- [ ] Add command completion in chat.
- [ ] Add command history integration.
- [ ] Ensure EaglerTone commands never leak to multiplayer servers.
- [ ] Add permission/safety checks for every action command.

## 6. Baritone settings

- [x] Initial settings names/defaults.
- [ ] Port all supported Baritone settings and descriptions.
- [ ] Add typed boolean, integer, float, long, enum, and list values.
- [ ] Add setting bounds and validation.
- [ ] Add setting aliases for 1.12.2 and 1.8.9 differences.
- [ ] Add browser persistence.
- [ ] Add reset-to-default behavior.
- [ ] Add settings screen.
- [ ] Add setting change notifications.
- [ ] Add settings export/import using browser-safe text.
- [ ] Document settings that are unavailable on Eaglercraft.

## 7. Mining and block interaction

- [ ] Implement block target selection.
- [x] Implement tool selection.
- [ ] Implement tool durability checks.
- [ ] Implement breaking-time calculation.
- [x] Implement normal dig start/stop/abort packets.
- [x] Implement reach and line-of-sight checks.
- [ ] Implement gravity block handling.
- [ ] Implement liquid handling.
- [ ] Implement ore/material filters.
- [x] Implement `#mine` by block ID/name.
- [x] Implement bounded multiple-block mining goals.
- [ ] Implement collect/drop behavior.
- [ ] Implement safety checks for lava, void, TNT, and protected zones.
- [ ] Implement container and tile-entity avoidance.
- [ ] Implement mining interruption/retry behavior.
- [x] Implement server correction handling.

## 8. Building and schematic support

- [ ] Define a version-neutral build-plan format.
- [ ] Support `.schematic` parsing.
- [ ] Support Sponge `.schem` parsing.
- [ ] Support browser file selection.
- [ ] Validate dimensions and block limits.
- [ ] Implement block palette/state conversion.
- [x] Implement placement target selection.
- [x] Implement face/support selection.
- [x] Implement hotbar/inventory item selection.
- [x] Implement normal right-click placement packets.
- [ ] Implement scaffolding/temporary blocks.
- [ ] Implement build retries after server rejection.
- [ ] Implement replace/waterlog/liquid rules supported by the target version.
- [x] Implement `#build`.
- [ ] Implement `#unbuild` where safe.
- [ ] Implement build progress, pause, resume, and cancellation.
- [ ] Implement preview rendering.
- [ ] Keep EaglerSchem manual preview behavior separate from automation.
- [ ] Add build-plan tests.

## 9. Inventory and item handling

- [ ] Implement inventory snapshot abstraction.
- [ ] Implement hotbar selection.
- [ ] Implement item/tool scoring.
- [ ] Implement normal container click actions.
- [ ] Implement cursor-stack tracking.
- [ ] Implement inventory synchronization checks.
- [ ] Implement crafting support where protocol/client APIs permit it.
- [ ] Implement furnace/smelting support where safe.
- [ ] Implement item pickup behavior.
- [ ] Implement throwaway-item selection.
- [ ] Implement food and healing selection.
- [ ] Implement bucket handling.
- [ ] Implement water/lava bucket safety.
- [ ] Implement ender pearl safety.
- [ ] Implement fireworks and Elytra inventory rules.
- [ ] Never bypass server inventory validation.

## 10. Entity, combat, and follow processes

- [x] Implement entity-query abstraction.
- [ ] Implement `#follow` target selection.
- [ ] Implement player/entity tracking.
- [ ] Implement distance and line-of-sight handling.
- [ ] Implement safe entity collision behavior.
- [ ] Implement hostile-mob avoidance.
- [ ] Implement optional legitimate combat process.
- [ ] Implement target selection and reach checks.
- [ ] Implement attack cooldown rules for the target version.
- [ ] Implement shield/offhand behavior where supported.
- [ ] Implement retreat and hazard avoidance.
- [ ] Keep combat disabled by default until explicitly enabled.

## 11. Exploration and search

- [ ] Implement `#explore` goal/process.
- [ ] Implement deterministic chunk exploration.
- [ ] Implement map/chunk visitation tracking.
- [ ] Implement `#find` block search.
- [ ] Implement loaded-chunk search.
- [ ] Implement optional server-safe exploration limits.
- [ ] Implement structure search only from client-visible data.
- [ ] Implement cancellation and memory bounds.
- [ ] Implement browser persistence for exploration state where possible.

## 12. Rendering and HUD

- [x] Initial planned-path overlay.
- [ ] Render path nodes with configurable colors.
- [ ] Render current path segment.
- [ ] Render goal marker.
- [ ] Render mining/building targets.
- [ ] Render block danger/debug information.
- [ ] Render path status and ETA HUD.
- [ ] Render calculation progress.
- [ ] Add toggle keys and settings.
- [ ] Support anaglyph and both render passes correctly.
- [ ] Restore all OpenGL state after rendering.
- [ ] Avoid rendering unloaded or stale paths.
- [ ] Keep browser performance within frame budget.
- [ ] Add debug screenshot/manual verification.

## 13. 1.12.2 client integration

- [x] Add EaglerTone source set to the normal Gradle build.
- [x] Add EaglerTone source set to the WASM-GC build.
- [x] Add client tick hook.
- [x] Add chat command hook.
- [x] Add toggle key.
- [x] Add player movement hook.
- [x] Add render hook.
- [ ] Add settings GUI hook.
- [x] Add disconnect/world-change reset hook.
- [ ] Add respawn/dimension-change reset hook.
- [ ] Add server correction hook.
- [ ] Add resource-pack/language entries for all controls/statuses.
- [ ] Verify desktop runtime.
- [ ] Verify JavaScript/TeaVM build.
- [ ] Verify WASM-GC build.
- [ ] Verify multiplayer with a test server.
- [ ] Verify integrated singleplayer.

## 14. 1.8.9 port (out of scope)

The requested target is 1.12.2 only. The entries below are retained as historical backlog and are not part of the active completion criteria.

- [ ] Obtain or add a compatible Eaglercraft 1.8.9 source tree.
- [ ] Create a separate 1.8.9 Gradle/build target.
- [ ] Map 1.8.9 block, world, entity, inventory, and packet APIs.
- [ ] Implement the 1.8.9 client adapter.
- [ ] Port movement and physics differences.
- [ ] Port protocol interaction differences.
- [ ] Port rendering differences.
- [ ] Port keybind/settings differences.
- [ ] Port command/chat differences.
- [ ] Verify browser build.
- [ ] Verify desktop runtime if supported.
- [ ] Verify multiplayer and integrated singleplayer.
- [ ] Keep 1.8.9 code isolated from 1.12.2 source and mappings.

## 15. Testing and quality

- [ ] Add pure Java unit tests for goals.
- [ ] Add pure Java unit tests for settings.
- [ ] Add pure Java unit tests for command parsing.
- [ ] Add pathfinder tests with synthetic worlds.
- [ ] Add movement simulation tests.
- [ ] Add schematic/build-plan tests.
- [ ] Add inventory state tests.
- [ ] Add regression tests for path failures.
- [ ] Add browser smoke test.
- [ ] Add desktop runtime smoke test.
- [ ] Add WASM-GC smoke test.
- [ ] Add multiplayer test checklist.
- [ ] Add performance benchmark for path calculation.
- [ ] Add memory-budget test for large loaded areas.
- [ ] Run Gradle compile with a supported Gradle/JDK combination.
- [ ] Fix all warnings that indicate portability problems.
- [x] Verify no Baritone/Forge/Fabric classes remain in browser sources.
- [ ] Verify no server bypass or unsafe packet behavior was introduced.

## 16. Documentation and release

- [x] Document copying `EaglerTone/` into a 1.12.2 source tree.
- [x] Document Gradle source-set integration.
- [ ] Document every supported command.
- [ ] Document every supported setting.
- [ ] Document feature differences from desktop Baritone.
- [ ] Document unsupported 1.12.2/1.8.9 APIs.
- [ ] Add build and release instructions.
- [ ] Add troubleshooting for Java/Gradle compatibility.
- [ ] Add screenshots/GIFs of pathing and planning.
- [ ] Add a changelog.
- [ ] Add a release checklist.
- [ ] Do not label a release “complete Baritone” until all required sections above are implemented and tested.
