package net.NindyBun.jamt.items;

import net.minecraft.world.item.Item;

public class AbstractMultiTool extends Item {
    public AbstractMultiTool() {
        super(new Item.Properties().stacksTo(1).setNoRepair());
    }
}
