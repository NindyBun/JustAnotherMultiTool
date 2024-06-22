package net.NindyBun.jamt.Enums;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum Modules {
    EMPTY("empty", ModItems.EMPTY, "empty", null),
    MINING_LASER("mining_laser", ModItems.MINING_LASER, "tool", new Group()
            .put(Group.GROUP_NAME, "mining_laser")
            .put(Group.GROUP_COLOR, Color.GREEN)
            .put(Group.BASE_DAMAGE, 2.0f)
            .put(Group.MINING_SPEED, 6.0f)
            .put(Group.RADIUS, 1)),
    BOLT_CASTER("bolt_caster", ModItems.BOLT_CASTER, "tool", new Group()
            .put(Group.GROUP_NAME, "bolt_caster")
            .put(Group.GROUP_COLOR, Color.ORANGE)
            .put(Group.BASE_DAMAGE, 2.0f)
            .put(Group.FIRE_RATE, 1)
            .put(Group.BURST_AMOUNT, 3)
            .put(Group.COOLDOWN, 8)
            .put(Group.SPEED, 3.14f)),
    ;

    private final String name;
    private Supplier<Item> item;
    private final String tooltip;
    private final String type;
    private final Group group;

    Modules(String name, Supplier<Item> item, String type, Group group) {
        this.name = name;
        this.item = item;
        this.tooltip = "tooltip."+JustAnotherMultiTool.MODID+"."+name;
        this.type = type;
        this.group = group;
    }

    public ItemStack getItem() {
        return new ItemStack(this.item.get());
    }

    public String getName() {
        return this.name;
    }

    public String getToolTip() {
        return this.tooltip;
    }

    public String getType() {
        return this.type;
    }

    public Group getGroup() {
        return this.group;
    }

    public static class Group {
        private Map<String, Object> contents = new HashMap<>();

        public static final String GROUP_NAME = "groupName";
        public static final String GROUP_COLOR = "groupColor";
        public static final String BASE_DAMAGE = "baseDamage";
        public static final String FIRE_RATE = "fireRate";
        public static final String BURST_AMOUNT = "burstAmount";
        public static final String COOLDOWN = "cooldown";
        public static final String SPEED = "speed";
        public static final String MINING_SPEED = "mining_speed";
        public static final String RADIUS = "radius";

        public Group() {}

        public Group put(String key, Object value) {
            this.contents.put(key, value);
            return this;
        }

        public Object get(String key) {
            return this.contents.get(key);
        }
    }
}
