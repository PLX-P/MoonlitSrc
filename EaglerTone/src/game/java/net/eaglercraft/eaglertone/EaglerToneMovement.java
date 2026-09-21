package net.eaglercraft.eaglertone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actions are applied by the Minecraft-version adapter using normal client
 * movement and interaction methods. The core never fabricates server state.
 */
public interface EaglerToneMovement {
    void walkForward(float amount);
    void strafe(float amount);
    void jump();
    void stop();

    /** Browser-safe action kinds that adapters may execute through normal APIs. */
    enum ActionType {
        WALK, STRAFE, JUMP, SNEAK, BREAK_BLOCK, PLACE_BLOCK, SELECT_SLOT, WAIT, CANCEL
    }

    /** Immutable adapter-neutral action description. */
    final class Action {
        public final ActionType type;
        public final int x;
        public final int y;
        public final int z;
        public final int value;
        public final String item;

        private Action(ActionType type, int x, int y, int z, int value, String item) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.value = value;
            this.item = item;
        }

        public static Action walk(int ticks) {
            return new Action(ActionType.WALK, 0, 0, 0, Math.max(1, ticks), null);
        }

        public static Action strafe(int ticks, int direction) {
            return new Action(ActionType.STRAFE, 0, 0, 0, direction < 0 ? -Math.max(1, ticks) : Math.max(1, ticks), null);
        }

        public static Action jump() {
            return new Action(ActionType.JUMP, 0, 0, 0, 1, null);
        }

        public static Action sneak(int ticks) {
            return new Action(ActionType.SNEAK, 0, 0, 0, Math.max(1, ticks), null);
        }

        public static Action breakBlock(int x, int y, int z) {
            return new Action(ActionType.BREAK_BLOCK, x, y, z, 0, null);
        }

        public static Action placeBlock(int x, int y, int z, String item) {
            if (item == null || item.length() == 0) {
                throw new IllegalArgumentException("item");
            }
            return new Action(ActionType.PLACE_BLOCK, x, y, z, 0, item);
        }

        public static Action selectSlot(int slot) {
            if (slot < 0 || slot > 8) {
                throw new IllegalArgumentException("slot");
            }
            return new Action(ActionType.SELECT_SLOT, 0, 0, 0, slot, null);
        }

        public static Action waitTicks(int ticks) {
            return new Action(ActionType.WAIT, 0, 0, 0, Math.max(1, ticks), null);
        }

        public static Action cancel() {
            return new Action(ActionType.CANCEL, 0, 0, 0, 0, null);
        }
    }

    /** Mutable-until-sealed plan that can be inspected or executed by an adapter. */
    final class ActionPlan {
        private final List<Action> actions = new ArrayList<Action>();
        private boolean sealed;
        private boolean cancelled;
        private int cursor;

        public ActionPlan add(Action action) {
            if (sealed || cancelled || action == null) {
                throw new IllegalStateException("plan is not editable");
            }
            actions.add(action);
            return this;
        }

        public ActionPlan seal() {
            sealed = true;
            return this;
        }

        public void cancel() {
            cancelled = true;
            cursor = actions.size();
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public boolean isComplete() {
            return cancelled || cursor >= actions.size();
        }

        public int size() {
            return actions.size();
        }

        public int position() {
            return cursor;
        }

        public Action current() {
            return isComplete() ? null : actions.get(cursor);
        }

        public void advance() {
            if (!isComplete()) {
                ++cursor;
            }
        }

        public List<Action> snapshot() {
            return Collections.unmodifiableList(new ArrayList<Action>(actions));
        }
    }
}
