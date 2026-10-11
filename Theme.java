package com.caleon.client.module;

/** Shared GUI/HUD accent color. Defaults to Caleon red; ChromaTheme can animate it. */
public class Theme {
    private static int accent = 0xFFFF2D3D;
    public static int accent() { return accent; }
    public static void setAccent(int argb) { accent = argb; }
    public static void reset() { accent = 0xFFFF2D3D; }
}
