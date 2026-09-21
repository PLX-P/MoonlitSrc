package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.ItemStack;

public class ArmorHUD extends HUDModule {

    private int armorValue = 0;

    public ArmorHUD(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Armor");
    }

    public ArmorHUD() {
        super();
        setName("Armor");
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.inventory != null) {
            armorValue = 0;
            for (int i = 0; i < 4; i++) {
                ItemStack stack = mc.player.inventory.getArmorStack(i);
                if (!stack.isEmpty()) {
                    armorValue += 2 + (stack.getItem().getItemDamage() >> 2);
                }
            }
        }
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        String armorText = "Armor: " + armorValue;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(armorText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Armor";
    }

    @Override
    public int getModuleType() {
        return 5;
    }
}
