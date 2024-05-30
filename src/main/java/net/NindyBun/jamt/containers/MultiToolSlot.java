package net.NindyBun.jamt.containers;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.items.ModuleCard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MultiToolSlot {
    public static final MultiToolSlot EMPTY = new MultiToolSlot(0, -1);
    private int state = 0;
    private Modules module = Modules.EMPTY;
    private ItemStack card = ItemStack.EMPTY;
    private final int index;

    public MultiToolSlot(int set, int index) {
        this.state = set;
        this.index = index;
    }

    public int get_index() {
        return this.index;
    }

    public void set_state(int state) {
        this.state = state;
    }

    public int get_state() {
        return this.state;
    }

    public Modules get_module() {
        return this.module;
    }

    public Modules set_module(Modules module) {
        this.module = module;
        return this.module;
    }

    public ItemStack get_itemStack() {
        return this.card;
    }

    public ItemStack set_itemStack(ItemStack card) {
        this.card = card;
        return this.card;
    }

    public void clear() {
        this.card = ItemStack.EMPTY;
        this.module = Modules.EMPTY;
    }

    public record MultiToolSlotCODEC(int index, int state, Modules modules) {

    }
}
