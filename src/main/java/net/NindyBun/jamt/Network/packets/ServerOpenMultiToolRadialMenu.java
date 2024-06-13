package net.NindyBun.jamt.Network.packets;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.PacketHandler;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerOpenMultiToolRadialMenu {
    public static final ServerOpenMultiToolRadialMenu INSTANCE = new ServerOpenMultiToolRadialMenu();

    public static ServerOpenMultiToolRadialMenu get() {
        return INSTANCE;
    }

    public void handle(ServerOpenMultiToolRadialMenuData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!ToolMethods.isHoldingTool(player)) return;
            ItemStack stack = ToolMethods.getTool(player);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData(stack));
        });
    }

    public record ServerOpenMultiToolRadialMenuData(int dummy) implements CustomPacketPayload {
        public static final Type<ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData> TYPE = new Type<>(new ResourceLocation(JustAnotherMultiTool.MODID, "open_server_multitool_radial_menu"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData::dummy,
                ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData::new
        );
    }

}
