package com.caleon.client.module;

public abstract class Setting {
    public final String name;
    /** Optional legend colour (RGB) shown as a swatch in the GUI, -1 = none. */
    public int color = -1;
    protected Setting(String name) { this.name = name; }

    public static class Bool extends Setting {
        public boolean value;
        public Bool(String name, boolean value) { super(name); this.value = value; }
        public Bool(String name, boolean value, int rgb) { super(name); this.value = value; this.color = rgb; }
    }

    public static class Num extends Setting {
        public double value, min, max;
        public Num(String name, double value, double min, double max) {
            super(name); this.value = value; this.min = min; this.max = max;
        }
    }

    /** A clickable row that runs code instead of holding a value - used for buttons like "Choose Image". */
    public static class Action extends Setting {
        public final Runnable run;
        public Action(String name, Runnable run) { super(name); this.run = run; }
    }
}
