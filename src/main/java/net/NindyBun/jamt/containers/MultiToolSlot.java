package net.NindyBun.jamt.containers;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.items.ModuleCard;

public class MultiToolSlot {
    private int state = 0;
    private Modules module = null;
    private final int index;

    public MultiToolSlot(int set, int index) {
        this.state = set;
        this.index = index;
    }

    public int get_index() {
        return this.index;
    }

    public void unlock_state() {
        this.state = 1;
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

    public void clear_module() {
        this.module = null;
    }

    public record MultiToolSlotCODEC(int index, int state, Modules modules) {

    }
}
