package net.eaglercraft.eaglertone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Immutable entity data supplied by a version adapter or synthetic test world. */
public final class EaglerToneEntity {
    public final int id;
    public final String name;
    public final String type;
    public final double x;
    public final double y;
    public final double z;
    public final boolean alive;
    public final boolean hostile;

    public EaglerToneEntity(int id, String name, String type, double x, double y, double z,
            boolean alive, boolean hostile) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.type = type == null ? "" : type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.alive = alive;
        this.hostile = hostile;
    }

    public double distanceSquared(double fromX, double fromY, double fromZ) {
        double dx = x - fromX;
        double dy = y - fromY;
        double dz = z - fromZ;
        return dx * dx + dy * dy + dz * dz;
    }

    public static List<EaglerToneEntity> aliveOnly(List<EaglerToneEntity> entities) {
        List<EaglerToneEntity> result = new ArrayList<EaglerToneEntity>();
        if (entities != null) {
            for (EaglerToneEntity entity : entities) {
                if (entity != null && entity.alive) {
                    result.add(entity);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    public static EaglerToneEntity nearest(List<EaglerToneEntity> entities, String nameOrType,
            double x, double y, double z, boolean hostileOnly) {
        if (entities == null) {
            return null;
        }
        EaglerToneEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (EaglerToneEntity entity : entities) {
            if (entity == null || !entity.alive || (hostileOnly && !entity.hostile)) {
                continue;
            }
            if (nameOrType != null && nameOrType.length() > 0
                    && !nameOrType.equalsIgnoreCase(entity.name)
                    && !nameOrType.equalsIgnoreCase(entity.type)) {
                continue;
            }
            double distance = entity.distanceSquared(x, y, z);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = entity;
            }
        }
        return best;
    }

    public static List<EaglerToneEntity> sortedByDistance(List<EaglerToneEntity> entities,
            final double x, final double y, final double z) {
        List<EaglerToneEntity> result = new ArrayList<EaglerToneEntity>(aliveOnly(entities));
        Collections.sort(result, new Comparator<EaglerToneEntity>() {
            @Override
            public int compare(EaglerToneEntity left, EaglerToneEntity right) {
                return Double.compare(left.distanceSquared(x, y, z), right.distanceSquared(x, y, z));
            }
        });
        return Collections.unmodifiableList(result);
    }
}
