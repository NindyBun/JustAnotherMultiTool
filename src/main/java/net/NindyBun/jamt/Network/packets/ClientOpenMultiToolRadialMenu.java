package net.NindyBun.jamt.Network.packets;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.screens.MultiToolRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientOpenMultiToolRadialMenu {
    public static final ClientOpenMultiToolRadialMenu INSTANCE = new ClientOpenMultiToolRadialMenu();

    public static ClientOpenMultiToolRadialMenu get() {
        return INSTANCE;
    }

    public void handle(ClientOpenMultiToolRadialMenuData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new MultiToolRadialMenu(data.stack()));
        });
    }

    public record ClientOpenMultiToolRadialMenuData(ItemStack stack) implements CustomPacketPayload {
        public static final Type<ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData> TYPE = new Type<>(new ResourceLocation(JustAnotherMultiTool.MODID, "open_client_multitool_radial_menu"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData::stack,
                ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData::new
        );
    }

}
