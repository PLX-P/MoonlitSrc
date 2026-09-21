package net.eaglercraft.eaglertone;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import net.minecraft.world.Chunk;
import net.minecraft.world.World;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EaglerToneEngine {

    private static EaglerToneEngine instance;
    private final Minecraft mc = Minecraft.getMinecraft();

    private final Map<String, EaglerTonePlugin> plugins = new ConcurrentHashMap<>();
    private final List<EaglerToneCommand> commands = new ArrayList<>();

    private EaglerToneSettings settings = new EaglerToneSettings();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    private long lastTickTime = 0;
    private int tickCount = 0;
    private int frameCount = 0;
    private int fps = 0;
    private int ping = 0;

    private double maxBps = 100.0;
    private double maxClimbBps = 20.0;
    private double maxFallBps = 20.0;
    private double maxLiftBps = 20.0;
    private double verticalBps = 12.0;
    private boolean antiFlingEnabled = false;
    private boolean antiKickEnabled = false;
    private double antiKickDropBps = 5.0;
    private float smoothingPercent = 100.0f;
    private boolean speedLimitEnabled = true;
    private double speedCapBps = 100.0;
    private double hardCap = 5.0;

    private boolean noSlowEnabled = false;
    private boolean noSlowdownEnabled = false;
    private boolean noRotateEnabled = false;
    private boolean autoStrafeEnabled = false;
    private boolean autoStrafeReverse = false;
    private boolean autoStrafeLeft = false;
    private boolean autoStrafeRight = false;
    private boolean autoStrafeStrafeOnly = false;

    private boolean bunnyHopEnabled = false;
    private boolean autoJumpEnabled = false;
    private boolean disableAutoJumpEnabled = false;

    private boolean flyEnabled = false;
    private boolean phaseEnabled = false;
    private boolean spiderEnabled = false;
    private boolean longJumpEnabled = false;
    private boolean scaffoldEnabled = false;
    private boolean liquidWalkEnabled = false;
    private boolean jesusEnabled = false;
    private boolean boatFlyEnabled = false;
    private boolean glideEnabled = false;
    private boolean speedEnabled = false;
    private boolean sprintEnabled = false;
    private boolean fastClimbEnabled = false;
    private boolean phaseEnabled2 = false;

    private boolean autoTotemEnabled = false;
    private boolean fastEatEnabled = false;
    private boolean invMoveEnabled = false;
    private boolean autoToolEnabled = false;
    private boolean fastPlaceEnabled = false;
    private boolean fastBreakEnabled = false;
    private boolean antiAfkEnabled = false;
    private boolean autoGGEnabled = false;
    private boolean autoFishEnabled = false;
    private boolean autoPotEnabled = false;
    private boolean chestStealerEnabled = false;
    private boolean bedBreakerEnabled = false;
    private boolean middleClickPearlEnabled = false;

    private boolean killAuraEnabled = false;
    private boolean autoFightEnabled = false;
    private boolean criticalsEnabled = false;
    private boolean autoPotEnabled2 = false;
    private boolean autoArmorEnabled = false;
    private boolean reachEnabled = false;
    private boolean aimAssistEnabled = false;
    private boolean autoClickerEnabled = false;
    private boolean autoTapEnabled = false;
    private boolean velocityEnabled = false;
    private boolean autoPotEnabled3 = false;

    private boolean espEnabled = false;
    private boolean xrayEnabled = false;
    private boolean fullbrightEnabled = false;
    private boolean chamsEnabled = false;
    private boolean nochamsEnabled = false;
    private boolean hiteffectEnabled = false;
    private boolean hitIndicatorEnabled = false;
    private boolean hitShowEnabled = false;
    private boolean armorEnabled = false;
    private boolean hitboxEnabled = false;

    private boolean nameTagsEnabled = false;
    private boolean nameTagsCustomFont = false;
    private boolean nameTagsCustomColor = false;
    private boolean pluginsEnabled = false;

    private boolean stepEnabled = false;
    private boolean noslowEnabled = false;
    private boolean nolimitsEnabled = false;
    private boolean droneEnabled = false;

    private boolean fpsCounterEnabled = false;
    private boolean cpsCounterEnabled = false;
    private boolean keystrokesEnabled = false;
    private boolean coordinatesEnabled = false;
    private boolean pingEnabled = false;
    private boolean directionEnabled = false;
    private boolean zoomEnabled = false;
    private boolean fullbrightEnabled2 = false;
    private boolean toggleSprintEnabled = false;
    private boolean toggleSneakEnabled = false;
    private boolean crosshairCustomEnabled = false;
    private boolean itemAnimationsEnabled = false;
    private boolean statusOverlayEnabled = false;

    private boolean autoWingsEnabled = false;
    private boolean autoCapeEnabled = false;

    private final List<String> disabledPlugins = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static EaglerToneEngine getInstance() {
        if (instance == null) {
            instance = new EaglerToneEngine();
        }
        return instance;
    }

    public void initialize() {
        if (isInitialized.get()) return;

        loadSettings();
        registerDefaultCommands();
        isInitialized.set(true);
    }

    public void start() {
        if (!isInitialized.get()) {
            initialize();
        }
        isRunning.set(true);
    }

    public void stop() {
        isRunning.set(false);
    }

    public void update() {
        if (!isRunning.get()) return;

        tickCount++;
        frameCount++;

        if (System.currentTimeMillis() - lastTickTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTickTime = System.currentTimeMillis();
        }

        updatePlugins();
        executeCommands();
    }

    private void registerDefaultCommands() {
        commands.add(new EaglerToneCommand("#goto", "Go to coordinates"));
        commands.add(new EaglerToneCommand("#goal", "Set goal"));
        commands.add(new EaglerToneCommand("#mine", "Mine block"));
        commands.add(new EaglerToneCommand("#build", "Build structure"));
        commands.add(new EaglerToneCommand("#explore", "Explore"));
        commands.add(new EaglerToneCommand("#follow", "Follow"));
        commands.add(new EaglerToneCommand("#come", "Come to you"));
        commands.add(new EaglerToneCommand("#surface", "Go to surface"));
        commands.add(new EaglerToneCommand("#stop", "Stop all tasks"));
        commands.add(new EaglerToneCommand("#status", "Show status"));
        commands.add(new EaglerToneCommand("#help", "Show help"));
        commands.add(new EaglerToneCommand("#speed", "Set speed"));
        commands.add(new EaglerToneCommand("#fly", "Toggle fly"));
        commands.add(new EaglerToneCommand("#phase", "Toggle phase"));
        commands.add(new EaglerToneCommand("#speed", "Set speed"));
        commands.add(new EaglerToneCommand("#bph", "Set blocks per hour"));
        commands.add(new EaglerToneCommand("#settings", "Open settings"));
        commands.add(new EaglerToneCommand("#plugins", "List plugins"));
        commands.add(new EaglerToneCommand("#plugin", "Toggle plugin"));
        commands.add(new EaglerToneCommand("#features", "List features"));
        commands.add(new EaglerToneCommand("#toggle", "Toggle feature"));
        commands.add(new EaglerToneCommand("#enable", "Enable feature"));
        commands.add(new EaglerToneCommand("#disable", "Disable feature"));
        commands.add(new EaglerToneCommand("#stat", "Show stats"));
        commands.add(new EaglerToneCommand("#reset", "Reset settings"));
        commands.add(new EaglerToneCommand("#save", "Save settings"));
        commands.add(new EaglerToneCommand("#load", "Load settings"));
        commands.add(new EaglerToneCommand("#config", "Open config"));
        commands.add(new EaglerToneCommand("#ping", "Check ping"));
        commands.add(new EaglerToneCommand("#fps", "Check fps"));
        commands.add(new EaglerToneCommand("#coords", "Get coordinates"));
        commands.add(new EaglerToneCommand("#time", "Get time"));
        commands.add(new EaglerToneCommand("#weather", "Get weather"));
        commands.add(new EaglerToneCommand("#version", "Show version"));
        commands.add(new EaglerToneCommand("#about", "Show info"));
        commands.add(new EaglerToneCommand("#discord", "Join Discord"));
        commands.add(new EaglerToneCommand("#github", "View GitHub"));
        commands.add(new EaglerToneCommand("#website", "View website"));
        commands.add(new EaglerToneCommand("#donate", "Donate"));
        commands.add(new EaglerToneCommand("#report", "Report bug"));
        commands.add(new EaglerToneCommand("#suggest", "Suggest feature"));
        commands.add(new EaglerToneCommand("#request", "Request feature"));
        commands.add(new EaglerToneCommand("#priority", "Set priority"));
        commands.add(new EaglerToneCommand("#queue", "Show queue"));
        commands.add(new EaglerToneCommand("#cancel", "Cancel task"));
        commands.add(new EaglerToneCommand("#pause", "Pause tasks"));
        commands.add(new EaglerToneCommand("#resume", "Resume tasks"));
        commands.add(new EaglerToneCommand("#restart", "Restart tasks"));
        commands.add(new EaglerToneCommand("#reload", "Reload config"));
    }

    private void executeCommands() {
        // Execute pending commands
    }

    private void updatePlugins() {
        for (EaglerTonePlugin plugin : plugins.values()) {
            if (plugin.isEnabled()) {
                plugin.update();
            }
        }
    }

    public void loadSettings() {
        // Load from file
        settings = new EaglerToneSettings();
        settings.maxBps = 100.0;
        settings.maxClimbBps = 20.0;
        settings.maxFallBps = 20.0;
        settings.maxLiftBps = 20.0;
        settings.verticalBps = 12.0;
        settings.antiFlingEnabled = false;
        settings.antiKickEnabled = false;
        settings.antiKickDropBps = 5.0;
        settings.smoothingPercent = 100.0f;
        settings.speedLimitEnabled = true;
        settings.speedCapBps = 100.0;
        settings.hardCap = 5.0;
        settings.noSlowEnabled = false;
        settings.noSlowdownEnabled = false;
        settings.noRotateEnabled = false;
        settings.autoStrafeEnabled = false;
        settings.autoStrafeReverse = false;
        settings.autoStrafeLeft = false;
        settings.autoStrafeRight = false;
        settings.autoStrafeStrafeOnly = false;
        settings.bunnyHopEnabled = false;
        settings.autoJumpEnabled = false;
        settings.disableAutoJumpEnabled = false;
        settings.flyEnabled = false;
        settings.phaseEnabled = false;
        settings.spiderEnabled = false;
        settings.longJumpEnabled = false;
        settings.scaffoldEnabled = false;
        settings.liquidWalkEnabled = false;
        settings.jesusEnabled = false;
        settings.boatFlyEnabled = false;
        settings.glideEnabled = false;
        settings.speedEnabled = false;
        settings.sprintEnabled = false;
        settings.fastClimbEnabled = false;
        settings.phaseEnabled2 = false;
        settings.autoTotemEnabled = false;
        settings.fastEatEnabled = false;
        settings.invMoveEnabled = false;
        settings.autoToolEnabled = false;
        settings.fastPlaceEnabled = false;
        settings.fastBreakEnabled = false;
        settings.antiAfkEnabled = false;
        settings.autoGGEnabled = false;
        settings.autoFishEnabled = false;
        settings.autoPotEnabled = false;
        settings.chestStealerEnabled = false;
        settings.bedBreakerEnabled = false;
        settings.middleClickPearlEnabled = false;
        settings.killAuraEnabled = false;
        settings.autoFightEnabled = false;
        settings.criticalsEnabled = false;
        settings.autoPotEnabled2 = false;
        settings.autoArmorEnabled = false;
        settings.reachEnabled = false;
        settings.aimAssistEnabled = false;
        settings.autoClickerEnabled = false;
        settings.autoTapEnabled = false;
        settings.velocityEnabled = false;
        settings.autoPotEnabled3 = false;
        settings.espEnabled = false;
        settings.xrayEnabled = false;
        settings.fullbrightEnabled = false;
        settings.chamsEnabled = false;
        settings.nochamsEnabled = false;
        settings.hiteffectEnabled = false;
        settings.hitIndicatorEnabled = false;
        settings.hitShowEnabled = false;
        settings.armorEnabled = false;
        settings.hitboxEnabled = false;
        settings.nameTagsEnabled = false;
        settings.nameTagsCustomFont = false;
        settings.nameTagsCustomColor = false;
        settings.pluginsEnabled = false;
        settings.stepEnabled = false;
        settings.noslowEnabled = false;
        settings.nolimitsEnabled = false;
        settings.droneEnabled = false;
        settings.fpsCounterEnabled = false;
        settings.cpsCounterEnabled = false;
        settings.keystrokesEnabled = false;
        settings.coordinatesEnabled = false;
        settings.pingEnabled = false;
        settings.directionEnabled = false;
        settings.zoomEnabled = false;
        settings.fullbrightEnabled2 = false;
        settings.toggleSprintEnabled = false;
        settings.toggleSneakEnabled = false;
        settings.crosshairCustomEnabled = false;
        settings.itemAnimationsEnabled = false;
        settings.statusOverlayEnabled = false;
        settings.autoWingsEnabled = false;
        settings.autoCapeEnabled = false;
    }

    public void saveSettings() {
        // Save to file
    }

    public EaglerToneSettings getSettings() {
        return settings;
    }

    public void setSettings(EaglerToneSettings settings) {
        this.settings = settings;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean isInitialized() {
        return isInitialized.get();
    }

    public int getFPS() {
        return fps;
    }

    public int getPing() {
        return ping;
    }

    public String getCommandsList() {
        StringBuilder sb = new StringBuilder();
        for (EaglerToneCommand cmd : commands) {
            sb.append(cmd.getName()).append(" - ").append(cmd.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public List<EaglerToneCommand> getCommands() {
        return new ArrayList<>(commands);
    }

    public void addCommand(EaglerToneCommand command) {
        commands.add(command);
    }

    public void removeCommand(EaglerToneCommand command) {
        commands.remove(command);
    }

    public Map<String, EaglerTonePlugin> getPlugins() {
        return new HashMap<>(plugins);
    }

    public void addPlugin(EaglerTonePlugin plugin) {
        plugins.put(plugin.getName(), plugin);
    }

    public void removePlugin(EaglerTonePlugin plugin) {
        plugins.remove(plugin.getName());
    }

    public EaglerTonePlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public List<String> getDisabledPlugins() {
        return new ArrayList<>(disabledPlugins);
    }

    public void addDisabledPlugin(String name) {
        disabledPlugins.add(name);
    }

    public void removeDisabledPlugin(String name) {
        disabledPlugins.remove(name);
    }

    public void togglePlugin(String name) {
        EaglerTonePlugin plugin = plugins.get(name);
        if (plugin != null) {
            plugin.toggle();
        }
    }

    public void enablePlugin(String name) {
        EaglerTonePlugin plugin = plugins.get(name);
        if (plugin != null) {
            plugin.enable();
        }
    }

    public void disablePlugin(String name) {
        EaglerTonePlugin plugin = plugins.get(name);
        if (plugin != null) {
            plugin.disable();
        }
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];

        for (EaglerToneCommand c : commands) {
            if (c.getName().equalsIgnoreCase(cmd)) {
                c.execute(parts);
                return;
            }
        }
    }

    public void onPlayerDamage(Entity hurtFoo, DamageSource damageSource, float amount) {
        if (mc.player == null || hurtFoo == null) return;

        if (settings.autoTotemEnabled && mc.player.isBurning()) {
            useTotem();
        }

        if (settings.autoArmorEnabled && mc.player.inventory.armorInventory != null) {
            equipArmor();
        }
    }

    private void useTotem() {
        // Use totem of undying
    }

    private void equipArmor() {
        // Equip armor
    }

    public void onEntityCollided(Entity entity) {
        // Handle entity collision
    }

    public void onBlockBreak(net.minecraft.block.Block block, int x, int y, int z) {
        // Handle block break
    }

    public void onBlockPlace(net.minecraft.block.Block block, int x, int y, int z) {
        // Handle block place
    }

    public void onPlayerInteract(Entity entity) {
        // Handle player interact
    }

    public void onTick() {
        if (!isRunning.get()) return;

        updateMovement();
        updateCombat();
        updateVisuals();
        updateHUD();
        updatePlugins();
    }

    private void updateMovement() {
        if (settings.noSlowEnabled) {
            mc.player.movementInput = new MovementInput();
            mc.player.moveStrafing = 0.0f;
            mc.player.moveForward = 0.0f;
        }

        if (settings.noSlowdownEnabled) {
            // No slowdown
        }

        if (settings.noRotateEnabled) {
            // No rotate
        }

        if (settings.autoStrafeEnabled) {
            // Auto strafe
        }

        if (settings.bunnyHopEnabled) {
            // Bunny hop
        }

        if (settings.flyEnabled) {
            // Fly
        }

        if (settings.phaseEnabled) {
            // Phase
        }

        if (settings.spiderEnabled) {
            // Spider
        }

        if (settings.longJumpEnabled) {
            // Long jump
        }

        if (settings.scaffoldEnabled) {
            // Scaffold
        }

        if (settings.liquidWalkEnabled) {
            // Liquid walk
        }

        if (settings.jesusEnabled) {
            // Jesus
        }

        if (settings.boatFlyEnabled) {
            // Boat fly
        }

        if (settings.glideEnabled) {
            // Glide
        }

        if (settings.speedEnabled) {
            // Speed
        }

        if (settings.sprintEnabled) {
            // Sprint
        }

        if (settings.fastClimbEnabled) {
            // Fast climb
        }

        if (settings.phaseEnabled2) {
            // Phase 2
        }
    }

    private void updateCombat() {
        if (settings.killAuraEnabled) {
            // Kill aura
        }

        if (settings.autoFightEnabled) {
            // Auto fight
        }

        if (settings.criticalsEnabled) {
            // Criticals
        }

        if (settings.autoPotEnabled2 || settings.autoPotEnabled3) {
            // Auto pot
        }

        if (settings.autoArmorEnabled) {
            // Auto armor
        }

        if (settings.reachEnabled) {
            // Reach
        }

        if (settings.aimAssistEnabled) {
            // Aim assist
        }

        if (settings.autoClickerEnabled) {
            // Auto clicker
        }

        if (settings.autoTapEnabled) {
            // Auto tap
        }

        if (settings.velocityEnabled) {
            // Velocity
        }
    }

    private void updateVisuals() {
        if (settings.espEnabled) {
            // ESP
        }

        if (settings.xrayEnabled) {
            // X-ray
        }

        if (settings.fullbrightEnabled || settings.fullbrightEnabled2) {
            // Fullbright
        }

        if (settings.chamsEnabled) {
            // Chams
        }

        if (settings.nochamsEnabled) {
            // No chams
        }

        if (settings.hiteffectEnabled) {
            // Hit effect
        }

        if (settings.hitIndicatorEnabled) {
            // Hit indicator
        }

        if (settings.hitShowEnabled) {
            // Hit show
        }

        if (settings.armorEnabled) {
            // Armor
        }

        if (settings.hitboxEnabled) {
            // Hitbox
        }

        if (settings.nameTagsEnabled) {
            // Name tags
        }

        if (settings.nameTagsCustomFont) {
            // Custom font
        }

        if (settings.nameTagsCustomColor) {
            // Custom color
        }
    }

    private void updateHUD() {
        if (settings.fpsCounterEnabled) {
            // FPS counter
        }

        if (settings.cpsCounterEnabled) {
            // CPS counter
        }

        if (settings.keystrokesEnabled) {
            // Keystrokes
        }

        if (settings.coordinatesEnabled) {
            // Coordinates
        }

        if (settings.pingEnabled) {
            // Ping
        }

        if (settings.directionEnabled) {
            // Direction
        }

        if (settings.zoomEnabled) {
            // Zoom
        }

        if (settings.toggleSprintEnabled) {
            // Toggle sprint
        }

        if (settings.toggleSneakEnabled) {
            // Toggle sneak
        }

        if (settings.crosshairCustomEnabled) {
            // Crosshair custom
        }

        if (settings.itemAnimationsEnabled) {
            // Item animations
        }

        if (settings.statusOverlayEnabled) {
            // Status overlay
        }

        if (settings.autoWingsEnabled) {
            // Auto wings
        }

        if (settings.autoCapeEnabled) {
            // Auto cape
        }
    }

    public double getMaxBps() {
        return maxBps;
    }

    public void setMaxBps(double maxBps) {
        this.maxBps = maxBps;
    }

    public double getMaxClimbBps() {
        return maxClimbBps;
    }

    public void setMaxClimbBps(double maxClimbBps) {
        this.maxClimbBps = maxClimbBps;
    }

    public double getMaxFallBps() {
        return maxFallBps;
    }

    public void setMaxFallBps(double maxFallBps) {
        this.maxFallBps = maxFallBps;
    }

    public double getMaxLiftBps() {
        return maxLiftBps;
    }

    public void setMaxLiftBps(double maxLiftBps) {
        this.maxLiftBps = maxLiftBps;
    }

    public double getVerticalBps() {
        return verticalBps;
    }

    public void setVerticalBps(double verticalBps) {
        this.verticalBps = verticalBps;
    }

    public boolean isAntiFlingEnabled() {
        return antiFlingEnabled;
    }

    public void setAntiFlingEnabled(boolean antiFlingEnabled) {
        this.antiFlingEnabled = antiFlingEnabled;
    }

    public boolean isAntiKickEnabled() {
        return antiKickEnabled;
    }

    public void setAntiKickEnabled(boolean antiKickEnabled) {
        this.antiKickEnabled = antiKickEnabled;
    }

    public double getAntiKickDropBps() {
        return antiKickDropBps;
    }

    public void setAntiKickDropBps(double antiKickDropBps) {
        this.antiKickDropBps = antiKickDropBps;
    }

    public float getSmoothingPercent() {
        return smoothingPercent;
    }

    public void setSmoothingPercent(float smoothingPercent) {
        this.smoothingPercent = smoothingPercent;
    }

    public boolean isSpeedLimitEnabled() {
        return speedLimitEnabled;
    }

    public void setSpeedLimitEnabled(boolean speedLimitEnabled) {
        this.speedLimitEnabled = speedLimitEnabled;
    }

    public double getSpeedCapBps() {
        return speedCapBps;
    }

    public void setSpeedCapBps(double speedCapBps) {
        this.speedCapBps = speedCapBps;
    }

    public double getHardCap() {
        return hardCap;
    }

    public void setHardCap(double hardCap) {
        this.hardCap = hardCap;
    }

    public boolean isNoSlowEnabled() {
        return noSlowEnabled;
    }

    public void setNoSlowEnabled(boolean noSlowEnabled) {
        this.noSlowEnabled = noSlowEnabled;
    }

    public boolean isNoSlowdownEnabled() {
        return noSlowdownEnabled;
    }

    public void setNoSlowdownEnabled(boolean noSlowdownEnabled) {
        this.noSlowdownEnabled = noSlowdownEnabled;
    }

    public boolean isNoRotateEnabled() {
        return noRotateEnabled;
    }

    public void setNoRotateEnabled(boolean noRotateEnabled) {
        this.noRotateEnabled = noRotateEnabled;
    }

    public boolean isAutoStrafeEnabled() {
        return autoStrafeEnabled;
    }

    public void setAutoStrafeEnabled(boolean autoStrafeEnabled) {
        this.autoStrafeEnabled = autoStrafeEnabled;
    }

    public boolean isAutoStrafeReverse() {
        return autoStrafeReverse;
    }

    public void setAutoStrafeReverse(boolean autoStrafeReverse) {
        this.autoStrafeReverse = autoStrafeReverse;
    }

    public boolean isAutoStrafeLeft() {
        return autoStrafeLeft;
    }

    public void setAutoStrafeLeft(boolean autoStrafeLeft) {
        this.autoStrafeLeft = autoStrafeLeft;
    }

    public boolean isAutoStrafeRight() {
        return autoStrafeRight;
    }

    public void setAutoStrafeRight(boolean autoStrafeRight) {
        this.autoStrafeRight = autoStrafeRight;
    }

    public boolean isAutoStrafeStrafeOnly() {
        return autoStrafeStrafeOnly;
    }

    public void setAutoStrafeStrafeOnly(boolean autoStrafeStrafeOnly) {
        this.autoStrafeStrafeOnly = autoStrafeStrafeOnly;
    }

    public boolean isBunnyHopEnabled() {
        return bunnyHopEnabled;
    }

    public void setBunnyHopEnabled(boolean bunnyHopEnabled) {
        this.bunnyHopEnabled = bunnyHopEnabled;
    }

    public boolean isAutoJumpEnabled() {
        return autoJumpEnabled;
    }

    public void setAutoJumpEnabled(boolean autoJumpEnabled) {
        this.autoJumpEnabled = autoJumpEnabled;
    }

    public boolean isDisableAutoJumpEnabled() {
        return disableAutoJumpEnabled;
    }

    public void setDisableAutoJumpEnabled(boolean disableAutoJumpEnabled) {
        this.disableAutoJumpEnabled = disableAutoJumpEnabled;
    }

    public boolean isFlyEnabled() {
        return flyEnabled;
    }

    public void setFlyEnabled(boolean flyEnabled) {
        this.flyEnabled = flyEnabled;
    }

    public boolean isPhaseEnabled() {
        return phaseEnabled;
    }

    public void setPhaseEnabled(boolean phaseEnabled) {
        this.phaseEnabled = phaseEnabled;
    }

    public boolean isSpiderEnabled() {
        return spiderEnabled;
    }

    public void setSpiderEnabled(boolean spiderEnabled) {
        this.spiderEnabled = spiderEnabled;
    }

    public boolean isLongJumpEnabled() {
        return longJumpEnabled;
    }

    public void setLongJumpEnabled(boolean longJumpEnabled) {
        this.longJumpEnabled = longJumpEnabled;
    }

    public boolean isScaffoldEnabled() {
        return scaffoldEnabled;
    }

    public void setScaffoldEnabled(boolean scaffoldEnabled) {
        this.scaffoldEnabled = scaffoldEnabled;
    }

    public boolean isLiquidWalkEnabled() {
        return liquidWalkEnabled;
    }

    public void setLiquidWalkEnabled(boolean liquidWalkEnabled) {
        this.liquidWalkEnabled = liquidWalkEnabled;
    }

    public boolean isJesusEnabled() {
        return jesusEnabled;
    }

    public void setJesusEnabled(boolean jesusEnabled) {
        this.jesusEnabled = jesusEnabled;
    }

    public boolean isBoatFlyEnabled() {
        return boatFlyEnabled;
    }

    public void setBoatFlyEnabled(boolean boatFlyEnabled) {
        this.boatFlyEnabled = boatFlyEnabled;
    }

    public boolean isGlideEnabled() {
        return glideEnabled;
    }

    public void setGlideEnabled(boolean glideEnabled) {
        this.glideEnabled = glideEnabled;
    }

    public boolean isSpeedEnabled() {
        return speedEnabled;
    }

    public void setSpeedEnabled(boolean speedEnabled) {
        this.speedEnabled = speedEnabled;
    }

    public boolean isSprintEnabled() {
        return sprintEnabled;
    }

    public void setSprintEnabled(boolean sprintEnabled) {
        this.sprintEnabled = sprintEnabled;
    }

    public boolean isFastClimbEnabled() {
        return fastClimbEnabled;
    }

    public void setFastClimbEnabled(boolean fastClimbEnabled) {
        this.fastClimbEnabled = fastClimbEnabled;
    }

    public boolean isPhaseEnabled2() {
        return phaseEnabled2;
    }

    public void setPhaseEnabled2(boolean phaseEnabled2) {
        this.phaseEnabled2 = phaseEnabled2;
    }

    public boolean isAutoTotemEnabled() {
        return autoTotemEnabled;
    }

    public void setAutoTotemEnabled(boolean autoTotemEnabled) {
        this.autoTotemEnabled = autoTotemEnabled;
    }

    public boolean isFastEatEnabled() {
        return fastEatEnabled;
    }

    public void setFastEatEnabled(boolean fastEatEnabled) {
        this.fastEatEnabled = fastEatEnabled;
    }

    public boolean isInvMoveEnabled() {
        return invMoveEnabled;
    }

    public void setInvMoveEnabled(boolean invMoveEnabled) {
        this.invMoveEnabled = invMoveEnabled;
    }

    public boolean isAutoToolEnabled() {
        return autoToolEnabled;
    }

    public void setAutoToolEnabled(boolean autoToolEnabled) {
        this.autoToolEnabled = autoToolEnabled;
    }

    public boolean isFastPlaceEnabled() {
        return fastPlaceEnabled;
    }

    public void setFastPlaceEnabled(boolean fastPlaceEnabled) {
        this.fastPlaceEnabled = fastPlaceEnabled;
    }

    public boolean isFastBreakEnabled() {
        return fastBreakEnabled;
    }

    public void setFastBreakEnabled(boolean fastBreakEnabled) {
        this.fastBreakEnabled = fastBreakEnabled;
    }

    public boolean isAntiAfkEnabled() {
        return antiAfkEnabled;
    }

    public void setAntiAfkEnabled(boolean antiAfkEnabled) {
        this.antiAfkEnabled = antiAfkEnabled;
    }

    public boolean isAutoGGEnabled() {
        return autoGGEnabled;
    }

    public void setAutoGGEnabled(boolean autoGGEnabled) {
        this.autoGGEnabled = autoGGEnabled;
    }

    public boolean isAutoFishEnabled() {
        return autoFishEnabled;
    }

    public void setAutoFishEnabled(boolean autoFishEnabled) {
        this.autoFishEnabled = autoFishEnabled;
    }

    public boolean isAutoPotEnabled() {
        return autoPotEnabled;
    }

    public void setAutoPotEnabled(boolean autoPotEnabled) {
        this.autoPotEnabled = autoPotEnabled;
    }

    public boolean isChestStealerEnabled() {
        return chestStealerEnabled;
    }

    public void setChestStealerEnabled(boolean chestStealerEnabled) {
        this.chestStealerEnabled = chestStealerEnabled;
    }

    public boolean isBedBreakerEnabled() {
        return bedBreakerEnabled;
    }

    public void setBedBreakerEnabled(boolean bedBreakerEnabled) {
        this.bedBreakerEnabled = bedBreakerEnabled;
    }

    public boolean isMiddleClickPearlEnabled() {
        return middleClickPearlEnabled;
    }

    public void setMiddleClickPearlEnabled(boolean middleClickPearlEnabled) {
        this.middleClickPearlEnabled = middleClickPearlEnabled;
    }

    public boolean isKillAuraEnabled() {
        return killAuraEnabled;
    }

    public void setKillAuraEnabled(boolean killAuraEnabled) {
        this.killAuraEnabled = killAuraEnabled;
    }

    public boolean isAutoFightEnabled() {
        return autoFightEnabled;
    }

    public void setAutoFightEnabled(boolean autoFightEnabled) {
        this.autoFightEnabled = autoFightEnabled;
    }

    public boolean isCriticalsEnabled() {
        return criticalsEnabled;
    }

    public void setCriticalsEnabled(boolean criticalsEnabled) {
        this.criticalsEnabled = criticalsEnabled;
    }

    public boolean isAutoPotEnabled2() {
        return autoPotEnabled2;
    }

    public void setAutoPotEnabled2(boolean autoPotEnabled2) {
        this.autoPotEnabled2 = autoPotEnabled2;
    }

    public boolean isAutoArmorEnabled() {
        return autoArmorEnabled;
    }

    public void setAutoArmorEnabled(boolean autoArmorEnabled) {
        this.autoArmorEnabled = autoArmorEnabled;
    }

    public boolean isReachEnabled() {
        return reachEnabled;
    }

    public void setReachEnabled(boolean reachEnabled) {
        this.reachEnabled = reachEnabled;
    }

    public boolean isAimAssistEnabled() {
        return aimAssistEnabled;
    }

    public void setAimAssistEnabled(boolean aimAssistEnabled) {
        this.aimAssistEnabled = aimAssistEnabled;
    }

    public boolean isAutoClickerEnabled() {
        return autoClickerEnabled;
    }

    public void setAutoClickerEnabled(boolean autoClickerEnabled) {
        this.autoClickerEnabled = autoClickerEnabled;
    }

    public boolean isAutoTapEnabled() {
        return autoTapEnabled;
    }

    public void setAutoTapEnabled(boolean autoTapEnabled) {
        this.autoTapEnabled = autoTapEnabled;
    }

    public boolean isVelocityEnabled() {
        return velocityEnabled;
    }

    public void setVelocityEnabled(boolean velocityEnabled) {
        this.velocityEnabled = velocityEnabled;
    }

    public boolean isAutoPotEnabled3() {
        return autoPotEnabled3;
    }

    public void setAutoPotEnabled3(boolean autoPotEnabled3) {
        this.autoPotEnabled3 = autoPotEnabled3;
    }

    public boolean isEspEnabled() {
        return espEnabled;
    }

    public void setEspEnabled(boolean espEnabled) {
        this.espEnabled = espEnabled;
    }

    public boolean isXrayEnabled() {
        return xrayEnabled;
    }

    public void setXrayEnabled(boolean xrayEnabled) {
        this.xrayEnabled = xrayEnabled;
    }

    public boolean isFullbrightEnabled() {
        return fullbrightEnabled;
    }

    public void setFullbrightEnabled(boolean fullbrightEnabled) {
        this.fullbrightEnabled = fullbrightEnabled;
    }

    public boolean isChamsEnabled() {
        return chamsEnabled;
    }

    public void setChamsEnabled(boolean chamsEnabled) {
        this.chamsEnabled = chamsEnabled;
    }

    public boolean isNochamsEnabled() {
        return nochamsEnabled;
    }

    public void setNochamsEnabled(boolean nochamsEnabled) {
        this.nochamsEnabled = nochamsEnabled;
    }

    public boolean isHiteffectEnabled() {
        return hiteffectEnabled;
    }

    public void setHiteffectEnabled(boolean hiteffectEnabled) {
        this.hiteffectEnabled = hiteffectEnabled;
    }

    public boolean isHitIndicatorEnabled() {
        return hitIndicatorEnabled;
    }

    public void setHitIndicatorEnabled(boolean hitIndicatorEnabled) {
        this.hitIndicatorEnabled = hitIndicatorEnabled;
    }

    public boolean isHitShowEnabled() {
        return hitShowEnabled;
    }

    public void setHitShowEnabled(boolean hitShowEnabled) {
        this.hitShowEnabled = hitShowEnabled;
    }

    public boolean isArmorEnabled() {
        return armorEnabled;
    }

    public void setArmorEnabled(boolean armorEnabled) {
        this.armorEnabled = armorEnabled;
    }

    public boolean isHitboxEnabled() {
        return hitboxEnabled;
    }

    public void setHitboxEnabled(boolean hitboxEnabled) {
        this.hitboxEnabled = hitboxEnabled;
    }

    public boolean isNameTagsEnabled() {
        return nameTagsEnabled;
    }

    public void setNameTagsEnabled(boolean nameTagsEnabled) {
        this.nameTagsEnabled = nameTagsEnabled;
    }

    public boolean isNameTagsCustomFont() {
        return nameTagsCustomFont;
    }

    public void setNameTagsCustomFont(boolean nameTagsCustomFont) {
        this.nameTagsCustomFont = nameTagsCustomFont;
    }

    public boolean isNameTagsCustomColor() {
        return nameTagsCustomColor;
    }

    public void setNameTagsCustomColor(boolean nameTagsCustomColor) {
        this.nameTagsCustomColor = nameTagsCustomColor;
    }

    public boolean isPluginsEnabled() {
        return pluginsEnabled;
    }

    public void setPluginsEnabled(boolean pluginsEnabled) {
        this.pluginsEnabled = pluginsEnabled;
    }

    public boolean isStepEnabled() {
        return stepEnabled;
    }

    public void setStepEnabled(boolean stepEnabled) {
        this.stepEnabled = stepEnabled;
    }

    public boolean isNoslowEnabled() {
        return noslowEnabled;
    }

    public void setNoslowEnabled(boolean noslowEnabled) {
        this.noslowEnabled = noslowEnabled;
    }

    public boolean isNolimitsEnabled() {
        return nolimitsEnabled;
    }

    public void setNolimitsEnabled(boolean nolimitsEnabled) {
        this.nolimitsEnabled = nolimitsEnabled;
    }

    public boolean isDroneEnabled() {
        return droneEnabled;
    }

    public void setDroneEnabled(boolean droneEnabled) {
        this.droneEnabled = droneEnabled;
    }

    public boolean isFpsCounterEnabled() {
        return fpsCounterEnabled;
    }

    public void setFpsCounterEnabled(boolean fpsCounterEnabled) {
        this.fpsCounterEnabled = fpsCounterEnabled;
    }

    public boolean isCpsCounterEnabled() {
        return cpsCounterEnabled;
    }

    public void setCpsCounterEnabled(boolean cpsCounterEnabled) {
        this.cpsCounterEnabled = cpsCounterEnabled;
    }

    public boolean isKeystrokesEnabled() {
        return keystrokesEnabled;
    }

    public void setKeystrokesEnabled(boolean keystrokesEnabled) {
        this.keystrokesEnabled = keystrokesEnabled;
    }

    public boolean isCoordinatesEnabled() {
        return coordinatesEnabled;
    }

    public void setCoordinatesEnabled(boolean coordinatesEnabled) {
        this.coordinatesEnabled = coordinatesEnabled;
    }

    public boolean isPingEnabled() {
        return pingEnabled;
    }

    public void setPingEnabled(boolean pingEnabled) {
        this.pingEnabled = pingEnabled;
    }

    public boolean isDirectionEnabled() {
        return directionEnabled;
    }

    public void setDirectionEnabled(boolean directionEnabled) {
        this.directionEnabled = directionEnabled;
    }

    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public boolean isFullbrightEnabled2() {
        return fullbrightEnabled2;
    }

    public void setFullbrightEnabled2(boolean fullbrightEnabled2) {
        this.fullbrightEnabled2 = fullbrightEnabled2;
    }

    public boolean isToggleSprintEnabled() {
        return toggleSprintEnabled;
    }

    public void setToggleSprintEnabled(boolean toggleSprintEnabled) {
        this.toggleSprintEnabled = toggleSprintEnabled;
    }

    public boolean isToggleSneakEnabled() {
        return toggleSneakEnabled;
    }

    public void setToggleSneakEnabled(boolean toggleSneakEnabled) {
        this.toggleSneakEnabled = toggleSneakEnabled;
    }

    public boolean isCrosshairCustomEnabled() {
        return crosshairCustomEnabled;
    }

    public void setCrosshairCustomEnabled(boolean crosshairCustomEnabled) {
        this.crosshairCustomEnabled = crosshairCustomEnabled;
    }

    public boolean isItemAnimationsEnabled() {
        return itemAnimationsEnabled;
    }

    public void setItemAnimationsEnabled(boolean itemAnimationsEnabled) {
        this.itemAnimationsEnabled = itemAnimationsEnabled;
    }

    public boolean isStatusOverlayEnabled() {
        return statusOverlayEnabled;
    }

    public void setStatusOverlayEnabled(boolean statusOverlayEnabled) {
        this.statusOverlayEnabled = statusOverlayEnabled;
    }

    public boolean isAutoWingsEnabled() {
        return autoWingsEnabled;
    }

    public void setAutoWingsEnabled(boolean autoWingsEnabled) {
        this.autoWingsEnabled = autoWingsEnabled;
    }

    public boolean isAutoCapeEnabled() {
        return autoCapeEnabled;
    }

    public void setAutoCapeEnabled(boolean autoCapeEnabled) {
        this.autoCapeEnabled = autoCapeEnabled;
    }
}
