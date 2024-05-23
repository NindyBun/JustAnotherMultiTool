package net.NindyBun.jamt.Enums;

import java.util.ArrayList;
import java.util.List;

public enum MultiToolClasses {
    C("C", 1.0F, 9, 14, 64*4,
            new int[]{
                    1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
                    0, 1, 1, 1, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            }),
    B("B", 2.0F, 18, 26, 64*8,
            new int[]{
                    1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 0, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            }),
    A("A", 3.0F, 27, 48, 64*16,
            new int[]{
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            }),
    S("S", 4.0F, 36, 60, 0,
            new int[]{
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            });

    public final String letter;
    public final int max_tools = 4;
    public final float base_dmg;
    public int slots;
    public final int max_slots;
    public final int class_upgrade_cost;
    public final int[] default_slots;

    MultiToolClasses(String letter, float baseDmg, int slots, int max_slots, int class_upgrade_cost, int[] default_slots) {
        this.letter = letter;
        this.base_dmg = baseDmg;
        this.slots = slots;
        this.max_slots = max_slots;
        this.class_upgrade_cost = class_upgrade_cost;
        this.default_slots = default_slots;
    }
}
