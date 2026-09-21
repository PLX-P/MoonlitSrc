# EaglerTone

EaglerTone is a browser-safe, Baritone-inspired automation core for the **Eaglercraft 1.12.2 client** in this source tree. The adapter uses the normal Minecraft 1.12.2 client/world APIs and ordinary client input; the server remains authoritative for movement, inventory, mining, and placement.

## Supported API surface

- Bounded A* path planning over loaded 1.12.2 blocks.
- Walk, diagonal, step-up, safe descent, hazard avoidance, water cost, and configurable fall limits.
- Goals: X/Z, block, Y level, near, two-block, get-to-block, run-away, composite, and inverted goals.
- Commands: `#goto`, `#goal`, `#stop`, `#cancel`, `#pause`, `#resume`, `#replan`, `#path`, `#eta`, `#status`, `#version`, `#help`, `#set`, `#get`, `#unset`, `#modified`, `#mine`, and `#build`.
- Typed, bounded settings with browser-safe import/export text.
- Browser-safe bounded world cache with block, chunk, and unload invalidation.
- Client-visible entity snapshots and nearest-entity helpers.
- Planned-path rendering and ordinary movement-input steering.
- Server-validated bounded mining and building through `PlayerControllerMP`, including navigation to loaded targets, reach/line-of-sight checks, tool selection, support-face selection, hotbar lookup, and client block/item registries.
- Manual-input handoff, GUI/disconnect safety, world-change reset, and server correction compatibility.

## Commands

```text
#goto <x> <z>
#goal block <x> <y> <z>
#goal gettoblock <x> <y> <z>
#goal near <x> <y> <z> <radius>
#goal ylevel <y>
#goal twoblocks <x> <y> <z>
#goal runaway <x> <y> <z> <distance>
#mine <x> <y> <z> [radius 0-4]
#build <x> <y> <z> <block-name>
```

`#mine` and `#build` execute one block at a time through the normal 1.12.2 `PlayerControllerMP` API. The adapter only starts an action when the target is loaded and within normal reach; the controller and server still enforce permissions, inventory, line-of-sight, placement, and block-breaking rules. Missing blocks/items, protected targets, rejected placements, and unloaded targets do not trigger fabricated packets.

## Build

From this directory:

```bash
./gradlew compileJava --no-daemon
```

The EaglerTone source set is already included by `build.gradle` for the normal Java and WASM-GC source configuration.

## Deliberate limitations

EaglerTone does not include anti-cheat bypasses, packet spoofing, server-state forgery, reflection, desktop filesystem APIs, combat automation, inventory automation, crafting, container operations, or actions against unloaded chunks. Large-scale mining/building, retries after server rejection, inventory/container automation, and schematic support remain future work.
