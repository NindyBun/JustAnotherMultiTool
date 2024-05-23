package net.NindyBun.jamt.Enums;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public enum Modules {
    MINING_LASER("mining_laser", ModItems.MINING_LASER);

    private final String name;
    private Supplier<Item> item;
    private final String tooltip;


    Modules(String name, Supplier<Item> item) {
        this.name = name;
        this.item = item;
        this.tooltip = "tooltip."+JustAnotherMultiTool.MODID+"."+name;
    }

    public ItemStack getItem() {
        return new ItemStack(this.item.get());
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getImage() {
        return new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modules/"+this.name+".png");
    }

    public String getToolTip() {
        return this.tooltip;
    }

}
