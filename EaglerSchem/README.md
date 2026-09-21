# EaglerSchem

**EaglerSchem — made by PLX** is a standalone client-side schematic preview component for the Eaglercraft 1.12.2 source tree in this repository.

The implementation is intentionally written against the Eagler 1.12 APIs already present here. No Forge, Fabric, Litematica jar, or proprietary runtime is required. This checkout does not contain Litematica source, and the original Litematica code cannot simply be dropped into Eaglercraft because it depends on a different loader, mappings, rendering hooks, and desktop file APIs.

## Add it to your own Eagler source fork

1. Copy the entire `EaglerSchem/` directory into the root of your Eaglercraft source repository.
2. Add this source directory to the Java source set in `build.gradle`:

   ```gradle
   sourceSets {
       main {
           java {
               srcDir 'EaglerSchem/src/game/java'
           }
       }
   }
   ```

   If the project already lists `src/game/java` in `srcDirs`, adding the directory to that same list is also fine.
3. Copy the four small integration changes from this repository:
   - `GameSettings.java`: add `keyBindEaglerSchem` with default `KeyboardConstants.KEY_RBRACKET`, and include it in both `keyBindings` arrays.
   - `Minecraft.java`: call `EaglerSchemManager.tick(this)` at the end of `processKeyBinds` handling.
   - `EntityRenderer.java`: call `EaglerSchemManager.render(world, cameraX, cameraY, cameraZ)` after world layers render.
   - `desktopRuntime/resources/assets/minecraft/lang/en_us.lang`: add `key.eaglerSchem=Toggle EaglerSchem Preview`.
4. Run `./gradlew compileJava`.
5. Build the browser client using the existing `CompileEPK` and `CompileJS` scripts.

## Controls

- `]`: toggle the preview; this is configurable in **Options → Controls → Miscellaneous**.
- Arrow keys: move the loaded schematic on X/Z.
- `Space`: move up.
- `Shift`: move down.
- Upload is requested automatically the first time the preview is enabled with no file loaded.

## Supported files

- `.schematic`: legacy MCEdit NBT `Blocks`, `Data`, `Width`, `Height`, and `Length`.
- `.schem`: Sponge NBT palette and varint `BlockData`.
- `.litematica`: not parsed directly in this minimal Eagler port. Export the file from Litematica to `.schem` or `.schematic`, then upload that export.

## Safety behavior

This component is a **manual-only visual aid**. It does not enable freecam flight, modify the client world, send synthetic placement packets, bypass reach checks, or auto-build. The red overlay marks a non-air block of a different type already present in the world. Use the normal game controls to place blocks only when the server allows it; this avoids silently creating a client/server desync or triggering anti-cheat systems.

## License and attribution

Keep the upstream repository `LICENSE` and attribution notices when publishing a fork. The EaglerSchem-specific source is marked with `made by PLX`; it does not remove or replace upstream licensing requirements.
