# Baritone attribution and port boundary

EaglerTone uses the architecture and concepts of [Baritone](https://github.com/cabaletta/baritone), an LGPL-3.0-licensed Minecraft pathfinding project by its contributors.

The EaglerTone core is an original browser-safe rewrite for Eaglercraft 1.12.2. It does not link against or copy Baritone's Forge, Fabric, desktop LWJGL, reflection, filesystem, or loader integration classes. Baritone-derived architecture includes the bounded A* search shape, goal-based heuristics, movement-result cost rejection, cancellation, and best-effort path calculation concepts. Eaglercraft adapter code and browser integration are original EaglerTone code.

When Baritone source code is incorporated in the future, the original copyright headers and the complete LGPL-3.0 license must be retained with the corresponding source distribution. The official license and source are available from the Baritone repository above.

EaglerTone also contains Eaglercraft source distributed under the repository's existing license and attribution notices. No anti-cheat bypass, packet spoofing, server-state forgery, or desktop-only dependency is permitted in this port.
