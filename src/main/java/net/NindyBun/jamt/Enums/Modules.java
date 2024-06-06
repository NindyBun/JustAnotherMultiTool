package net.NindyBun.jamt.Enums;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public enum Modules {
    EMPTY("empty", ModItems.EMPTY, "empty"),
    MINING_LASER("mining_laser", ModItems.MINING_LASER, "tool"),
    BOLT_CASTER("bolt_caster", ModItems.BOLT_CASTER, "tool"),
    ;

    private final String name;
    private Supplier<Item> item;
    private final String tooltip;
    private final String type;

    Modules(String name, Supplier<Item> item, String type) {
        this.name = name;
        this.item = item;
        this.tooltip = "tooltip."+JustAnotherMultiTool.MODID+"."+name;
        this.type = type;
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

}
