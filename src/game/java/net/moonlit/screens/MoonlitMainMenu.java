package net.moonlit.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.Minecraft;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class MoonlitMainMenu extends GuiScreen {

    private static final String MOONLIT_NAME = "MOONLIT";
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation("moonlit:textures/gui/Moonlitbg.png");

    private final List<MoonlitButton> menuButtons = new ArrayList<>();
    private final List<GuiButton> buttonList = new ArrayList<>();
    private MoonlitButton singleplayerButton;
    private MoonlitButton multiplayerButton;
    private MoonlitButton settingsButton;
    private MoonlitButton quitButton;
    private MoonlitButton discordButton;
    private MoonlitButton eaglerToneButton;

    private boolean confirmQuit;
    private boolean discordHovered = false;

    public MoonlitMainMenu() {}

    @Override
    public void initGui() {
        int width = this.width;
        int height = this.height;

        int centerX = width / 2;
        int buttonY = height / 2 - 20;
        int buttonSpacing = 24;
        int buttonWidth = 200;
        int leftOffset = centerX - buttonWidth / 2;

        singleplayerButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY, buttonWidth, 20, "Singleplayer");
        singleplayerButton.setOnClick(() -> mc.displayGuiScreen(new GuiSelectWorld(this)));
        menuButtons.add(singleplayerButton);

        multiplayerButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY + buttonSpacing, buttonWidth, 20, "Multiplayer");
        multiplayerButton.setOnClick(() -> mc.displayGuiScreen(new GuiMultiplayer(this)));
        menuButtons.add(multiplayerButton);

        settingsButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY + buttonSpacing * 2, buttonWidth, 20, "Settings");
        settingsButton.setOnClick(() -> mc.displayGuiScreen(new MoonlitSettings(this)));
        menuButtons.add(settingsButton);

        discordButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY + buttonSpacing * 3, buttonWidth, 20, "Join Discord");
        discordButton.setOnClick(() -> openDiscord());
        menuButtons.add(discordButton);

        eaglerToneButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY + buttonSpacing * 4, buttonWidth, 20, "EaglerTone");
        eaglerToneButton.setOnClick(() -> mc.displayGuiScreen(new GuiEaglerTone(this)));
        menuButtons.add(eaglerToneButton);

        quitButton = new MoonlitButton(mc, mc.fontRenderer, leftOffset, buttonY + buttonSpacing * 5, buttonWidth, 20, "Quit");
        quitButton.setOnClick(() -> confirmQuit = !confirmQuit);
        menuButtons.add(quitButton);

        buttonList.clear();
        buttonList.add(singleplayerButton);
        buttonList.add(multiplayerButton);
        buttonList.add(settingsButton);
        buttonList.add(discordButton);
        buttonList.add(eaglerToneButton);
        buttonList.add(quitButton);
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
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button == singleplayerButton) {
                mc.displayGuiScreen(new GuiSelectWorld(this));
            } else if (button == multiplayerButton) {
                mc.displayGuiScreen(new GuiMultiplayer(this));
            } else if (button == settingsButton) {
                mc.displayGuiScreen(new MoonlitSettings(this));
            } else if (button == discordButton) {
                openDiscord();
            } else if (button == eaglerToneButton) {
                mc.displayGuiScreen(new GuiEaglerTone(this));
            } else if (button == quitButton) {
                confirmQuit = !confirmQuit;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackgroundImage();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();

        int centerX = width / 2;

        drawMainMenuBackground();

        if (mc.fontRenderer != null) {
            int moonlitY = height / 2 - 140;
            mc.fontRenderer.drawString(MOONLIT_NAME, centerX - mc.fontRenderer.getStringWidth(MOONLIT_NAME) / 2, moonlitY, 0xFFCCCCCC);

            if (mc.fontRenderer != null) {
                mc.fontRenderer.drawString("Made by Greenshire on Discord", centerX - mc.fontRenderer.getStringWidth("Made by Greenshire on Discord") / 2, moonlitY + 18, 0xFF888888);
            }
        }

        for (MoonlitButton button : menuButtons) {
            button.render(mouseX, mouseY, partialTicks);
        }

        if (discordButton != null && discordHovered) {
            drawDiscordHovered(mouseX, mouseY);
        }

        if (confirmQuit) {
            drawSubMenu(mouseX, mouseY, 0xFF1A1A1A, 0xFF444444, 1.0f, 6.0f);
            if (mc.fontRenderer != null) {
                mc.fontRenderer.drawString("Are you sure?", centerX - 75, height / 2 - 70, 0xFFCCCCCC);
                mc.fontRenderer.drawString("Yes", centerX - 75, height / 2 - 40, 0xFF8888FF);
                mc.fontRenderer.drawString("No", centerX + 35, height / 2 - 40, 0xFF888888);
            }
        }

        drawBottomInfo();
    }

    private void drawBackgroundImage() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        int bgX = 0;
        int bgY = 0;
        int bgWidth = width;
        int bgHeight = height;

        net.lax1dude.eaglercraft.minecraft.GuiButtonWithStupidIcons.drawTexturedModalRect(bgX, bgY, 0, 0, bgWidth, bgHeight);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawMainMenuBackground() {
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

    private void drawDiscordHovered(int mouseX, int mouseY) {
        if (mc.fontRenderer == null) return;

        int tooltipX = mouseX + 10;
        int tooltipY = mouseY - 30;

        net.moonlit.render.MoonlitRenderer.drawRoundedRect(tooltipX, tooltipY, 200, 20, 0xFF1A1A1ACC, 0xFF444444AA, 1.0f, 4.0f);
        mc.fontRenderer.drawString("https://discord.gg/jyaNPBpSzt", tooltipX + 10, tooltipY + 7, 0xFFCCCCCC);
    }

    private void drawBottomInfo() {
        if (mc.fontRenderer == null) return;

        int fps = mc.displayFps;
        int ping = 0;

        drawBottomLeft(mc.fontRenderer, fps, ping);
    }

    private void drawBottomLeft(FontRenderer font, int fps, int ping) {
        int leftX = 10;
        int bottomY = height - 20;

        font.drawString("Moonlit Client", leftX, bottomY, 0xFF888888);
        font.drawString("1.12.2", leftX + font.getStringWidth("Moonlit Client") + 5, bottomY, 0xFF666666);

        int rightX = width - 10 - font.getStringWidth("FPS " + fps + "    Ping " + ping + "ms");
        font.drawString("FPS " + fps + "    Ping " + ping + "ms", rightX, bottomY, 0xFF888888);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (confirmQuit) {
            int centerX = width / 2;
            int subX = centerX - 100;
            int subY = height / 2 - 80;
            int subW = 200;
            int subH = 120;

            if (mouseX >= subX + 25 && mouseX <= subX + 75 && mouseY >= subY + 40 && mouseY <= subY + 60) {
                mc.shutdown();
            } else if (mouseX >= subX + 75 && mouseX <= subX + 125 && mouseY >= subY + 40 && mouseY <= subY + 60) {
                confirmQuit = false;
            }
            return;
        }

        for (MoonlitButton button : menuButtons) {
            if (button.contains(mouseX, mouseY)) {
                actionPerformed(button);
                break;
            }
        }

        if (discordButton != null && discordButton.contains(mouseX, mouseY)) {
            discordHovered = true;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        discordHovered = false;
    }

    @Override
    public void onGuiClosed() {}

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyPressed(int keyCode, char typedChar) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean keyPress(int keyCode, char typedChar) {
        return super.keyPress(keyCode, typedChar);
    }

    @Override
    public boolean keyDown(int keyCode) {
        return false;
    }

    @Override
    protected void mouseMovedOrUp(int i, int j, int k) {}
}
