package net.eaglercraft.eaglertone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Browser-safe A* search inspired by Baritone's movement-result architecture.
 * The adapter supplies legal movement/cost information; this class never sends
 * packets or changes world state.
 */
public final class EaglerTonePathfinder {
    public interface Cancellation {
        boolean isCancelled();
    }

    public static final Cancellation NEVER_CANCELLED = new Cancellation() {
        @Override
        public boolean isCancelled() {
            return false;
        }
    };

    private final EaglerToneWorld world;
    private final int maxNodes;
    private final int maxFall;
    private final long timeoutMillis;
    private final Cancellation cancellation;
    private final boolean allowDiagonalDescend;
    private final boolean allowParkour;
    private int visitedNodes;
    private int movementsConsidered;
    private boolean cancelled;
    private boolean budgetExhausted;
    private boolean timedOut;

    public EaglerTonePathfinder(EaglerToneWorld world, int maxNodes) {
        this(world, maxNodes, 3, 0L, NEVER_CANCELLED);
    }

    public EaglerTonePathfinder(EaglerToneWorld world, int maxNodes, int maxFall, Cancellation cancellation) {
        this(world, maxNodes, maxFall, 0L, cancellation, true, true);
    }

    public EaglerTonePathfinder(EaglerToneWorld world, int maxNodes, int maxFall,
            long timeoutMillis, Cancellation cancellation) {
        this(world, maxNodes, maxFall, timeoutMillis, cancellation, true, true);
    }

    public EaglerTonePathfinder(EaglerToneWorld world, int maxNodes, int maxFall,
            long timeoutMillis, Cancellation cancellation, boolean allowDiagonalDescend, boolean allowParkour) {
        if (world == null) {
            throw new IllegalArgumentException("world");
        }
        this.world = world;
        this.maxNodes = Math.max(64, maxNodes);
        this.maxFall = Math.max(0, maxFall);
        this.timeoutMillis = Math.max(0L, timeoutMillis);
        this.cancellation = cancellation == null ? NEVER_CANCELLED : cancellation;
        this.allowDiagonalDescend = allowDiagonalDescend;
        this.allowParkour = allowParkour;
    }

    public List<Node> find(int startX, int startY, int startZ, EaglerToneEngine.Goal goal) {
        visitedNodes = 0;
        movementsConsidered = 0;
        cancelled = false;
        budgetExhausted = false;
        timedOut = false;
        if (goal == null || goal.isSatisfied(startX, startY, startZ)) {
            return Collections.emptyList();
        }

        long deadline = timeoutMillis == 0L ? Long.MAX_VALUE : System.currentTimeMillis() + timeoutMillis;
        Node start = new Node(startX, startY, startZ, null, 0.0F, Movement.START);
        PriorityQueue<Node> open = new PriorityQueue<Node>(16, new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                int result = Float.compare(a.score, b.score);
                return result != 0 ? result : Float.compare(a.cost, b.cost);
            }
        });
        Map<Long, Node> best = new HashMap<Long, Node>();
        Set<Long> closed = new HashSet<Long>();
        Node bestSoFar = start;
        float bestHeuristic = heuristic(start, goal);
        start.score = bestHeuristic;
        open.add(start);
        best.put(start.key(), start);

        while (!open.isEmpty()) {
            if (cancellation.isCancelled()) {
                cancelled = true;
                break;
            }
            if (System.currentTimeMillis() >= deadline) {
                timedOut = true;
                break;
            }
            if (visitedNodes++ >= maxNodes) {
                budgetExhausted = true;
                break;
            }
            Node current = open.poll();
            if (goal.isSatisfied(current.x, current.y, current.z)) {
                return reconstruct(current);
            }
            if (!closed.add(current.key())) {
                continue;
            }
            float currentHeuristic = heuristic(current, goal);
            if (currentHeuristic < bestHeuristic) {
                bestHeuristic = currentHeuristic;
                bestSoFar = current;
            }

            for (Movement movement : Movement.values()) {
                if (movement == Movement.START
                        || (!allowDiagonalDescend && movement.name().startsWith("DESCEND_")
                                && movement.dx != 0 && movement.dz != 0)
                        || (!allowParkour && movement.name().startsWith("STEP_")
                                && movement.dx != 0 && movement.dz != 0)) {
                    continue;
                }
                int nx = current.x + movement.dx;
                int nz = current.z + movement.dz;
                int ny = current.y;
                if (movement.name().startsWith("STEP_")) {
                    ny = current.y + 1;
                } else if (movement.name().startsWith("DESCEND_")) {
                    ny = findDescent(current.y, nx, nz);
                }
                ++movementsConsidered;
                if (ny == Integer.MIN_VALUE || !isLoaded(nx, ny, nz)
                        || !canStand(nx, ny, nz) || !isLoaded(nx, ny - 1, nz)
                        || (movement.dx != 0 && movement.dz != 0
                                && !isSafeDiagonal(current.x, current.y, current.z, movement.dx, movement.dz))
                        || world.isHazard(nx, ny, nz)) {
                    continue;
                }
                add(open, best, closed, current, nx, ny, nz, movement, goal);
            }
        }
        if (cancelled) {
            return Collections.emptyList();
        }
        return bestSoFar == start ? Collections.emptyList() : reconstruct(bestSoFar);
    }

    private void add(PriorityQueue<Node> open, Map<Long, Node> best, Set<Long> closed,
            Node current, int x, int y, int z, Movement movement, EaglerToneEngine.Goal goal) {
        float terrainCost = world.movementCost(x, y, z);
        if (Float.isNaN(terrainCost) || Float.isInfinite(terrainCost) || terrainCost <= 0.0F) {
            return;
        }
        Node next = new Node(x, y, z, current, current.cost + movement.cost * terrainCost, movement);
        long key = next.key();
        if (closed.contains(key)) {
            return;
        }
        Node old = best.get(key);
        if (old == null || next.cost < old.cost) {
            next.score = next.cost + heuristic(next, goal);
            best.put(key, next);
            open.add(next);
        }
    }

    private boolean isSafeDiagonal(int x, int y, int z, int dx, int dz) {
        int sideX = x + dx;
        int sideZ = z + dz;
        return isLoaded(sideX, y, z) && isLoaded(x, y, sideZ)
                && canStand(sideX, y, z) && canStand(x, y, sideZ);
    }

    private int findDescent(int y, int nx, int nz) {
        for (int drop = 1; drop <= maxFall; ++drop) {
            int candidate = y - drop;
            if (isLoaded(nx, candidate, nz) && canStand(nx, candidate, nz)) {
                return candidate;
            }
            if (!world.isPassable(nx, candidate, nz)) {
                break;
            }
        }
        return Integer.MIN_VALUE;
    }

    private boolean isLoaded(int x, int y, int z) {
        return world.isLoaded(x, y, z);
    }

    private boolean canStand(int x, int y, int z) {
        // Never ask an adapter for block state outside its loaded read window.
        // Treating an unloaded head or support block as air can create paths into
        // chunks that are not ready and makes the next movement tick unsafe.
        return isLoaded(x, y - 1, z) && isLoaded(x, y, z) && isLoaded(x, y + 1, z)
                && world.isPassable(x, y, z) && world.isPassable(x, y + 1, z)
                && world.isSolid(x, y - 1, z);
    }

    private static float heuristic(EaglerTonePathfinder.Node node, EaglerToneEngine.Goal goal) {
        return goal.heuristic(node.x, node.y, node.z);
    }

    private static List<Node> reconstruct(Node end) {
        List<Node> result = new ArrayList<Node>();
        for (Node node = end; node != null; node = node.parent) {
            result.add(node);
        }
        Collections.reverse(result);
        return result;
    }

    public int getVisitedNodes() {
        return visitedNodes;
    }

    public int getMovementsConsidered() {
        return movementsConsidered;
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    public boolean wasBudgetExhausted() {
        return budgetExhausted;
    }

    public boolean wasTimedOut() {
        return timedOut;
    }

    public static final class Node {
        public final int x, y, z;
        public final Node parent;
        public final float cost;
        public final String movement;
        private float score;

        private Node(int x, int y, int z, Node parent, float cost, Movement movement) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.parent = parent;
            this.cost = cost;
            this.movement = movement.name();
        }

        private long key() {
            return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
        }
    }

    private enum Movement {
        START(0, 0, 0.0F),
        WALK_EAST(1, 0, 1.0F), WALK_WEST(-1, 0, 1.0F),
        WALK_SOUTH(0, 1, 1.0F), WALK_NORTH(0, -1, 1.0F),
        WALK_SOUTHEAST(1, 1, 1.414F), WALK_SOUTHWEST(-1, 1, 1.414F),
        WALK_NORTHEAST(1, -1, 1.414F), WALK_NORTHWEST(-1, -1, 1.414F),
        STEP_EAST(1, 0, 1.25F), STEP_WEST(-1, 0, 1.25F),
        STEP_SOUTH(0, 1, 1.25F), STEP_NORTH(0, -1, 1.25F),
        STEP_SOUTHEAST(1, 1, 1.75F), STEP_SOUTHWEST(-1, 1, 1.75F),
        STEP_NORTHEAST(1, -1, 1.75F), STEP_NORTHWEST(-1, -1, 1.75F),
        DESCEND_EAST(1, 0, 1.1F), DESCEND_WEST(-1, 0, 1.1F),
        DESCEND_SOUTH(0, 1, 1.1F), DESCEND_NORTH(0, -1, 1.1F),
        DESCEND_SOUTHEAST(1, 1, 1.55F), DESCEND_SOUTHWEST(-1, 1, 1.55F),
        DESCEND_NORTHEAST(1, -1, 1.55F), DESCEND_NORTHWEST(-1, -1, 1.55F);

        final int dx, dz;
        final float cost;

        Movement(int dx, int dz, float cost) {
            this.dx = dx;
            this.dz = dz;
            this.cost = cost;
        }
    }
}
