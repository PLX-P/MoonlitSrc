package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.potion.Potion;

public class PotionEffects extends HUDModule {

    private String potionText = "No effects";

    public PotionEffects(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Potion Effects");
    }

    public PotionEffects() {
        super();
        setName("Potion Effects");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.getActivePotionEffects() != null) {
            if (mc.player.getActivePotionEffects().isEmpty()) {
                potionText = "No effects";
                return;
            }

            String effectText = "";
            for (net.minecraft.potion.PotionEffect effect : mc.player.getActivePotionEffects()) {
                Potion potion = Potion.getPotionById(effect.getPotionId());
                if (potion != null) {
                    effectText += potion.getName() + " " + effect.getAmplifier() + " " + effect.getDuration() + "s\n";
                }
            }
            potionText = effectText;
        } else {
            potionText = "No effects";
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String text = "Effects:\n" + potionText;

        int lines = 1;
        for (char c : potionText.toCharArray()) {
            if (c == '\n') lines++;
        }

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight() + (lines - 1) * 12, 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight() + (lines - 1) * 12, 0xFF444444AA, fontRenderer);
        drawText(text, getRenderX() + 5, getRenderY() + 5, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Potion Effects";
    }

    @Override
    public int getModuleType() {
        return 6;
    }
}
