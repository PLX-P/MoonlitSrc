package net.eaglercraft.eaglertone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Browser-safe, typed settings with conservative bounds for client automation. */
public final class EaglerToneSettings {
    private final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
    private final Map<String, Object> values = new LinkedHashMap<String, Object>();

    public EaglerToneSettings() {
        // Movement settings
        defaultValue("maxBps", Double.valueOf(100.0));
        defaultValue("maxClimbBps", Double.valueOf(20.0));
        defaultValue("maxFallBps", Double.valueOf(20.0));
        defaultValue("maxLiftBps", Double.valueOf(20.0));
        defaultValue("verticalBps", Double.valueOf(12.0));
        defaultValue("antiFling", Boolean.FALSE);
        defaultValue("antiKick", Boolean.FALSE);
        defaultValue("antiKickDropBps", Double.valueOf(5.0));
        defaultValue("smoothingPercent", Float.valueOf(100.0f));
        defaultValue("speedLimitEnabled", Boolean.TRUE);
        defaultValue("speedCapBps", Double.valueOf(100.0));
        defaultValue("hardCap", Double.valueOf(5.0));
        
        // Movement modules
        defaultValue("noSlow", Boolean.FALSE);
        defaultValue("noSlowdown", Boolean.FALSE);
        defaultValue("noRotate", Boolean.FALSE);
        defaultValue("autoStrafe", Boolean.FALSE);
        defaultValue("autoStrafeReverse", Boolean.FALSE);
        defaultValue("autoStrafeLeft", Boolean.FALSE);
        defaultValue("autoStrafeRight", Boolean.FALSE);
        defaultValue("autoStrafeStrafeOnly", Boolean.FALSE);
        defaultValue("bunnyHop", Boolean.FALSE);
        defaultValue("autoJump", Boolean.FALSE);
        defaultValue("disableAutoJump", Boolean.FALSE);
        defaultValue("fly2", Boolean.FALSE);
        defaultValue("phase", Boolean.FALSE);
        defaultValue("spider", Boolean.FALSE);
        defaultValue("longJump", Boolean.FALSE);
        defaultValue("scaffold", Boolean.FALSE);
        defaultValue("liquidWalk", Boolean.FALSE);
        defaultValue("jesus", Boolean.FALSE);
        defaultValue("boatFly", Boolean.FALSE);
        defaultValue("glide", Boolean.FALSE);
        defaultValue("speed2", Boolean.FALSE);
        defaultValue("sprint2", Boolean.FALSE);
        defaultValue("fastClimb", Boolean.FALSE);
        defaultValue("phase2", Boolean.FALSE);
        
        // Player modules
        defaultValue("autoTotem", Boolean.FALSE);
        defaultValue("fastEat", Boolean.FALSE);
        defaultValue("invMove", Boolean.FALSE);
        defaultValue("autoTool", Boolean.FALSE);
        defaultValue("fastPlace", Boolean.FALSE);
        defaultValue("fastBreak", Boolean.FALSE);
        defaultValue("antiAfk", Boolean.FALSE);
        defaultValue("autoGG", Boolean.FALSE);
        defaultValue("autoFish", Boolean.FALSE);
        defaultValue("autoPot", Boolean.FALSE);
        defaultValue("chestStealer", Boolean.FALSE);
        defaultValue("bedBreaker", Boolean.FALSE);
        defaultValue("middleClickPearl", Boolean.FALSE);
        
        // Combat modules
        defaultValue("killAura", Boolean.FALSE);
        defaultValue("autoFight", Boolean.FALSE);
        defaultValue("criticals", Boolean.FALSE);
        defaultValue("autoPot2", Boolean.FALSE);
        defaultValue("autoArmor", Boolean.FALSE);
        defaultValue("reach", Boolean.FALSE);
        defaultValue("aimAssist", Boolean.FALSE);
        defaultValue("autoClicker", Boolean.FALSE);
        defaultValue("autoTap", Boolean.FALSE);
        defaultValue("velocity", Boolean.FALSE);
        defaultValue("autoPot3", Boolean.FALSE);
        
        // Visual modules
        defaultValue("esp", Boolean.FALSE);
        defaultValue("xray", Boolean.FALSE);
        defaultValue("fullbright", Boolean.FALSE);
        defaultValue("chams", Boolean.FALSE);
        defaultValue("nochams", Boolean.FALSE);
        defaultValue("hiteffect", Boolean.FALSE);
        defaultValue("hitIndicator", Boolean.FALSE);
        defaultValue("hitShow", Boolean.FALSE);
        defaultValue("armor2", Boolean.FALSE);
        defaultValue("hitbox", Boolean.FALSE);
        
        // Misc modules
        defaultValue("nameTags", Boolean.FALSE);
        defaultValue("nameTagsCustomFont", Boolean.FALSE);
        defaultValue("nameTagsCustomColor", Boolean.FALSE);
        defaultValue("plugins", Boolean.FALSE);
        defaultValue("step", Boolean.FALSE);
        defaultValue("noslow", Boolean.FALSE);
        defaultValue("nolimits", Boolean.FALSE);
        defaultValue("drone", Boolean.FALSE);
        
        // QoL modules
        defaultValue("fpsCounter", Boolean.FALSE);
        defaultValue("cpsCounter", Boolean.FALSE);
        defaultValue("keystrokes", Boolean.FALSE);
        defaultValue("coordinates", Boolean.FALSE);
        defaultValue("ping2", Boolean.FALSE);
        defaultValue("direction", Boolean.FALSE);
        defaultValue("zoom", Boolean.FALSE);
        defaultValue("fullbright2", Boolean.FALSE);
        defaultValue("toggleSprint", Boolean.FALSE);
        defaultValue("toggleSneak", Boolean.FALSE);
        defaultValue("crosshairCustom", Boolean.FALSE);
        defaultValue("itemAnimations", Boolean.FALSE);
        defaultValue("statusOverlay", Boolean.FALSE);
        
        // Cosmetics
        defaultValue("autoWings", Boolean.FALSE);
        defaultValue("autoCape", Boolean.FALSE);
    }

    private void defaultValue(String name, Object value) {
        defaults.put(name, value);
        values.put(name, value);
    }

    public void put(String name, Object value) {
        if (name == null || name.length() == 0 || !defaults.containsKey(name) || !valid(name, value)) {
            throw new IllegalArgumentException("invalid setting: " + name);
        }
        values.put(name, value);
    }

    public Object get(String name) {
        return values.get(name);
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public boolean setFromString(String name, String text) {
        if (!values.containsKey(name) || text == null) {
            return false;
        }
        Object old = values.get(name);
        try {
            Object parsed;
            if (old instanceof Boolean) {
                if (!"true".equalsIgnoreCase(text) && !"false".equalsIgnoreCase(text)) {
                    return false;
                }
                parsed = Boolean.valueOf(text);
            } else if (old instanceof Integer) {
                parsed = Integer.valueOf(text);
            } else if (old instanceof Long) {
                parsed = Long.valueOf(text);
            } else if (old instanceof Float) {
                parsed = Float.valueOf(text);
            } else if (old instanceof Double) {
                parsed = Double.valueOf(text);
            } else {
                parsed = text;
            }
            put(name, parsed);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public boolean reset(String name) {
        if (!defaults.containsKey(name)) {
            return false;
        }
        values.put(name, defaults.get(name));
        return true;
    }

    public List<String> modifiedNames() {
        List<String> result = new ArrayList<String>();
        for (String name : defaults.keySet()) {
            if (!defaults.get(name).equals(values.get(name))) {
                result.add(name);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public Map<String, Object> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, Object>(values));
    }

    /** Exports settings as portable tab-separated text without filesystem APIs. */
    public String exportText() {
        StringBuilder result = new StringBuilder();
        for (String name : defaults.keySet()) {
            result.append(name).append('\t').append(values.get(name)).append('\n');
        }
        return result.toString();
    }

    /** Imports valid entries and ignores malformed or unknown entries. */
    public int importText(String text) {
        if (text == null) {
            return 0;
        }
        int imported = 0;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String[] fields = line.split("\\t", 2);
            if (fields.length == 2 && setFromString(fields[0], fields[1])) {
                ++imported;
            }
        }
        return imported;
    }

    private static boolean valid(String name, Object value) {
        if (value == null) {
            return false;
        }
        if ("pathCutoffMinimumLength".equals(name)) {
            return value instanceof Integer && ((Integer) value).intValue() >= 1 && ((Integer) value).intValue() <= 1000;
        }
        if ("maxFallHeightBucket".equals(name)) {
            return value instanceof Integer && ((Integer) value).intValue() >= 0 && ((Integer) value).intValue() <= 32;
        }
        if (name.endsWith("TimeoutMS")) {
            return value instanceof Number && ((Number) value).longValue() >= 0L && ((Number) value).longValue() <= 60000L;
        }
        if ("maxWalkWhileBreaking".equals(name)) {
            return value instanceof Integer && ((Integer) value).intValue() >= 0 && ((Integer) value).intValue() <= 1000;
        }
        if (name.equals("maxBps") || name.equals("maxClimbBps") || name.equals("maxFallBps") || 
            name.equals("maxLiftBps") || name.equals("verticalBps") || name.equals("antiKickDropBps") ||
            name.equals("speedCapBps") || name.equals("hardCap")) {
            return value instanceof Number && ((Number) value).doubleValue() >= 0.0 && ((Number) value).doubleValue() <= 200.0;
        }
        if (name.equals("smoothingPercent")) {
            return value instanceof Number && ((Number) value).floatValue() >= 0.0f && ((Number) value).floatValue() <= 100.0f;
        }
        return true;
    }
}
