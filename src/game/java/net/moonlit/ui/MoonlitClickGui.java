package net.moonlit.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.moonlit.animation.MoonlitAnimation;
import net.moonlit.modules.MoonlitModule;
import net.moonlit.modules.MoonlitModuleManager;

public class MoonlitClickGui extends GuiScreen {

    private static final String[] CATEGORIES = {"Combat", "Movement", "Player", "Render", "World", "Misc", "HUD", "Client"};
    private static final int SIDEBAR_WIDTH = 160;
    private static final int MODULE_CARD_WIDTH = 200;
    private static final int MODULE_CARD_HEIGHT = 80;

    private String selectedCategory = "Combat";
    private List<MoonlitModule> modulesInCategory = new ArrayList<>();
    private MoonlitModule selectedModule = null;
    private boolean showModuleSettings = false;

    private MoonlitTextField searchField;
    private List<MoonlitButton> categoryButtons = new ArrayList<>();
    private final List<GuiButton> buttonList = new ArrayList<>();
    private MoonlitPanel modulePanel;
    private MoonlitPanel settingsPanel;
    private boolean guiOpen = false;
    private float guiAlpha = 0.8f;

    public MoonlitClickGui() {
        searchField = new MoonlitTextField(mc, mc.fontRenderer, 200, 10, 180, 20, "Search modules...");
    }

    @Override
    public void initGui() {
        int width = this.width;
        int height = this.height;

        int startY = 50;
        int spacing = 25;

        categoryButtons.clear();
        for (int i = 0; i < CATEGORIES.length; i++) {
            String category = CATEGORIES[i];
            int x = 10;
            int y = startY + i * spacing;
            MoonlitButton btn = new MoonlitButton(mc, mc.fontRenderer, x, y, SIDEBAR_WIDTH - 20, 20, getCategoryDisplay(category));
            btn.setOnClick(() -> selectCategory(category));
            btn.setMinWidth(SIDEBAR_WIDTH - 20);
            categoryButtons.add(btn);
            buttonList.add(btn);
        }

        searchField = new MoonlitTextField(mc, mc.fontRenderer, 20, 10, 160, 20, "Search modules...");
        searchField.setOnChange((text) -> filterModules(text));

        modulePanel = new MoonlitPanel(mc, mc.fontRenderer, SIDEBAR_WIDTH + 20, 50, width - SIDEBAR_WIDTH - 40, height - 100);
        modulePanel.setBackgroundColor(0xFF0A0812);
        modulePanel.setBorderColor(0xFF2A2040);
        modulePanel.setRadius(12);
        modulePanel.setDrawBackground(true);

        settingsPanel = new MoonlitPanel(mc, mc.fontRenderer, width / 2 - 150, height / 2 - 100, 300, 200);
        settingsPanel.setVisible(false);
        settingsPanel.setBackgroundColor(0xFF0A0812);
        settingsPanel.setBorderColor(0xFF2A2040);
        settingsPanel.setRadius(12);
        settingsPanel.setDrawBackground(true);

        guiOpen = true;
    }

    private String getCategoryDisplay(String category) {
        switch (category) {
            case "Combat": return "\u2694  Combat";
            case "Movement": return "\u221F  Movement";
            case "Player": return "\u2659  Player";
            case "Render": return "\u25C8  Render";
            case "World": return "\u25A3  World";
            case "Misc": return "\u25C6  Misc";
            case "HUD": return "\u25A4  HUD";
            case "Client": return "\u2699  Client";
            default: return category;
        }
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        modulesInCategory.clear();
        for (MoonlitModule module : MoonlitModuleManager.getInstance().getModules()) {
            if (module.getCategory().equalsIgnoreCase(category)) {
                modulesInCategory.add(module);
            }
        }
    }

    private void filterModules(String query) {
        if (query == null || query.isEmpty()) {
            selectCategory(selectedCategory);
            return;
        }

        modulesInCategory.clear();
        for (MoonlitModule module : MoonlitModuleManager.getInstance().getModules()) {
            if (module.getName().toLowerCase().contains(query.toLowerCase()) ||
                module.getCategory().toLowerCase().contains(query.toLowerCase())) {
                modulesInCategory.add(module);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackgroundImage();

        drawClickGuiBackground();

        if (mc.fontRenderer != null) {
            int fps = mc.displayFps;
            int ping = 0;
            drawBottomInfo(mc.fontRenderer, fps, ping);
        }

        drawSidebar(mouseX, mouseY);
        drawModulePanel(mouseX, mouseY);
        drawSettingsPanel(mouseX, mouseY);

        searchField.render(mouseX, mouseY, partialTicks);
    }

    private void drawBackgroundImage() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moonlit:textures/gui/Moonlitbg.png"));
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        net.lax1dude.eaglercraft.minecraft.GuiButtonWithStupidIcons.drawTexturedModalRect(0, 0, 0, 0, width, height);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawClickGuiBackground() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        int overlayX = 0;
        int overlayY = 0;
        int overlayW = width;
        int overlayH = height;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(overlayX, overlayY, overlayW, overlayH, 0xCC000000, 0x00000000, 0.0f, 0.0f);

        GlStateManager.disableBlend();
    }

    private void drawSidebar(int mouseX, int mouseY) {
        int sidebarX = 10;
        int sidebarY = 40;
        int sidebarW = SIDEBAR_WIDTH;
        int sidebarH = height - 90;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(sidebarX, sidebarY, sidebarW, sidebarH, 0xCC0A0812, 0xFF2A2040, 1.0f, 12);

        if (mc.fontRenderer != null) {
            int titleX = sidebarX + sidebarW / 2;
            mc.fontRenderer.drawString("MOONLIT", titleX, sidebarY + 15, 0xFFCCCCCC);
            mc.fontRenderer.drawString("1.12.2", titleX, sidebarY + 30, 0xFF666666);
        }

        int iconX = sidebarX + 10;
        int iconY = sidebarY + 50;
        mc.fontRenderer.drawString("\u263E", iconX, iconY, 0xFF8888FF);

        for (MoonlitButton btn : categoryButtons) {
            btn.render(mouseX, mouseY, 0);

            if (btn.contains(mouseX, mouseY)) {
                int hoverBg = 0xFF151020;
                net.moonlit.render.MoonlitRenderer.drawRoundedRect(btn.getX() - 2, btn.getY() - 2, btn.getWidth() + 4, btn.getHeight() + 4, hoverBg, 0xFF443050, 1.0f, 6);
            }

            if (btn.displayString != null && btn.displayString.startsWith(getCategoryDisplay(selectedCategory).split(" ")[0])) {
                int accentX = btn.getX() - 4;
                int accentY = btn.getY() + 2;
                int accentH = btn.getHeight() - 4;
                net.moonlit.render.MoonlitRenderer.drawRoundedRect(accentX, accentY, 3, accentH, 0xFF8888FF, 0x00000000, 0.0f, 1.5f);
            }
        }
    }

    private void drawModulePanel(int mouseX, int mouseY) {
        if (modulePanel == null) return;

        modulePanel.render(mouseX, mouseY, 0);

        int panelX = modulePanel.getX() + 10;
        int panelY = modulePanel.getY() + 10;
        int cardSpacing = 10;

        int i = 0;
        for (MoonlitModule module : modulesInCategory) {
            int cardX = panelX + (i % 2) * (MODULE_CARD_WIDTH + cardSpacing);
            int cardY = panelY + (i / 2) * (MODULE_CARD_HEIGHT + cardSpacing);

            if (cardY + MODULE_CARD_HEIGHT > modulePanel.getY() + modulePanel.getHeight() - 10) break;

            drawModuleCard(cardX, cardY, module, mouseX, mouseY);

            i++;
        }
    }

    private void drawModuleCard(int x, int y, MoonlitModule module, int mouseX, int mouseY) {
        boolean isHovered = (x <= mouseX && mouseX < x + MODULE_CARD_WIDTH &&
                             y <= mouseY && mouseY < y + MODULE_CARD_HEIGHT);
        boolean isSelected = selectedModule == module;

        int bgColor = isHovered ? 0xFF151025 : 0xFF0D0A15;
        int borderColor = isSelected ? 0xFF8888FF : (isHovered ? 0xFF443050 : 0xFF2A2040);
        int toggleColor = module.isEnabled() ? 0xFF8888FF : 0xFF444444;

        net.moonlit.render.MoonlitRenderer.drawBorderedRoundedRect(x, y, MODULE_CARD_WIDTH, MODULE_CARD_HEIGHT, module.getRadius(), bgColor, borderColor, 1.0f, module.getRadius());

        if (mc.fontRenderer != null) {
            int textX = x + 10;
            int titleY = y + 15;

            mc.fontRenderer.drawString(module.getName(), textX, titleY, 0xFFCCCCCC);

            int toggleX = x + MODULE_CARD_WIDTH - 60;
            int toggleY = y + 15;
            int toggleSize = 12;

            net.moonlit.render.MoonlitRenderer.drawRoundedRect(toggleX, toggleY, toggleSize, toggleSize, toggleColor, 3.0f);
            mc.fontRenderer.drawString(module.isEnabled() ? "\u25CF" : "\u25CB", toggleX + toggleSize / 2 - 4, toggleY + toggleSize / 2 - 4, 0xFFCCCCCC);

            int descY = y + 35;
            String desc = module.getDescription();
            if (desc != null && desc.length() > 25) {
                desc = desc.substring(0, 25) + "...";
            }
            mc.fontRenderer.drawString(desc != null ? desc : "", textX, descY, 0xFF888888);

            int settingsX = x + MODULE_CARD_WIDTH - 10;
            int settingsY = y + MODULE_CARD_HEIGHT - 15;
            mc.fontRenderer.drawString("\u2699", settingsX, settingsY, 0xFF666666);
        }
    }

    private void drawSettingsPanel(int mouseX, int mouseY) {
        if (!showModuleSettings || settingsPanel == null || selectedModule == null) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        settingsPanel.render(mouseX, mouseY, 0);

        int panelX = settingsPanel.getX();
        int panelY = settingsPanel.getY();
        int panelW = settingsPanel.getWidth();

        if (mc.fontRenderer != null) {
            int titleX = panelX + panelW / 2;
            fontRenderer.drawString(selectedModule.getName(), titleX, panelY + 15, 0xFFCCCCCC);

            int closeX = panelX + panelW - 20;
            int closeY = panelY + 5;
            net.moonlit.render.MoonlitRenderer.drawRoundedRect(closeX, closeY, 14, 14, 0xFF333333, 0xFF444444, 1.0f, 3.0f);
            fontRenderer.drawString("\u2715", closeX + 3, closeY + 3, 0xFF888888);

            int descY = panelY + 35;
            fontRenderer.drawString(selectedModule.getDescription(), panelX + 10, descY, 0xFF888888);
            fontRenderer.drawString("Settings", panelX + 10, descY + 15, 0xFF666666);
        }

        GlStateManager.disableBlend();
    }

    private void drawBottomInfo(FontRenderer font, int fps, int ping) {
        int leftX = 10;
        int bottomY = height - 20;

        font.drawString("Moonlit Client", leftX, bottomY, 0xFF888888);
        font.drawString("1.12.2", leftX + font.getStringWidth("Moonlit Client") + 5, bottomY, 0xFF666666);

        int rightX = width - 10 - font.getStringWidth("FPS " + fps + "    Ping " + ping + "ms");
        font.drawString("FPS " + fps + "    Ping " + ping + "ms", rightX, bottomY, 0xFF888888);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            handleModuleClick(mouseX, mouseY);
        } else if (mouseButton == 1) {
            handleModuleRightClick(mouseX, mouseY);
        }

        if (selectedModule != null && showModuleSettings) {
            int closeX = settingsPanel.getX() + settingsPanel.getWidth() - 20;
            int closeY = settingsPanel.getY() + 5;

            if (mouseX >= closeX && mouseX < closeX + 14 && mouseY >= closeY && mouseY < closeY + 14) {
                showModuleSettings = false;
                selectedModule = null;
                settingsPanel.setVisible(false);
                return;
            }
        }

        searchField.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    private void handleModuleClick(int mouseX, int mouseY) {
        int panelX = modulePanel.getX() + 10;
        int panelY = modulePanel.getY() + 10;
        int cardSpacing = 10;

        int i = 0;
        for (MoonlitModule module : modulesInCategory) {
            int cardX = panelX + (i % 2) * (MODULE_CARD_WIDTH + cardSpacing);
            int cardY = panelY + (i / 2) * (MODULE_CARD_HEIGHT + cardSpacing);

            if (cardY + MODULE_CARD_HEIGHT > modulePanel.getY() + modulePanel.getHeight() - 10) break;

            if (mouseX >= cardX && mouseX < cardX + MODULE_CARD_WIDTH &&
                mouseY >= cardY && mouseY < cardY + MODULE_CARD_HEIGHT) {

                if (selectedModule == module && showModuleSettings) {
                    showModuleSettings = false;
                    selectedModule = null;
                    settingsPanel.setVisible(false);
                } else {
                    selectedModule = module;
                    showModuleSettings = true;
                    settingsPanel.setVisible(true);
                }
                return;
            }
            i++;
        }

        if (settingsPanel != null && settingsPanel.contains(mouseX, mouseY)) {
            for (MoonlitButton btn : categoryButtons) {
                if (btn.contains(mouseX, mouseY)) {
                    selectCategory(btn.displayString.replaceAll("^\\s*\\S+\\s+", ""));
                    return;
                }
            }
        }
    }

    private void handleModuleRightClick(int mouseX, int mouseY) {
        int panelX = modulePanel.getX() + 10;
        int panelY = modulePanel.getY() + 10;
        int cardSpacing = 10;

        int i = 0;
        for (MoonlitModule module : modulesInCategory) {
            int cardX = panelX + (i % 2) * (MODULE_CARD_WIDTH + cardSpacing);
            int cardY = panelY + (i / 2) * (MODULE_CARD_HEIGHT + cardSpacing);

            if (cardY + MODULE_CARD_HEIGHT > modulePanel.getY() + modulePanel.getHeight() - 10) break;

            if (mouseX >= cardX && mouseX < cardX + MODULE_CARD_WIDTH &&
                mouseY >= cardY && mouseY < cardY + MODULE_CARD_HEIGHT) {

                selectedModule = module;
                showModuleSettings = true;
                settingsPanel.setVisible(true);
                populateSettingsPanel(module);
                return;
            }
            i++;
        }
    }

    private void populateSettingsPanel(MoonlitModule module) {
        settingsPanel.clearChildren();

        MoonlitTextField rangeField = new MoonlitTextField(mc, mc.fontRenderer, 10, 60, 130, 20, "Range");
        rangeField.setMaxLength(10);
        settingsPanel.addChild(rangeField);

        MoonlitDropdown modeDropdown = new MoonlitDropdown(mc, mc.fontRenderer, 10, 90, 130, 20, "Mode");
        modeDropdown.setOptions("Single", "Multi", "All");
        settingsPanel.addChild(modeDropdown);

        MoonlitToggle autoBlockToggle = new MoonlitToggle(mc, mc.fontRenderer, 10, 120, 130, 20, "AutoBlock");
        settingsPanel.addChild(autoBlockToggle);

        if (mc.fontRenderer != null) {
            int sliderX = 10;
            int sliderY = 150;
            MoonlitSlider rangeSlider = new MoonlitSlider(mc, mc.fontRenderer, sliderX, sliderY, 130, 20, "Range", 0.0f, 10.0f, 4.0f);
            settingsPanel.addChild(rangeSlider);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void onGuiClosed() {
        guiOpen = false;
    }

    @Override
    protected void keyPressed(int keyCode, char typedChar) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        }
    }
}
