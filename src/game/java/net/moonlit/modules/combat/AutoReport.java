package net.moonlit.modules.combat;

import net.moonlit.modules.MoonlitModule;

public class AutoReport extends MoonlitModule {

    private int reportDelay = 1000;
    private long lastReportTime = 0;

    public AutoReport() {
        super("AutoReport", "Combat");
        setDescription("Automatically reports players when you die");
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
        lastReportTime = 0;
    }

    @Override
    public void update() {
        // Would check for player kills here
    }

    @Override
    public void render() {
        // No rendering needed for this module
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
