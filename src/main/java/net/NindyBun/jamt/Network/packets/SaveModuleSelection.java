package net.NindyBun.jamt.Network.packets;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SaveModuleSelection {
    public static final SaveModuleSelection INSTANCE = new SaveModuleSelection();

    public static SaveModuleSelection get() {
        return INSTANCE;
    }

    public void handle(SaveModuleSelectionData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!ToolMethods.isHoldingTool(player)) return;
            ItemStack stack = ToolMethods.getTool(player);
            stack.set(ModDataComponents.SELECTED_MODULE.get(), data.name());
        });
    }

    public record SaveModuleSelectionData(String name) implements CustomPacketPayload {
        public static final Type<SaveModuleSelection.SaveModuleSelectionData> TYPE = new Type<>(new ResourceLocation(JustAnotherMultiTool.MODID, "save_module_selection"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, SaveModuleSelection.SaveModuleSelectionData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, SaveModuleSelection.SaveModuleSelectionData::name,
                SaveModuleSelection.SaveModuleSelectionData::new
        );
    }

}
