package net.NindyBun.jamt.containers;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModBlocks;
import net.NindyBun.jamt.Registries.ModContainers;
import net.NindyBun.jamt.Tools.Helpers;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModificationTableContainer extends AbstractContainerMenu {
    private BlockEntity blockEntity;
    private IItemHandler playerInventory;
    private List<Modules> moduleCache = new ArrayList<>();

    public ModificationTableContainer(int pContainerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(ModContainers.MODIFICATION_TABLE_CONTAINER.get(), pContainerId);

        this.blockEntity = Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        setupContainerSlots();
        setupPlayerSlots();
    }

    public ModificationTableContainer(int pContainerId, Level pLevel, BlockPos pPos, Inventory pPlayerInventory) {
        super(ModContainers.MODIFICATION_TABLE_CONTAINER.get(), pContainerId);

        this.blockEntity = pLevel.getBlockEntity(pPos);
        this.playerInventory = new InvWrapper(pPlayerInventory);

        setupContainerSlots();
        setupPlayerSlots();
    }

    private void setupContainerSlots() {
        IItemHandler cap = this.blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        if (cap != null)
            addSlot(new WatchedSlot(cap, 0, 80, 35, this::updateModuleCache));
    }

    public List<Modules> getModuleCache() {
        return moduleCache;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int column, int dx) {
        for (int i = 0; i < column; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int column, int dx, int row, int dy) {
        for (int i = 0; i < row; i++) {
            index = addSlotRange(handler, index, x, y, column, dx);
            y += dy;
        }
        return index;
    }

    private void setupPlayerSlots() {
        addSlotRange(playerInventory, 0, 8, 142, 9, 18);
        addSlotBox(playerInventory, 9, 8, 84, 9, 18, 3, 18);
    }

    private void updateModuleCache(int index) {
        ItemStack stack = this.getSlot(index).getItem();
        if ( (stack.isEmpty() && !moduleCache.isEmpty()) || !(stack.getItem() instanceof AbstractMultiTool) ) {
            moduleCache.clear();
            return;
        }

        moduleCache.clear();
        moduleCache = ToolMethods.getModules(stack);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if (pIndex == 0) {
                if (!this.moveItemStackTo(stack, 1, this.getItems().size(), false))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(stack, itemStack);
            }else{
                if (stack.getItem() instanceof AbstractMultiTool) {
                    if (!this.moveItemStackTo(stack, 0, 1, false))
                        return ItemStack.EMPTY;
                }else if (pIndex < 10) {
                    if (!this.moveItemStackTo(stack, 10, 37, false))
                        return ItemStack.EMPTY;
                }else if (pIndex < 38 && !this.moveItemStackTo(stack, 1, 10, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, stack);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(getTE().getLevel(), blockEntity.getBlockPos()), pPlayer, ModBlocks.MODIFICATION_TABLE.get());
    }

    public BlockEntity getTE() {
        return this.blockEntity;
    }
}
