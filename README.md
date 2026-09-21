# EaglerTone Client

EaglerTone is a PLX-branded Eaglercraft 1.12.2 browser-client fork. The original source license and attribution notices remain in `LICENSE`.

> **Port status:** the port is a staged, version-neutral rewrite. The current checkout includes a browser-safe bounded A* planner with ordinary walking, one-block step-up, safe descent, cancellation hooks, path statistics, typed settings, command controls, and bounded multi-block mining and building through the normal server-validated 1.12.2 client controller, including tool selection and navigation to loaded targets. Inventory automation, advanced movement, multi-block plans, and the 1.8.9 adapter remain incomplete; this is not feature-complete desktop Baritone.

## EaglerTone source integration

The full port roadmap is tracked in [`TODO.md`](TODO.md). It deliberately lists incomplete features instead of claiming that the entire desktop Baritone implementation is already available.

The `EaglerTone/` directory is part of the 1.12.2 source tree and is already included by `build.gradle` in both `main` and `eagler` source sets. If copying the port into another Eaglercraft 1.12.2 checkout, copy `EaglerTone/` and add this directory to both source sets:

```gradle
sourceSets {
    main { java { srcDir 'EaglerTone/src/game/java' } }
    eagler { java { srcDir 'EaglerTone/src/game/java' } }
}
```

The core has no Forge, Fabric, LWJGL, reflection, desktop filesystem, or anti-cheat-bypass dependency. The current 1.12 adapter is wired into the client tick and chat path: press `[` to toggle EaglerTone, then use `#goto <x> <z>`, `#goal block <x> <y> [z]`, `#goal ylevel <y>`, `#goal near <x> <y> <z> <radius>`, `#path`, `#status`, `#help`, `#set`, `#get`, `#unset`, `#modified`, `#stop`, `#pause`, or `#resume`. Path planning reads loaded blocks through the normal `WorldClient` API. The adapter stops on screens, manual movement input, and world changes; it uses ordinary movement input and only enables sprinting when the normal hunger rule allows it. Single-block mining and building use the normal Eaglercraft/Minecraft `PlayerControllerMP` APIs and respect server permissions, reach, inventory, and placement rules. Multi-block plans, inventory automation, advanced movement, and advanced HUD features remain outside the current implementation.

### Port scope and attribution

Baritone is licensed under LGPL-3.0. Keep its license and notices when porting Baritone-derived code, and keep the Eaglercraft source license and notices. The intended port targets are the pathing engine, goals, processes, settings, command control, render/debug output, mining/building interaction, inventory handling, and Elytra behavior where the target protocol supports them. Unsupported desktop or loader APIs must be rewritten rather than copied.

There is no 1.8.9 Eaglercraft source tree in this repository. A genuine 1.8.9 port cannot be compiled or verified until a compatible 1.8.9 checkout is supplied; it must remain a separate version adapter/source tree.

## Porting from Baritone and GitHub workflow

EaglerTone is a source port for Eaglercraft 1.12.2. Use the official Baritone repository as the architectural and source reference:

```bash
git clone https://github.com/cabaletta/baritone.git
cd baritone
git log --oneline --decorate -20
```

Record the exact Baritone commit used. Do not add Baritone as a Gradle dependency and do not copy its Forge, Fabric, loader, desktop-LWJGL, reflection, or desktop-filesystem integrations. Port the platform-neutral pathing, goals, movement-cost, settings, process, and calculation ideas into `EaglerTone/src/game/java/net/eaglercraft/eaglertone/`, adapting all Minecraft APIs to the normal Eaglercraft 1.12.2 APIs.

Keep Baritone attribution and licensing with the source distribution:

```text
EaglerTone/BARITONE-LICENSE
EaglerTone/BARITONE-NOTICE.md
```

Document the exact upstream commit and identify copied/derived files versus original EaglerTone rewrites. The port must not use anti-cheat bypasses, packet spoofing, server-state forgery, Forge/Fabric classes, desktop-only APIs, or reflection.

The EaglerTone source set is already included in both Gradle source sets. When creating a branch and submitting the work to GitHub:

```bash
git checkout -b feature/eaglertone-baritone-1122
git add "Eagler Client/EaglerTone" "Eagler Client/README.md" "Eagler Client/TODO.md" "Eagler Client/build.gradle"
git diff --cached --check
git status
```

Do not add `baritone/.git`, Baritone build outputs, `Eagler Client/build`, or generated client artifacts unless they are explicitly part of a release. Java 17 is the supported build JDK for this checkout:

```bash
cd "Eagler Client"
./gradlew clean compileJava --no-daemon
./gradlew generateJavaScript --no-daemon
./gradlew runclient --no-daemon
```

A GitHub Actions build should use `actions/setup-java@v4` with Temurin Java 17, run `./gradlew compileJava --no-daemon`, and reject `net.minecraftforge`, `net.fabricmc`, `org.lwjgl`, and `java.nio.file` imports under `EaglerTone/src/game/java`.

### Java 17 is recommended for compiling to TeaVM

### Java 8 or greater is required for the desktop runtime

**Most Java IDEs will allow you to import this repository as a gradle project for compiling it to JavaScript.**

Java must be added to your PATH!

**To compile the web client:**
1. Run `CompileEPK`
2. Run `CompileJS` (or the `generateJavaScript` gradle task in your IDE)
3. Check the "javascript" folder

**To compile an offline download:**
1. Run `CompileEPK`
2. Run `CompileJS` (or the `generateJavaScript` gradle task in your IDE)
3. Run `MakeOfflineDownload`
4. Check the "javascript" folder

**To use the desktop runtime:**
1. Open a terminal and run `./gradlew runclient`
2. Run/Debug the client with the included "eaglercraftDebugRuntime" configuration

**To setup a multiplayer server:**

Dead simple test server: https://github.com/catfoolyou/EagsTestServer

To make a server for Eaglercraft 1.12 the recommended software to use is EaglercraftXBungee ("EaglerXBungee") which is included [here](https://github.com/lax1dude/eagl3rxbungee/blob/main/EaglerXBungee-Latest.jar). This is a plugin designed to be used with BungeeCord to allow Eaglercraft players to join your BungeeCord server. It is assumed that the reader already knows what BungeeCord is and has a working server set up that is joinable via java edition. If you don't know what BungeeCord is, please research the topic yourself first before continuing. Waterfall and FlameCord have also been tested, but EaglerXBungee was natively compiled against BungeeCord.

There is an experimental velocity plugin available [here](https://github.com/lax1dude/eagl3rxbungee/blob/main/EaglerXVelocity-Latest.jar) but it is still in development and not recommended for public servers, so be sure to check for updates regularly if you use it. Configuration files are basically identical to EaglercraftXBungee so its safe to just directy copy in your old EaglercraftXBungee config files to the `plugins/eaglerxvelocity` folder and they should work with a minimal number of edits if you are migrating your network from BungeeCord to Velocity.

**Warning:** Both EaglerXBungee and EaglerXVelocity perform a lot of reflection that will inevitably break after a while when BungeeCord or Velocity is updated upstream. Both plugins will display the precise build number of BungeeCord and Velocity that has been tested by the developers and known to be compatible with EaglerXBungee and EaglerXVelocity when the proxy first starts up. If you are experiencing issues, try checking the BungeeCord or Velocity website for old versions and find the closest version number to whatever the current compatible version number is that is printed by EaglerXBungee/EaglerXVelocity, it will probably fix whatever missing functions the error messages are complaining about.

### Installation

Obtain the latest version of the EaglerXBungee JAR file (it can be downloaded in the 1.8 client from the "Multiplayer" screen) and place it in the "plugins" folder of your BungeeCord server. It's recommended to only join native Minecraft 1.12.2 servers through an EaglerXBungee server but plugins like ProtocolSupport have allowed some people to join newer servers too.

Configuration files and other plugin data will be written in `plugins/EaglercraftXBungee`

For more information about EaglerXBungee/Velocity and how to setup a multiplayer server look at the 1.8 readme [here](https://git.eaglercraft.rip/eaglercraft/eaglercraft-1.8#making-a-server)

**Note:** 
This is eclipse-specific problem, just import it into IntelliJ as a Gradle project (NOT as an Eclipse project!) and it will work fine

If you are trying to use the desktop runtime on Linux, make sure you add the "desktopRuntime" folder to the `LD_LIBRARY_PATH` environment variable of the Java process. This should be done automatically by the Eclipse project's default run configuration, but it might not work properly on every system ~~or when the Eclipse project is imported into IntelliJ.~~

The source codes of EaglercraftXBungee and EaglercraftXVelocity are not included here.
