package net.NindyBun.jamt.capabilities;

import net.NindyBun.jamt.Registries.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public class EnergyCapability extends EnergyStorage {
    private final ItemStack stack;

    public EnergyCapability(ItemStack stack, int capacity) {
        super(getMaxCapacity(stack, capacity), Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.stack = stack;
        this.energy = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    private static int getMaxCapacity(ItemStack stack, int capacity) {
        return stack.getOrDefault(ModDataComponents.ENERGY_MAX, capacity);
    }

    public void updatedMaxEnergy(int max) {
        stack.set(ModDataComponents.ENERGY_MAX.get(), max);
        this.capacity = max;
        this.energy = Math.min(max, this.energy);
        this.receiveEnergy(1, false);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = this.getEnergyStored() + maxReceive;
        if (stored < 0)
            return 0;
        int amount = super.receiveEnergy(maxReceive, simulate);
        if (!simulate)
            stack.set(ModDataComponents.ENERGY.get(), this.energy);
        return amount;
    }
}
