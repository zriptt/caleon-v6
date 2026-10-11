package com.caleon.client.module;

import com.caleon.client.render.SkyboxRenderer;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VisualModules {

    /**
     * Loads an image from config/caleonclient/skybox/, ready to use as a sky replacement.
     * NOT WIRED UP YET: Minecraft 1.21.4 renders the sky through a "frame graph" pass system,
     * and safely replacing it needs the real decompiled WorldRenderer source to find the correct
     * injection point - guessing it crashed the game once already, so this module currently loads
     * and remembers your picked image, but does not draw it. See the chat message when you enable it.
     */
    public static class CustomSky extends Module {
        public static boolean active;

        final Setting.Action choose = add(new Setting.Action("Choose Image", CustomSky::openPicker));
        final Setting.Action openFolder = add(new Setting.Action("Open Folder", CustomSky::openFolder));

        public CustomSky() { super("Custom Sky (not wired up)", Category.VISUALS); }

        public static Path folder() {
            Path p = FabricLoader.getInstance().getConfigDir().resolve("caleonclient").resolve("skybox");
            try { Files.createDirectories(p); } catch (IOException ignored) {}
            return p;
        }

        public static List<Path> images() {
            List<Path> out = new ArrayList<>();
            try (var s = Files.list(folder())) {
                s.filter(f -> {
                    String n = f.getFileName().toString().toLowerCase();
                    return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
                }).sorted().forEach(out::add);
            } catch (IOException ignored) {}
            return out;
        }

        public static void select(Path file) {
            if (SkyboxRenderer.load(file)) Config.setString("CustomSky.file", file.getFileName().toString());
        }

        static void openPicker() { mc.setScreen(new com.caleon.client.gui.SkyboxScreen()); }

        public static void openFolder() {
            try { Desktop.getDesktop().open(folder().toFile()); } catch (Exception ignored) {}
        }

        @Override public void onEnable() {
            active = true;
            if (mc.player != null) mc.player.sendMessage(
                net.minecraft.text.Text.literal("[Caleon] Custom Sky: image picker works, but the actual sky-replacement render hook isn't safely wired up yet - nothing will visually change."), false);
            if (!SkyboxRenderer.ready()) {
                String saved = Config.getString("CustomSky.file");
                if (saved != null) {
                    Path f = folder().resolve(saved);
                    if (Files.exists(f)) select(f);
                }
                if (!SkyboxRenderer.ready()) openPicker();
            }
        }

        @Override public void onDisable() { active = false; }
    }

    /** Cycles the GUI/HUD accent color through the rainbow instead of staying fixed red. */
    public static class ChromaTheme extends Module {
        final Setting.Num speed = add(new Setting.Num("Speed", 1.0, 0.1, 5.0));
        private float hue;

        public ChromaTheme() { super("Chroma Theme", Category.VISUALS); }

        @Override public void onTick() {
            hue = (hue + 0.0015f * (float) speed.value) % 1f;
            Theme.setAccent(0xFF000000 | (Color.HSBtoRGB(hue, 0.85f, 1f) & 0xFFFFFF));
        }

        @Override public void onDisable() { Theme.reset(); }
    }

    /** Hides the whole vanilla HUD (hotbar, XP bar, crosshair...) for clean screenshots/clips. */
    public static class CleanScreen extends Module {
        public CleanScreen() { super("Clean Screen", Category.VISUALS); }
        @Override public void onEnable() { mc.options.hudHidden = true; }
        @Override public void onDisable() { mc.options.hudHidden = false; }
    }

    /** Smoothly widens your FOV while sprinting, like a lot of clean/"smooth" client packs do. */
    public static class FovBoost extends Module {
        final Setting.Num amount = add(new Setting.Num("Boost", 15, 0, 40));
        private boolean applied;
        private int base;

        public FovBoost() { super("Fov Boost", Category.VISUALS); }

        @Override public void onTick() {
            if (mc.player == null) return;
            var opt = mc.options.getFov();
            boolean sprint = mc.player.isSprinting();
            if (sprint) {
                if (!applied) { base = opt.getValue(); applied = true; }
                opt.setValue((int) Math.min(110, base + amount.value));
            } else if (applied) {
                opt.setValue(base);
                applied = false;
            }
        }

        @Override public void onDisable() {
            if (applied) { mc.options.getFov().setValue(base); applied = false; }
        }
    }
}
