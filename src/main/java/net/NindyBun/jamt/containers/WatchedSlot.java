package net.NindyBun.jamt.containers;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Consumer;

public class WatchedSlot extends SlotItemHandler {
    private Consumer<Integer> onPress;

    public WatchedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Consumer<Integer> onPress) {
        super(itemHandler, index, xPosition, yPosition);
        this.onPress = onPress;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.onPress.accept(this.getSlotIndex());
    }
}
