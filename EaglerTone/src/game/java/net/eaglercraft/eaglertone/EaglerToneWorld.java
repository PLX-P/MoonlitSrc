package net.eaglercraft.eaglertone;

/** Loaded-world read contract implemented by each Minecraft-version adapter. */
public interface EaglerToneWorld {
    boolean isPassable(int x, int y, int z);
    boolean isSolid(int x, int y, int z);
    boolean isLoaded(int x, int y, int z);

    /** Optional safety classification; hazards are excluded by default. */
    default boolean isHazard(int x, int y, int z) {
        return false;
    }

    /** Optional movement cost multiplier for non-hazardous terrain. */
    default float movementCost(int x, int y, int z) {
        return 1.0F;
    }
}
