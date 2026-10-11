package com.caleon.client.gui;

import com.caleon.client.module.VisualModules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

/** Pick an image already dropped into config/caleonclient/skybox/, or open that folder to add more. */
public class SkyboxScreen extends Screen {
    private static final int ACCENT = 0xFFFF2D3D;

    public SkyboxScreen() { super(Text.literal("Custom Sky")); }

    @Override public boolean shouldPause() { return false; }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fillGradient(0, 0, width, height, 0xC0160005, 0xE8040001);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Custom Sky", width / 2, 16, ACCENT);
        ctx.drawCenteredTextWithShadow(textRenderer, "Click an image below, or Open Folder to drop in your own (.png/.jpg)", width / 2, 30, 0xFFBBA0A0);

        List<Path> images = VisualModules.CustomSky.images();
        int y = 50;
        if (images.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, "No images yet - use Open Folder below to add some", width / 2, y, 0xFF806060);
            y += 20;
        }
        for (Path img : images) {
            String name = img.getFileName().toString();
            boolean over = mouseX >= width / 2 - 120 && mouseX <= width / 2 + 120 && mouseY >= y && mouseY < y + 14;
            ctx.fill(width / 2 - 120, y, width / 2 + 120, y + 14, over ? 0xA0FF2D3D : 0x60170B0E);
            ctx.drawTextWithShadow(textRenderer, name, width / 2 - 115, y + 3, 0xFFFFFFFF);
            y += 16;
        }

        int fy = y + 10;
        boolean overFolder = mouseX >= width / 2 - 60 && mouseX <= width / 2 + 60 && mouseY >= fy && mouseY < fy + 16;
        ctx.fill(width / 2 - 60, fy, width / 2 + 60, fy + 16, overFolder ? 0xFFFF2D3D : 0x80170B0E);
        ctx.drawCenteredTextWithShadow(textRenderer, "Open Folder", width / 2, fy + 4, 0xFFFFFFFF);
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        List<Path> images = VisualModules.CustomSky.images();
        int y = 50;
        if (images.isEmpty()) y += 20;
        for (Path img : images) {
            if (mx >= width / 2 - 120 && mx <= width / 2 + 120 && my >= y && my < y + 14) {
                VisualModules.CustomSky.select(img);
                close();
                return true;
            }
            y += 16;
        }
        int fy = y + 10;
        if (mx >= width / 2 - 60 && mx <= width / 2 + 60 && my >= fy && my < fy + 16) {
            VisualModules.CustomSky.openFolder();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }
}
