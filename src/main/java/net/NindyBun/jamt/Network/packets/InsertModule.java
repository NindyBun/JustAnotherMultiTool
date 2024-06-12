package net.NindyBun.jamt.Network.packets;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.NindyBun.jamt.items.ModuleCard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class InsertModule {
    public static final InsertModule INSTANCE = new InsertModule();

    public static InsertModule get() {
        return INSTANCE;
    }

    public void handle(final InsertModuleData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            BlockPos pos = data.pos();
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (!(blockEntity instanceof ModificationTableEntity)) return;

            ModificationTableContainer container = ((ModificationTableEntity) blockEntity).getContainer(player);
            ItemStack held = player.containerMenu.getCarried();
            if (!ItemStack.matches(held, data.held())) return;

            ItemStack set = ModificationTableContainer.Actions.insert_module(container, data.held(), data.slot());
            container.getTE().setChanged();
            player.containerMenu.setCarried(set);
        });
    }

    public record InsertModuleData(BlockPos pos, ItemStack held, int slot) implements CustomPacketPayload {
        public static final Type<InsertModuleData> TYPE = new Type<>(new ResourceLocation(JustAnotherMultiTool.MODID, "insert_module"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, InsertModuleData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, InsertModuleData::pos,
                ItemStack.STREAM_CODEC, InsertModuleData::held,
                ByteBufCodecs.INT, InsertModuleData::slot,
                InsertModuleData::new
        );
    }
}
