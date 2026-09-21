package net.moonlit.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class MoonlitSettings extends GuiScreen {

    private static final int CATEGORY_NONE = 0;
    private static final int CATEGORY_GRAPHICS = 1;
    private static final int CATEGORY_PERFORMANCE = 2;
    private static final int CATEGORY_VIDEO = 3;
    private static final int CATEGORY_AUDIO = 4;
    private static final int CATEGORY_CONTROLS = 5;
    private static final int CATEGORY_INTERFACE = 6;
    private static final int CATEGORY_HUD = 7;
    private static final int CATEGORY_ACCESSIBILITY = 8;
    private static final int CATEGORY_MOONLIT = 9;

    private int activeCategory = CATEGORY_GRAPHICS;
    private final List<MoonlitSettingsCategory> categories = new ArrayList<>();
    private GuiButton lastButtonClicked;

    private MoonlitButton btnGraphics;
    private MoonlitButton btnPerformance;
    private MoonlitButton btnVideo;
    private MoonlitButton btnAudio;
    private MoonlitButton btnControls;
    private MoonlitButton btnInterface;
    private MoonlitButton btnHUD;
    private MoonlitButton btnAccessibility;
    private MoonlitButton btnMoonlit;

    private MoonlitTab selectedTab;
    private MoonlitPanel settingsPanel;
    private MoonlitScrollPanel scrollPanel;

    private Minecraft mc;
    private FontRenderer fontRenderer;

    private int activeTabIndex = 0;

    public MoonlitSettings(GuiScreen parent) {
        super(parent);
        this.mc = parent.mc;
        this.fontRenderer = parent.fontRenderer;
    }

    @Override
    public void initGui() {
        int width = this.width;
        int height = this.height;

        int centerX = width / 2;
        int leftX = centerX - 250;
        int categoryWidth = 150;
        int categoryHeight = 25;
        int categorySpacing = 6;

        categories.clear();

        btnGraphics = new MoonlitButton(mc, fontRenderer, leftX, 20, categoryWidth, categoryHeight, "Graphics");
        btnGraphics.setOnClick(() -> setCategory(CATEGORY_GRAPHICS));
        categories.add(new MoonlitSettingsCategory(CATEGORY_GRAPHICS, "Graphics", btnGraphics));

        btnPerformance = new MoonlitButton(mc, fontRenderer, leftX, 20 + categoryHeight + categorySpacing, categoryWidth, categoryHeight, "Performance");
        btnPerformance.setOnClick(() -> setCategory(CATEGORY_PERFORMANCE));
        categories.add(new MoonlitSettingsCategory(CATEGORY_PERFORMANCE, "Performance", btnPerformance));

        btnVideo = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 2, categoryWidth, categoryHeight, "Video");
        btnVideo.setOnClick(() -> setCategory(CATEGORY_VIDEO));
        categories.add(new MoonlitSettingsCategory(CATEGORY_VIDEO, "Video", btnVideo));

        btnAudio = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 3, categoryWidth, categoryHeight, "Audio");
        btnAudio.setOnClick(() -> setCategory(CATEGORY_AUDIO));
        categories.add(new MoonlitSettingsCategory(CATEGORY_AUDIO, "Audio", btnAudio));

        btnControls = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 4, categoryWidth, categoryHeight, "Controls");
        btnControls.setOnClick(() -> setCategory(CATEGORY_CONTROLS));
        categories.add(new MoonlitSettingsCategory(CATEGORY_CONTROLS, "Controls", btnControls));

        btnInterface = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 5, categoryWidth, categoryHeight, "Interface");
        btnInterface.setOnClick(() -> setCategory(CATEGORY_INTERFACE));
        categories.add(new MoonlitSettingsCategory(CATEGORY_INTERFACE, "Interface", btnInterface));

        btnHUD = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 6, categoryWidth, categoryHeight, "HUD");
        btnHUD.setOnClick(() -> setCategory(CATEGORY_HUD));
        categories.add(new MoonlitSettingsCategory(CATEGORY_HUD, "HUD", btnHUD));

        btnAccessibility = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 7, categoryWidth, categoryHeight, "Accessibility");
        btnAccessibility.setOnClick(() -> setCategory(CATEGORY_ACCESSIBILITY));
        categories.add(new MoonlitSettingsCategory(CATEGORY_ACCESSIBILITY, "Accessibility", btnAccessibility));

        btnMoonlit = new MoonlitButton(mc, fontRenderer, leftX, 20 + (categoryHeight + categorySpacing) * 8, categoryWidth, categoryHeight, "Moonlit");
        btnMoonlit.setOnClick(() -> setCategory(CATEGORY_MOONLIT));
        categories.add(new MoonlitSettingsCategory(CATEGORY_MOONLIT, "Moonlit", btnMoonlit));

        settingsPanel = new MoonlitPanel(mc, fontRenderer, leftX + categoryWidth + 10, 10, width - (leftX + categoryWidth + 10) - 10, height - 20);
        scrollPanel = new MoonlitScrollPanel(mc, fontRenderer, leftX + categoryWidth + 10, 10, width - (leftX + categoryWidth + 10) - 10, height - 20);

        rebuildCategoryPanel(CATEGORY_GRAPHICS);
    }

    private void setCategory(int category) {
        activeCategory = category;
        rebuildCategoryPanel(category);
    }

    private void rebuildCategoryPanel(int category) {
        scrollPanel.clearChildren();

        switch (category) {
            case CATEGORY_GRAPHICS:
                addGraphicsSettings();
                break;
            case CATEGORY_PERFORMANCE:
                addPerformanceSettings();
                break;
            case CATEGORY_VIDEO:
                addVideoSettings();
                break;
            case CATEGORY_AUDIO:
                addAudioSettings();
                break;
            case CATEGORY_CONTROLS:
                addControlsSettings();
                break;
            case CATEGORY_INTERFACE:
                addInterfaceSettings();
                break;
            case CATEGORY_HUD:
                addHUDSettings();
                break;
            case CATEGORY_ACCESSIBILITY:
                addAccessibilitySettings();
                break;
            case CATEGORY_MOONLIT:
                addMoonlitSettings();
                break;
        }
    }

    private void addGraphicsSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 200, 20, "Smooth Lighting"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 200, 20, "Particles"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 200, 20, "Clouds"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Entity Shadows"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Weather"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 200, 20, "Animations"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 160, 200, 20, "VSync"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 185, 200, 20, "Render Distance", 2, 16, 8));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 210, 200, 20, "GUI Scale", 0.5f, 2.0f, 1.0f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 235, 200, 20, "FPS Limit", 30, 240, 60));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 260, 200, 20, "Graphics Quality"));
    }

    private void addPerformanceSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 250, 20, "Potato Mode"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 250, 20, "Low Memory Mode"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 250, 20, "Potato Mode Enhanced"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 250, 20, "Low Memory Mode"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 250, 20, "Disable Blur"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 250, 20, "Disable Glow"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 160, 250, 20, "Disable Shadows"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 185, 250, 20, "Disable Animations"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 210, 250, 20, "Disable Custom UI"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 235, 250, 20, "Disable Custom HUD"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 260, 250, 20, "Disable Custom Menus"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 285, 250, 20, "Disable Custom Loading"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 310, 250, 20, "Disable Custom Resources"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 335, 250, 20, "Disable Texture Pack"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 360, 250, 20, "Disable Shader"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 385, 250, 20, "Disable Blur"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 410, 250, 20, "Disable Glow"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 435, 250, 20, "Disable Shadow"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 460, 250, 20, "Disable Animation"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 485, 250, 20, "Disable Custom Font"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 510, 250, 20, "Disable Unicode"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 535, 250, 20, "Disable Anti-Alias"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 560, 250, 20, "Disable Connected Texture"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 585, 250, 20, "Disable Blazeprints"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 610, 250, 20, "Disable Screen Shake"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 635, 250, 20, "Disable Handshake"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 660, 250, 20, "FPS Limit", 30, 240, 60));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 685, 250, 20, "Render Distance", 2, 16, 6));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 710, 250, 20, "Particle Lag", 0, 100, 50));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 735, 250, 20, "Performance Profile"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 760, 250, 20, "Reset Performance Settings"));
    }

    private void addVideoSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 250, 20, "Fullscreen"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 250, 20, "VSync"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 250, 20, "Triple Buffer"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 250, 20, "Enable Blazeprints"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 250, 20, "Disable Screen Handshake"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 250, 20, "Connected Texture"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 160, 250, 20, "Custom Font"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 185, 250, 20, "Unicode Font"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 210, 250, 20, "Anti-Aliasing"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 235, 250, 20, "Render Chunk Border"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 260, 250, 20, "GUI Scale", 0.5f, 2.0f, 1.0f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 285, 250, 20, "FOV", 70, 110, 90));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 310, 250, 20, "Render Distance", 2, 16, 8));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 335, 250, 20, "Brightness", 0.0f, 1.0f, 0.5f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 360, 250, 20, "Gamma", 0.0f, 1.0f, 0.5f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 385, 250, 20, "Smooth Lighting Level", 0.0f, 1.0f, 0.5f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 410, 250, 20, "Entity Shadow Brightness", 0.0f, 1.0f, 0.5f));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 435, 250, 20, "Particle Quality"));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 460, 250, 20, "Clouds Mode"));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 485, 250, 20, "Entity Shadow Mode"));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 510, 250, 20, "Video Mode"));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 535, 250, 20, "Anti-Alias Level"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 560, 250, 20, "Reset Video Settings"));
    }

    private void addAudioSettings() {
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 10, 200, 20, "Master Volume", 0, 100, 75));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 35, 200, 20, "Music Volume", 0, 100, 50));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 60, 200, 20, "Sound Effects", 0, 100, 75));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 85, 200, 20, "Ambient Volume", 0, 100, 50));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 110, 200, 20, "Voice Volume", 0, 100, 75));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 200, 20, "Music"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 160, 200, 20, "Sound Effects"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 185, 200, 20, "Ambient Sounds"));
    }

    private void addControlsSettings() {
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 10, 200, 20, "Key Bindings"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 35, 200, 20, "Sensitivity"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 60, 200, 20, "Mouse Sensitivity", 0.5f, 5.0f, 1.0f));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Invert Y-Axis"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Raw Input"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 200, 20, "Touchscreen Mode"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 160, 200, 20, "Reset Controls"));
    }

    private void addInterfaceSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 200, 20, "Show Coordinates"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 200, 20, "Show Ping"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 200, 20, "Show FPS"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Show CPS"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Show Crosshair"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 135, 200, 20, "Crosshair Size", 1, 10, 5));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 160, 200, 20, "HUD Style"));
    }

    private void addHUDSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 200, 20, "Show HUD"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 200, 20, "Show Player List"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 200, 20, "Show Chat"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Show Boss Bar"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Show Action Bar"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 200, 20, "Enable HUD Editor"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 160, 200, 20, "Open HUD Editor"));
    }

    private void addAccessibilitySettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 200, 20, "High Contrast"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 200, 20, "Large Text"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 200, 20, "Reduce Motion"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Screen Reader Support"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Colorblind Mode"));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 135, 200, 20, "Colorblind Type"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 160, 200, 20, "Text Size", 0.5f, 2.0f, 1.0f));
    }

    private void addMoonlitSettings() {
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 10, 200, 20, "Enable Moonlit"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 35, 200, 20, "Enable Custom UI"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 60, 200, 20, "Enable Animations"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 85, 200, 20, "Enable Blur"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 110, 200, 20, "Enable Glow"));
        scrollPanel.addChild(new MoonlitToggle(mc, fontRenderer, 10, 135, 200, 20, "Enable Shadows"));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 160, 200, 20, "UI Brightness", 0.5f, 1.5f, 1.0f));
        scrollPanel.addChild(new MoonlitSlider(mc, fontRenderer, 10, 185, 200, 20, "UI Scale", 0.5f, 2.0f, 1.0f));
        scrollPanel.addChild(new MoonlitDropdown(mc, fontRenderer, 10, 210, 200, 20, "Theme"));
        scrollPanel.addChild(new MoonlitButton(mc, fontRenderer, 10, 235, 200, 20, "Reset to Defaults"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            for (MoonlitSettingsCategory category : categories) {
                if (category.button == button) {
                    setCategory(category.categoryId);
                    return;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        for (MoonlitSettingsCategory category : categories) {
            category.button.render(mouseX, mouseY, partialTicks);
        }

        scrollPanel.render(mouseX, mouseY, partialTicks);

        if (fontRenderer != null) {
            fontRenderer.drawString("Settings", width / 2 - fontRenderer.getStringWidth("Settings") / 2, 4, 0xFFCCCCCC);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        scrollPanel.onMousePressed(mouseX, mouseY, mouseButton);

        for (MoonlitSettingsCategory category : categories) {
            if (category.button.contains(mouseX, mouseY)) {
                actionPerformed(category.button);
                break;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {}

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class MoonlitSettingsCategory {
        final int categoryId;
        final String name;
        final MoonlitButton button;

        MoonlitSettingsCategory(int categoryId, String name, MoonlitButton button) {
            this.categoryId = categoryId;
            this.name = name;
            this.button = button;
        }
    }
}
