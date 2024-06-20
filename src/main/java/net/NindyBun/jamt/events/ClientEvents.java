package net.NindyBun.jamt.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.packets.ServerOpenMultiToolRadialMenu;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModSounds;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.datafix.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.windows.MOUSEINPUT;

import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = JustAnotherMultiTool.MODID)
public class ClientEvents {
    public static KeyMapping multitool_key, itemuse_key;
    private static boolean multitool_keyWasDown = false;

    @SubscribeEvent
    public static void onClientTick(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        Player player = Minecraft.getInstance().player;
        boolean isHoldingMultiTool = false;

        if (Minecraft.getInstance().screen == null) {
            if (ToolMethods.isHoldingTool(player)) {
                isHoldingMultiTool = true;
            }
            boolean multitool_keyIsDown = multitool_key.isDown();
            if (multitool_keyIsDown && !multitool_keyWasDown && isHoldingMultiTool) {
                ItemStack stack = ToolMethods.getTool(player);
                if (stack.getOrDefault(ModDataComponents.OVERLOADED.get(), false) || ToolMethods.get_module_tools(stack).isEmpty()) {
                    if (!multitool_keyWasDown)
                        if (player.level().isClientSide) player.playSound(ModSounds.ERROR.get(), 0.45f, 1f);
                    multitool_keyWasDown = true;
                    return;
                }
            }
            if (multitool_keyIsDown && !multitool_keyWasDown) {
                while (multitool_key.consumeClick() && isHoldingMultiTool) {
                    if (Minecraft.getInstance().screen == null) {
                        PacketDistributor.sendToServer(new ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData(0));
                    }
                }
            }
            multitool_keyWasDown = multitool_keyIsDown;
        } else {
            multitool_keyWasDown = true;
        }
    }

    public static boolean isKeyDown(KeyMapping keyMapping) {
        if (keyMapping.isUnbound())
            return false;

        boolean isDown = switch (keyMapping.getKey().getType()) {
            case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyMapping.getKey().getValue());
            case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keyMapping.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> false;
        };
        return isDown && keyMapping.getKeyConflictContext().isActive() && keyMapping.getKeyModifier().isActive(keyMapping.getKeyConflictContext());
    }

    public static void wipeOpen() {
        while (multitool_key.consumeClick()){}
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = JustAnotherMultiTool.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class KeyBus {
        @SubscribeEvent
        public static void registerKeyBinds(RegisterKeyMappingsEvent event) {
            event.register(multitool_key = new KeyMapping("key."+JustAnotherMultiTool.MODID+".multitool_key", GLFW.GLFW_KEY_V, "key.categories."+JustAnotherMultiTool.MODID));
            event.register(itemuse_key = new KeyMapping("key."+JustAnotherMultiTool.MODID+".itemuse_key", InputConstants.Type.MOUSE, 1, "key.categories."+JustAnotherMultiTool.MODID));
        }
    }

}
