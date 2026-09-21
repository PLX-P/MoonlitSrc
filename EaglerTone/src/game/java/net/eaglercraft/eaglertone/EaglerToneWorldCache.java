package net.eaglercraft.eaglertone;

import java.util.LinkedHashMap;
import java.util.Map;

/** Bounded block cache for planning; unloaded positions are never converted to air. */
public final class EaglerToneWorldCache implements EaglerToneWorld {
    private static final class State {
        final boolean loaded;
        final boolean passable;
        final boolean solid;
        final boolean hazard;
        final float cost;

        State(boolean loaded, boolean passable, boolean solid, boolean hazard, float cost) {
            this.loaded = loaded;
            this.passable = passable;
            this.solid = solid;
            this.hazard = hazard;
            this.cost = cost;
        }
    }

    private final EaglerToneWorld source;
    private final int capacity;
    private final Map<Long, State> states;

    public EaglerToneWorldCache(EaglerToneWorld source, int capacity) {
        if (source == null) {
            throw new IllegalArgumentException("source");
        }
        this.source = source;
        this.capacity = Math.max(64, capacity);
        this.states = new LinkedHashMap<Long, State>(this.capacity, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, State> eldest) {
                return size() > EaglerToneWorldCache.this.capacity;
            }
        };
    }

    @Override
    public synchronized boolean isLoaded(int x, int y, int z) {
        return state(x, y, z).loaded;
    }

    @Override
    public synchronized boolean isPassable(int x, int y, int z) {
        return state(x, y, z).passable;
    }

    @Override
    public synchronized boolean isSolid(int x, int y, int z) {
        return state(x, y, z).solid;
    }

    @Override
    public synchronized boolean isHazard(int x, int y, int z) {
        return state(x, y, z).hazard;
    }

    @Override
    public synchronized float movementCost(int x, int y, int z) {
        return state(x, y, z).cost;
    }

    public synchronized void invalidate(int x, int y, int z) {
        states.remove(key(x, y, z));
    }

    public synchronized void invalidateChunk(int chunkX, int chunkZ) {
        java.util.Iterator<Long> iterator = states.keySet().iterator();
        while (iterator.hasNext()) {
            long key = iterator.next().longValue();
            int x = (int) (key >> 38);
            if ((x & 0x02000000) != 0) {
                x |= 0xFC000000;
            }
            int z = (int) (key << 26 >> 38);
            if ((z & 0x02000000) != 0) {
                z |= 0xFC000000;
            }
            if ((x >> 4) == chunkX && (z >> 4) == chunkZ) {
                iterator.remove();
            }
        }
    }

    public synchronized void clear() {
        states.clear();
    }

    public synchronized int size() {
        return states.size();
    }

    private State state(int x, int y, int z) {
        long key = key(x, y, z);
        State cached = states.get(key);
        if (cached != null) {
            return cached;
        }
        boolean loaded = source.isLoaded(x, y, z);
        State result = loaded
                ? new State(true, source.isPassable(x, y, z), source.isSolid(x, y, z),
                        source.isHazard(x, y, z), source.movementCost(x, y, z))
                : new State(false, false, false, true, Float.POSITIVE_INFINITY);
        states.put(key, result);
        return result;
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
