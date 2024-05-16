package net.NindyBun.jamt.containers;

import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ModificationTableHandler extends ItemStackHandler {
    ModificationTableEntity blockEntity;

    public ModificationTableHandler(int size, ModificationTableEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && stack.getItem() instanceof AbstractMultiTool;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (blockEntity != null)
            blockEntity.setChanged();
    }
}
