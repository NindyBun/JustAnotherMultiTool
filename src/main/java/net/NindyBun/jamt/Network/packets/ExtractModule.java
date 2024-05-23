package net.NindyBun.jamt.Network.packets;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ExtractModule {
    public static final ExtractModule INSTANCE = new ExtractModule();

    public static ExtractModule get() {
        return INSTANCE;
    }

    public void handle(final ExtractModuleData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
           Player player = context.player();
           Level level = player.level();
           BlockPos pos = data.pos();
           BlockEntity blockEntity = level.getBlockEntity(pos);

           if (!(blockEntity instanceof ModificationTableEntity)) return;

           ModificationTableContainer container = ((ModificationTableEntity) blockEntity).getContainer(player);



        });
    }

    public record ExtractModuleData(BlockPos pos, String name) implements CustomPacketPayload {
        public static final Type<ExtractModuleData> TYPE = new Type<>(new ResourceLocation(JustAnotherMultiTool.MODID, "extract_module"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, ExtractModuleData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, ExtractModuleData::pos,
                ByteBufCodecs.STRING_UTF8, ExtractModuleData::name,
                ExtractModuleData::new
        );
    }
}
