package net.NindyBun.jamt.Enums;

public enum MultiToolClasses {
    C("C", 2, 1.0F, 3),
    B("B", 3, 2.0F, 5),
    A("A", 4, 3.0F, 8),
    S("S", 4, 4.0F, 12);

    public final String letter;
    public final int max_tools;
    public final float base_dmg;
    public final int max_level;

    MultiToolClasses(String letter, int maxTools, float baseDmg, int maxLevel) {
        this.letter = letter;
        this.max_tools = maxTools;
        this.base_dmg = baseDmg;
        this.max_level = maxLevel;
    }
}
