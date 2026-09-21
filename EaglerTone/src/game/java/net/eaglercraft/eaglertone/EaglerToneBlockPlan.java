package net.eaglercraft.eaglertone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pure core build/mining plan; adapters perform only normal server-validated actions. */
public final class EaglerToneBlockPlan {
    public enum Operation { BREAK, PLACE }

    public static final class Entry {
        public final Operation operation;
        public final int x;
        public final int y;
        public final int z;
        public final String block;

        private Entry(Operation operation, int x, int y, int z, String block) {
            this.operation = operation;
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
        }

        public static Entry breakBlock(int x, int y, int z) {
            return new Entry(Operation.BREAK, x, y, z, "");
        }

        public static Entry placeBlock(int x, int y, int z, String block) {
            if (block == null || block.length() == 0 || block.indexOf('\t') >= 0 || block.indexOf('\n') >= 0) {
                throw new IllegalArgumentException("block");
            }
            return new Entry(Operation.PLACE, x, y, z, block);
        }
    }

    private final List<Entry> entries = new ArrayList<Entry>();
    private int cursor;
    private boolean cancelled;

    public EaglerToneBlockPlan add(Entry entry) {
        if (entry == null || cancelled) {
            throw new IllegalStateException("plan is cancelled");
        }
        entries.add(entry);
        return this;
    }

    public EaglerToneBlockPlan addBreak(int x, int y, int z) {
        return add(Entry.breakBlock(x, y, z));
    }

    public EaglerToneBlockPlan addPlace(int x, int y, int z, String block) {
        return add(Entry.placeBlock(x, y, z, block));
    }

    public void cancel() {
        cancelled = true;
        cursor = entries.size();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isComplete() {
        return cancelled || cursor >= entries.size();
    }

    public Entry current() {
        return isComplete() ? null : entries.get(cursor);
    }

    public void advance() {
        if (!isComplete()) {
            ++cursor;
        }
    }

    public int size() {
        return entries.size();
    }

    public int completed() {
        return cursor;
    }

    public List<Entry> snapshot() {
        return Collections.unmodifiableList(new ArrayList<Entry>(entries));
    }

    /** Portable text format: operation, x, y, z, block. */
    public String exportText() {
        StringBuilder result = new StringBuilder();
        for (Entry entry : entries) {
            result.append(entry.operation.name()).append('\t').append(entry.x).append('\t')
                    .append(entry.y).append('\t').append(entry.z).append('\t').append(entry.block).append('\n');
        }
        return result.toString();
    }

    /** Imports valid lines and skips malformed entries. */
    public int importText(String text) {
        if (text == null || cancelled) {
            return 0;
        }
        int imported = 0;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String[] fields = line.split("\\t", 5);
            if (fields.length != 5) {
                continue;
            }
            try {
                int x = Integer.parseInt(fields[1]);
                int y = Integer.parseInt(fields[2]);
                int z = Integer.parseInt(fields[3]);
                if ("BREAK".equals(fields[0]) && fields[4].length() == 0) {
                    addBreak(x, y, z);
                    ++imported;
                } else if ("PLACE".equals(fields[0]) && fields[4].length() > 0) {
                    addPlace(x, y, z, fields[4]);
                    ++imported;
                }
            } catch (RuntimeException ignored) {
                // Invalid input is ignored rather than producing unsafe actions.
            }
        }
        return imported;
    }
}
