package net.NindyBun.jamt.entities;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModBlocks;
import net.NindyBun.jamt.Registries.ModEntities;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.NindyBun.jamt.containers.ModificationTableHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ModificationTableEntity extends BlockEntity implements MenuProvider {
    public final ModificationTableHandler handler = new ModificationTableHandler(1, this);

    public ModificationTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModEntities.MODIFICATION_TABLE_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return super.getUpdateTag(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (pTag.contains("Inventory"))
            handler.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("Inventory", handler.serializeNBT(pRegistries));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen." + JustAnotherMultiTool.MODID + ".modification_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ModificationTableContainer(pContainerId, level, worldPosition, pPlayerInventory);
    }

    public ModificationTableContainer getContainer(Player player) {
        return new ModificationTableContainer(0, player.level(), this.worldPosition, player.getInventory());
    }



}
