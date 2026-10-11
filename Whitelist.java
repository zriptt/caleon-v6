package com.caleon.client.module;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Player names on this list are skipped by targeting modules (Aim Assist, Trigger Bot), so you
 * never accidentally lock onto or auto-hit someone you've whitelisted - handy for teammates.
 */
public class Whitelist {
    private static final TreeSet<String> NAMES = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("caleonclient_whitelist.txt"); }

    public static List<String> names() { return new ArrayList<>(NAMES); }

    public static boolean isWhitelisted(String name) { return name != null && NAMES.contains(name); }

    public static void add(String name) {
        if (name == null) return;
        name = name.trim();
        if (!name.isEmpty()) { NAMES.add(name); save(); }
    }

    public static void remove(String name) { NAMES.remove(name); save(); }

    public static void load() {
        NAMES.clear();
        try {
            for (String line : Files.readAllLines(path())) {
                String t = line.trim();
                if (!t.isEmpty()) NAMES.add(t);
            }
        } catch (IOException ignored) {}
    }

    public static void save() {
        try { Files.write(path(), NAMES); } catch (IOException ignored) {}
    }
}
