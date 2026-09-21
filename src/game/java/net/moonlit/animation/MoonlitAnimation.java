package net.moonlit.animation;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;

public class MoonlitAnimation {

    private static final long NANOS_PER_MS = 1_000_000L;

    public enum Easing {
        LINEAR,
        EASE_OUT,
        EASE_IN,
        EASE_IN_OUT,
        CUBIC
    }

    private static class AnimationState {
        float startValue;
        float endValue;
        float durationMs;
        long startTimeNs;
        Easing easing;
        Runnable onComplete;
        boolean paused;
        float pausedTimeMs;

        float getValue(long nowNs) {
            long elapsedNs = nowNs - startTimeNs;
            float elapsedMs = elapsedNs / (float) NANOS_PER_MS;

            if (paused) {
                elapsedMs = pausedTimeMs;
            }

            if (elapsedMs >= durationMs) {
                if (onComplete != null) {
                    onComplete.run();
                }
                return endValue;
            }

            float t = elapsedMs / durationMs;

            switch (easing) {
                case LINEAR:
                    t = t;
                    break;
                case EASE_OUT:
                    t = 1 - (float) Math.pow(1 - t, 3);
                    break;
                case EASE_IN:
                    t = (float) Math.pow(t, 3);
                    break;
                case EASE_IN_OUT:
                    t = t < 0.5 ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
                    break;
                case CUBIC:
                    t = t < 0.5 ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
                    break;
            }

            return startValue + (endValue - startValue) * t;
        }

        boolean isComplete(long nowNs) {
            long elapsedNs = nowNs - startTimeNs;
            float elapsedMs = elapsedNs / (float) NANOS_PER_MS;
            if (paused) {
                elapsedMs = pausedTimeMs;
            }
            return elapsedMs >= durationMs;
        }

        void setPaused(boolean paused) {
            if (paused && !this.paused) {
                long elapsedNs = EagRuntime.steadyTimeNanos() - startTimeNs;
                pausedTimeMs = elapsedNs / (float) NANOS_PER_MS;
            }
            this.paused = paused;
        }
    }

    private static final List<AnimationState> animations = new ArrayList<>();

    private static long lastTickNs;

    static {
        lastTickNs = EagRuntime.steadyTimeNanos();
    }

    public static void update() {
        long nowNs = EagRuntime.steadyTimeNanos();
        long deltaNs = nowNs - lastTickNs;
        lastTickNs = nowNs;

        for (int i = animations.size() - 1; i >= 0; i--) {
            AnimationState anim = animations.get(i);
            if (!anim.paused && anim.isComplete(nowNs)) {
                animations.remove(i);
            }
        }
    }

    public static void play(float startValue, float endValue, float durationMs, Easing easing, Runnable onComplete) {
        AnimationState anim = new AnimationState();
        anim.startValue = startValue;
        anim.endValue = endValue;
        anim.durationMs = durationMs;
        anim.startTimeNs = EagRuntime.steadyTimeNanos();
        anim.easing = easing;
        anim.onComplete = onComplete;
        anim.paused = false;
        anim.pausedTimeMs = 0;
        animations.add(anim);
    }

    public static float play(float startValue, float endValue, float durationMs, Easing easing) {
        AnimationState anim = new AnimationState();
        anim.startValue = startValue;
        anim.endValue = endValue;
        anim.durationMs = durationMs;
        anim.startTimeNs = EagRuntime.steadyTimeNanos();
        anim.easing = easing;
        anim.paused = false;
        anim.pausedTimeMs = 0;
        animations.add(anim);
        return anim.getValue(EagRuntime.steadyTimeNanos());
    }

    public static float play(float startValue, float endValue, float durationMs) {
        return play(startValue, endValue, durationMs, Easing.LINEAR);
    }

    public static float getValue(int index) {
        if (index < 0 || index >= animations.size()) {
            return 0;
        }
        AnimationState anim = animations.get(index);
        return anim.getValue(EagRuntime.steadyTimeNanos());
    }

    public static int getCount() {
        return animations.size();
    }

    public static void pauseAll() {
        for (AnimationState anim : animations) {
            anim.setPaused(true);
        }
    }

    public static void resumeAll() {
        long nowNs = EagRuntime.steadyTimeNanos();
        for (AnimationState anim : animations) {
            anim.setPaused(false);
            anim.startTimeNs = nowNs - (long) (anim.pausedTimeMs * NANOS_PER_MS);
        }
    }

    public static void clear() {
        animations.clear();
    }

    public static void remove(int index) {
        if (index >= 0 && index < animations.size()) {
            animations.remove(index);
        }
    }

    public static void setPaused(int index, boolean paused) {
        if (index >= 0 && index < animations.size()) {
            animations.get(index).setPaused(paused);
        }
    }

    public static float getProgress(int index) {
        if (index < 0 || index >= animations.size()) {
            return 0;
        }
        AnimationState anim = animations.get(index);
        long nowNs = EagRuntime.steadyTimeNanos();
        long elapsedNs = nowNs - anim.startTimeNs;
        float elapsedMs = elapsedNs / (float) NANOS_PER_MS;
        if (anim.paused) {
            elapsedMs = anim.pausedTimeMs;
        }
        return Math.min(elapsedMs / anim.durationMs, 1.0f);
    }

    public static String getEasingName(Easing easing) {
        switch (easing) {
            case LINEAR:
                return "linear";
            case EASE_OUT:
                return "ease-out";
            case EASE_IN:
                return "ease-in";
            case EASE_IN_OUT:
                return "ease-in-out";
            case CUBIC:
                return "cubic";
            default:
                return "unknown";
        }
    }
}
