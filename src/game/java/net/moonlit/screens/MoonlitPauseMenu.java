package net.moonlit.screens;

import java.io.IOException;

import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class MoonlitPauseMenu extends GuiScreen {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation("moonlit:textures/gui/Moonlitbg.png");

    private GuiScreen parent;
    private GuiButton resumeButton;
    private GuiButton settingsButton;
    private GuiButton controlsButton;
    private GuiButton hudButton;
    private GuiButton discordButton;
    private GuiButton disconnectButton;

    private boolean showDiscordTooltip = false;
    private int tooltipX = 0;
    private int tooltipY = 0;

    public MoonlitPauseMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int width = this.width;
        int height = this.height;

        int centerX = width / 2;
        int buttonY = height / 2 - 110;
        int buttonSpacing = 25;
        int buttonWidth = 200;
        int leftOffset = centerX - buttonWidth / 2;

        resumeButton = new GuiButton(0, leftOffset, buttonY, buttonWidth, 20, "Resume");
        settingsButton = new GuiButton(1, leftOffset, buttonY + buttonSpacing, buttonWidth, 20, "Settings");
        controlsButton = new GuiButton(2, leftOffset, buttonY + buttonSpacing * 2, buttonWidth, 20, "Controls");
        hudButton = new GuiButton(3, leftOffset, buttonY + buttonSpacing * 3, buttonWidth, 20, "HUD");
        discordButton = new GuiButton(4, leftOffset, buttonY + buttonSpacing * 4, buttonWidth, 20, "Discord");
        disconnectButton = new GuiButton(5, leftOffset, buttonY + buttonSpacing * 5, buttonWidth, 20, "Disconnect");

        buttonList.clear();
        buttonList.add(resumeButton);
        buttonList.add(settingsButton);
        buttonList.add(controlsButton);
        buttonList.add(hudButton);
        buttonList.add(discordButton);
        buttonList.add(disconnectButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button == resumeButton) {
                mc.displayGuiScreen(parent);
            } else if (button == settingsButton) {
                mc.displayGuiScreen(new MoonlitSettings(this));
            } else if (button == controlsButton) {
                mc.displayGuiScreen(new GuiOptions(this));
            } else if (button == hudButton) {
                mc.displayGuiScreen(new MoonlitHUDEditor(this));
            } else if (button == discordButton) {
                openDiscord();
            } else if (button == disconnectButton) {
                mc.displayGuiScreen(new GuiMainMenu());
            }
        }
    }

    private void openDiscord() {
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                desktop.browse(new java.net.URI("https://discord.gg/jyaNPBpSzt"));
            }
        } catch (Exception e) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("Failed to open Discord: " + e.getMessage()));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackgroundImage();

        int centerX = width / 2;

        drawPauseBackground(centerX);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        drawCenteredString(fontRenderer, "PAUSED", centerX, height / 2 - 120, 0xFF8888FF);

        GlStateManager.disableBlend();

        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
            int buttonX = centerX - 100;
            int buttonY = height / 2 - 80 + i * 25;

            GlStateManager.disableDepth();

            boolean isHovered = (button.hovered || button.isMouseOver());
            int hovered = isHovered ? 1 : 0;
            int bgColor = (hovered == 1) ? 0xFF333333 : 0xFF222222;
            int borderColor = (hovered == 1) ? 0xFF888888 : 0xFF444444;

            drawRoundedRect(buttonX, buttonY, 200, 20, bgColor, borderColor, 1.0f, 4.0f);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            fontRenderer.drawString(button.displayString, buttonX + (200 - fontRenderer.getStringWidth(button.displayString)) / 2, buttonY + 7, 0xFFCCCCCC);

            GlStateManager.enableDepth();

            if (button == discordButton && isHovered) {
                showDiscordTooltip = true;
                tooltipX = mouseX + 10;
                tooltipY = mouseY - 30;
            } else {
                showDiscordTooltip = false;
            }
        }

        if (showDiscordTooltip) {
            drawDiscordTooltip(tooltipX, tooltipY);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawDiscordTooltip(int x, int y) {
        net.moonlit.render.MoonlitRenderer.drawRoundedRect(x, y, 200, 20, 0xFF1A1A1ACC, 0xFF444444AA, 1.0f, 4.0f);
        fontRenderer.drawString("https://discord.gg/jyaNPBpSzt", x + 10, y + 7, 0xFFCCCCCC);
    }

    private void drawPauseBackground(int centerX) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        drawRoundedRect(0, 0, width, height, 0xCC1A1A1A, 0x00000000, 0.0f, 0.0f);

        drawRoundedRect(centerX - 150, height / 2 - 130, 300, 260, 0xFF222222, 0xFF444444, 1.0f, 8.0f);

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }

    private void drawBackgroundImage() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        net.lax1dude.eaglercraft.minecraft.GuiButtonWithStupidIcons.drawTexturedModalRect(0, 0, 0, 0, width, height);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void onGuiClosed() {}

    @Override
    protected void keyPressed(int keyCode, char typedChar) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
            int buttonX = width / 2 - 100;
            int buttonY = height / 2 - 80 + i * 25;

            if (mouseX >= buttonX && mouseX < buttonX + 200 && mouseY >= buttonY && mouseY < buttonY + 20) {
                actionPerformed(button);
                break;
            }
        }
    }

    private void drawRoundedRect(int x, int y, int w, int h, int color, int borderColor, float borderWidth, float radius) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        wr.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = (color >> 24 & 0xFF) / 255.0F;

        float br = (borderColor >> 16 & 0xFF) / 255.0F;
        float bg2 = (borderColor >> 8 & 0xFF) / 255.0F;
        float bb = (borderColor & 0xFF) / 255.0F;
        float ba = (borderColor >> 24 & 0xFF) / 255.0F;

        wr.pos((double) x, (double) y, 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) (x + w), (double) y, 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) (x + w), (double) (y + h), 0.0D).color(r, g, b, a).endVertex();
        wr.pos((double) x, (double) (y + h), 0.0D).color(r, g, b, a).endVertex();

        tess.draw();

        if (borderWidth > 0.0F && borderColor != 0) {
            wr.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

            wr.pos((double) x, (double) y, 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) (x + w), (double) y, 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) (x + w), (double) (y + 2), 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) x, (double) (y + 2), 0.0D).color(br, bg2, bb, ba).endVertex();

            wr.pos((double) (x + w - 2), (double) y, 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) (x + w), (double) y, 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) (x + w), (double) (y + h), 0.0D).color(br, bg2, bb, ba).endVertex();
            wr.pos((double) (x + w - 2), (double) (y + h), 0.0D).color(br, bg2, bb, ba).endVertex();

            tess.draw();
        }
    }
}
