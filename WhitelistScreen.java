package com.caleon.client.gui;

import com.caleon.client.module.Whitelist;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Type a username and press Enter to add it; click an entry in the list to remove it. */
public class WhitelistScreen extends Screen {
    private static final int ACCENT = 0xFFFF2D3D;
    private TextFieldWidget field;

    public WhitelistScreen() { super(Text.literal("Whitelist")); }

    @Override protected void init() {
        field = new TextFieldWidget(textRenderer, width / 2 - 100, 36, 200, 20, Text.literal("Username"));
        field.setMaxLength(32);
        addDrawableChild(field);
        setInitialFocus(field);
    }

    private void addCurrent() {
        String n = field.getText().trim();
        if (!n.isEmpty()) { Whitelist.add(n); field.setText(""); }
    }

    @Override public boolean shouldPause() { return false; }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fillGradient(0, 0, width, height, 0xC0160005, 0xE8040001);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Whitelist - skipped by Aim Assist / Trigger Bot", width / 2, 18, ACCENT);
        ctx.drawCenteredTextWithShadow(textRenderer, "Enter a name, press Enter to add. Click a row to remove.", width / 2, 62, 0xFFBBA0A0);

        List<String> names = Whitelist.names();
        int y = 76;
        if (names.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, "No one whitelisted yet", width / 2, y, 0xFF806060);
        }
        for (String n : names) {
            boolean over = mouseX >= width / 2 - 100 && mouseX <= width / 2 + 100 && mouseY >= y && mouseY < y + 14;
            ctx.fill(width / 2 - 100, y, width / 2 + 100, y + 14, over ? 0xA0FF2D3D : 0x60170B0E);
            ctx.drawTextWithShadow(textRenderer, n, width / 2 - 95, y + 3, 0xFFFFFFFF);
            if (over) ctx.drawTextWithShadow(textRenderer, "remove", width / 2 + 95 - textRenderer.getWidth("remove"), y + 3, 0xFFFFCCCC);
            y += 16;
        }
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        int y = 76;
        for (String n : Whitelist.names()) {
            if (mx >= width / 2 - 100 && mx <= width / 2 + 100 && my >= y && my < y + 14) { Whitelist.remove(n); return true; }
            y += 16;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { addCurrent(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
