package com.caleon.client.module;

public enum Category {
    COMBAT("Combat"), MISC("Misc"), BASEFINDING("Basefinding"), RENDER("Render"), VISUALS("Visuals"), DONUT("Donut");
    public final String label;
    Category(String label) { this.label = label; }
}
