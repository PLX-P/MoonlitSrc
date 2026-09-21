package net.moonlit.hud;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class MoonlitHUDEditor extends GuiScreen {

    private GuiScreen parent;
    private List<MoonlitButton> moduleButtons = new ArrayList<>();
    private HUDModule selectedModule;
    private boolean isDragging = false;
    private boolean isResizing = false;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private int moduleDragStartX = 0;
    private int moduleDragStartY = 0;

    public MoonlitHUDEditor(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int centerX = width / 2;

        moduleButtons.clear();

        MoonlitButton fpsButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 30, 150, 20, "FPS");
        MoonlitButton cpsButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 55, 150, 20, "CPS");
        MoonlitButton coordsButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 80, 150, 20, "Coordinates");
        MoonlitButton pingButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 105, 150, 20, "Ping");
        MoonlitButton keystrokesButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 130, 150, 20, "Keystrokes");
        MoonlitButton armorButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 155, 150, 20, "Armor");
        MoonlitButton potionsButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 180, 150, 20, "Potion Effects");
        MoonlitButton directionButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 205, 150, 20, "Direction");
        MoonlitButton clockButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 230, 150, 20, "Clock");
        MoonlitButton memoryButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 255, 150, 20, "Memory");
        MoonlitButton serverButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 280, 150, 20, "Server Info");

        MoonlitButton resetButton = new MoonlitButton(mc, fontRenderer, centerX - 150, 310, 150, 20, "Reset Layout");
        MoonlitButton closeButton = new MoonlitButton(mc, fontRenderer, centerX + 50, 310, 100, 20, "Close");

        moduleButtons.add(fpsButton);
        moduleButtons.add(cpsButton);
        moduleButtons.add(coordsButton);
        moduleButtons.add(pingButton);
        moduleButtons.add(keystrokesButton);
        moduleButtons.add(armorButton);
        moduleButtons.add(potionsButton);
        moduleButtons.add(directionButton);
        moduleButtons.add(clockButton);
        moduleButtons.add(memoryButton);
        moduleButtons.add(serverButton);
        moduleButtons.add(resetButton);
        moduleButtons.add(closeButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawMoonlitBackground(0xFF1A1A1ACC, 0xFF444444AA);

        drawCenteredString(fontRenderer, "HUD Editor", width / 2, 15, 0xFFCCCCCC);

        drawModuleList(mouseX, mouseY);

        drawModuleInfo(mouseX, mouseY);

        drawSelectedModule(mouseX, mouseY);

        for (MoonlitButton button : moduleButtons) {
            drawButton(button, mouseX, mouseY);
        }
    }

    private void drawMoonlitBackground(int bgColor, int borderColor) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        int x = 0;
        int y = 0;
        int w = width;
        int h = height;

        drawRoundedRect(x, y, w, h, bgColor, borderColor, 1.0f, 8.0f);

        GlStateManager.disableBlend();
    }

    private void drawRoundedRect(int x, int y, int w, int h, int bgColor, int borderColor, float borderWidth, float radius) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        int bgR = (bgColor >> 16 & 0xFF) / 255.0F;
        int bgG = (bgColor >> 8 & 0xFF) / 255.0F;
        int bgB = (bgColor & 0xFF) / 255.0F;
        int bgA = (bgColor >> 24 & 0xFF) / 255.0F;

        worldrenderer.pos(x, y, 0).color(bgR, bgG, bgB, bgA).endVertex();
        worldrenderer.pos(x + w, y, 0).color(bgR, bgG, bgB, bgA).endVertex();
        worldrenderer.pos(x + w, y + h, 0).color(bgR, bgG, bgB, bgA).endVertex();
        worldrenderer.pos(x, y + h, 0).color(bgR, bgG, bgB, bgA).endVertex();

        tessellator.draw();

        if (borderWidth > 0 && borderColor != 0) {
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

            int borderR = (borderColor >> 16 & 0xFF) / 255.0F;
            int borderG = (borderColor >> 8 & 0xFF) / 255.0F;
            int borderB = (borderColor & 0xFF) / 255.0F;
            int borderA = (borderColor >> 24 & 0xFF) / 255.0F;

            worldrenderer.pos(x, y, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x + w, y, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x + w, y + borderWidth, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x, y + borderWidth, 0).color(borderR, borderG, borderB, borderA).endVertex();

            worldrenderer.pos(x + w - borderWidth, y, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x + w, y, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x + w, y + h, 0).color(borderR, borderG, borderB, borderA).endVertex();
            worldrenderer.pos(x + w - borderWidth, y + h, 0).color(borderR, borderG, borderB, borderA).endVertex();

            tessellator.draw();
        }
    }

    private void drawCenteredString(FontRenderer font, String text, int x, int y, int color) {
        font.drawCenteredString(text, x, y, color);
    }

    private void drawModuleList(int mouseX, int mouseY) {
        int panelX = 10;
        int panelY = 35;
        int panelW = width - 20;
        int panelH = 300;

        drawRoundedRect(panelX, panelY, panelW, panelH, 0xFF222222, 0xFF555555, 1.0f, 6.0f);

        drawCenteredString(fontRenderer, "Modules", panelX + panelW / 2, panelY + 20, 0xFF8888FF);

        int listX = panelX + 10;
        int listY = panelY + 40;

        for (int i = 0; i < MoonlitHUD.getHUDModules().size(); i++) {
            HUDModule module = MoonlitHUD.getHUDModules().get(i);

            int moduleX = listX;
            int moduleY = listY + i * 20;

            if (moduleY > panelY + panelH - 20) {
                break;
            }

            drawModuleEntry(moduleX, moduleY, module, i);
        }
    }

    private void drawModuleEntry(int x, int y, HUDModule module, int index) {
        boolean isSelected = selectedModule == module;

        int bgColor = isSelected ? 0xFF333333 : (index % 2 == 0 ? 0xFF2A2A2A : 0xFF222222);
        int borderColor = isSelected ? 0xFF8888FF : 0xFF444444;
        int textColor = isSelected ? 0xFFCCCCFF : 0xFFCCCCCC;

        drawRoundedRect(x, y, 130, 18, bgColor, borderColor, 1.0f, 4.0f);
        drawText(module.getName(), x + 5, y + 5, textColor, fontRenderer);

        if (isSelected) {
            drawBorder(x, y, 130, 18, 0xFF8888FF, fontRenderer);
        }
    }

    private void drawText(String text, int x, int y, int color, FontRenderer font) {
        font.drawString(text, x, y, color);
    }

    private void drawBorder(int x, int y, int w, int h, int color, FontRenderer fontRenderer) {
        int borderWidth = 2;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        int colorR = (color >> 16 & 0xFF) / 255.0F;
        int colorG = (color >> 8 & 0xFF) / 255.0F;
        int colorB = (color & 0xFF) / 255.0F;
        int colorA = (color >> 24 & 0xFF) / 255.0F;

        worldrenderer.pos(x, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + w, y + borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + borderWidth, 0).color(colorR, colorG, colorB, colorA).endVertex();

        tessellator.draw();
    }

    private void drawModuleInfo(int mouseX, int mouseY) {
        if (selectedModule != null) {
            int panelX = width - 200;
            int panelY = 35;
            int panelW = 190;
            int panelH = 150;

            drawRoundedRect(panelX, panelY, panelW, panelH, 0xFF222222, 0xFF555555, 1.0f, 6.0f);

            drawCenteredString(fontRenderer, "Module Info", panelX + panelW / 2, panelY + 20, 0xFF8888FF);

            drawText("Name: " + selectedModule.getName(), panelX + 10, panelY + 45, 0xFFCCCCCC, fontRenderer);
            drawText("X: " + selectedModule.getX() + " Y: " + selectedModule.getY(), panelX + 10, panelY + 65, 0xFFCCCCCC, fontRenderer);
            drawText("W: " + selectedModule.getWidth() + " H: " + selectedModule.getHeight(), panelX + 10, panelY + 85, 0xFFCCCCCC, fontRenderer);
            drawText("Visible: " + selectedModule.isVisible(), panelX + 10, panelY + 105, 0xFFCCCCCC, fontRenderer);
            drawText("Type: " + selectedModule.getType(), panelX + 10, panelY + 125, 0xFFCCCCCC, fontRenderer);
        }
    }

    private void drawText(String text, int x, int y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawString(text, x, y, color);
    }

    private void drawSelectedModule(int mouseX, int mouseY) {
        if (selectedModule != null && selectedModule.isVisible()) {
            int rx = selectedModule.getRenderX();
            int ry = selectedModule.getRenderY();
            int rw = selectedModule.getRenderWidth();
            int rh = selectedModule.getRenderHeight();

            int bgColor = selectedModule.isSelected() ? 0xFF333333 : 0xFF1A1A1ACC;
            int borderColor = selectedModule.isSelected() ? 0xFF8888FF : 0xFF444444;

            drawRoundedRect(rx, ry, rw, rh, bgColor, borderColor, 1.0f, 4.0f);

            if (selectedModule.isSelected()) {
                drawResizeHandles(rx, ry, rw, rh);
            }
        }
    }

    private void drawResizeHandles(int x, int y, int w, int h) {
        int handleSize = 8;
        int handleColor = 0xFF8888FF;

        drawResizeHandle(x, y, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w / 2 - handleSize / 2, y, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w - handleSize, y, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x, y + h - handleSize, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w / 2 - handleSize / 2, y + h - handleSize, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w - handleSize, y + h - handleSize, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w - handleSize, y + h / 2 - handleSize / 2, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x, y + h / 2 - handleSize / 2, handleSize, handleColor, fontRenderer);
        drawResizeHandle(x + w - handleSize, y + h / 2 - handleSize / 2, handleSize, handleColor, fontRenderer);
    }

    private void drawResizeHandle(int x, int y, int size, int color, FontRenderer fontRenderer) {
        int halfSize = size / 2;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        int colorR = (color >> 16 & 0xFF) / 255.0F;
        int colorG = (color >> 8 & 0xFF) / 255.0F;
        int colorB = (color & 0xFF) / 255.0F;
        int colorA = (color >> 24 & 0xFF) / 255.0F;

        worldrenderer.pos(x, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + size, y, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x + size, y + size, 0).color(colorR, colorG, colorB, colorA).endVertex();
        worldrenderer.pos(x, y + size, 0).color(colorR, colorG, colorB, colorA).endVertex();

        tessellator.draw();
    }

    private void drawButton(MoonlitButton button, int mouseX, int mouseY) {
        if (!button.enabled) {
            return;
        }

        int hovered = (button.hovered || button.isMouseOver()) ? 1 : 0;
        int bgColor = (hovered == 1) ? 0xFF333333 : 0xFF222222;
        int borderColor = (hovered == 1) ? 0xFF888888 : 0xFF444444;

        drawRoundedRect(button.x, button.y, button.width, button.height, bgColor, borderColor, 1.0f, 4.0f);

        int textColor = (hovered == 1) ? 0xFFCCCCCC : 0xFFAAAAAA;
        drawCenteredString(fontRenderer, button.displayString, button.x + button.width / 2, button.y + 7, textColor);
    }

    @Override
    protected void actionPerformed(MoonlitButton button) {
        int centerX = width / 2;

        if (button == moduleButtons.get(0)) {
            addModuleToHUD("FPS", MoonlitHUD.fpsCounter);
        } else if (button == moduleButtons.get(1)) {
            addModuleToHUD("CPS", MoonlitHUD.cpsCounter);
        } else if (button == moduleButtons.get(2)) {
            addModuleToHUD("Coordinates", MoonlitHUD.coordinates);
        } else if (button == moduleButtons.get(3)) {
            addModuleToHUD("Ping", MoonlitHUD.ping);
        } else if (button == moduleButtons.get(4)) {
            addModuleToHUD("Keystrokes", MoonlitHUD.keystrokes);
        } else if (button == moduleButtons.get(5)) {
            addModuleToHUD("Armor", MoonlitHUD.armor);
        } else if (button == moduleButtons.get(6)) {
            addModuleToHUD("Potion Effects", MoonlitHUD.potionEffects);
        } else if (button == moduleButtons.get(7)) {
            addModuleToHUD("Direction", MoonlitHUD.direction);
        } else if (button == moduleButtons.get(8)) {
            addModuleToHUD("Clock", MoonlitHUD.clock);
        } else if (button == moduleButtons.get(9)) {
            addModuleToHUD("Memory", MoonlitHUD.memory);
        } else if (button == moduleButtons.get(10)) {
            addModuleToHUD("Server Info", MoonlitHUD.serverInfo);
        } else if (button == moduleButtons.get(11)) {
            MoonlitHUD.resetAllModulePositions();
        } else if (button == moduleButtons.get(12)) {
            MoonlitHUD.setHUDEditorVisible(false);
            mc.displayGuiScreen(parent);
        }
    }

    private void addModuleToHUD(String name, HUDModule module) {
        if (!MoonlitHUD.getHUDModules().contains(module)) {
            MoonlitHUD.addModule(module);
            module.setVisible(true);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            handleLeftClick(mouseX, mouseY);
        } else if (mouseButton == 1) {
            handleRightClick(mouseX, mouseY);
        }
    }

    private void handleLeftClick(int mouseX, int mouseY) {
        for (MoonlitButton button : moduleButtons) {
            if (button.enabled && button.mousePressed(mc, mouseX, mouseY)) {
                actionPerformed(button);
                return;
            }
        }
    }

    private void handleRightClick(int mouseX, int mouseY) {
        if (selectedModule != null && selectedModule.contains(mouseX, mouseY)) {
            selectedModule.setVisible(!selectedModule.isVisible());
            if (!selectedModule.isVisible()) {
                setSelectedModule(null);
            }
        }
    }

    private void setSelectedModule(HUDModule module) {
        if (selectedModule != null) {
            selectedModule.setSelected(false);
        }
        selectedModule = module;
        if (selectedModule != null) {
            selectedModule.setSelected(true);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            MoonlitHUD.setHUDEditorVisible(false);
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            isDragging = false;
        }
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        int mouseButton = Mouse.getButton();

        if (mouseButton == 0) {
            mouseClicked(mouseX, mouseY, 0);
        } else if (mouseButton == 1) {
            mouseClicked(mouseX, mouseY, 1);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        parent = null;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void keyPressed(int keyCode, char typedChar) {
        if (keyCode == 1) {
            MoonlitHUD.setHUDEditorVisible(false);
            mc.displayGuiScreen(parent);
        }
    }
}
