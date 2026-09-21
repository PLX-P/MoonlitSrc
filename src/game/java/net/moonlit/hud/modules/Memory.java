package net.moonlit.hud.modules;

import net.moonlit.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Memory extends HUDModule {

    private String memoryText = "Mem: 0/0 MB";

    public Memory(int x, int y, int width, int height) {
        super(x, y, width, height);
        setName("Memory");
    }

    public Memory() {
        super();
        setName("Memory");
    }

    @Override
    public void update() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();
        long used = memoryUsage.getUsed() / (1024 * 1024);
        long max = memoryUsage.getMax() / (1024 * 1024);
        if (max <= 0) max = 1;
        memoryText = "Mem: " + used + "/" + max + " MB";
    }

    @Override
    public void render(Minecraft mc, FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!visible) return;

        drawBackground(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF1A1A1ACC, fontRenderer);
        drawBorder(getRenderX(), getRenderY(), getRenderWidth(), getRenderHeight(), 0xFF444444AA, fontRenderer);
        drawText(memoryText, getRenderX() + 5, getRenderY() + getRenderHeight() / 2 - 4, color, fontRenderer);
    }

    @Override
    public String getType() {
        return "Memory";
    }

    @Override
    public int getModuleType() {
        return 9;
    }
}
