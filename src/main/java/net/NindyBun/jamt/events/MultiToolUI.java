package net.NindyBun.jamt.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.Helpers;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.DecimalFormat;

public class MultiToolUI {

    public static void render(GuiGraphics guiGraphics, float partialTicks) {
        Player player = Minecraft.getInstance().player;
        if (!ToolMethods.isHoldingTool(player)) {
            return;
        }
        ItemStack tool = ToolMethods.getTool(player);

        Minecraft client = Minecraft.getInstance();

        int winX = client.getWindow().getGuiScaledWidth();
        int winY = client.getWindow().getGuiScaledHeight();
        double scale = client.getWindow().getGuiScale();
        PoseStack poseStack = guiGraphics.pose();

        String selected = tool.get(ModDataComponents.SELECTED_MODULE.get());

        guiGraphics.fill((int) (winX*0.69), (int) (winY*0.015), (int) (winX*0.99), (int) (winY*0.040), Helpers.genColor(Color.DARK_GRAY, 0.5f).hashCode());

        switch(Modules.valueOf(selected.toUpperCase())) {
            case Modules.MINING_LASER:
                double heat = tool.get(ModDataComponents.HEAT.get());
                double heat_max = tool.get(ModDataComponents.HEAT_MAX.get());
                double hD = heat / heat_max;
                guiGraphics.fill((int) (winX*0.99 - winX*0.30*hD), (int) (winY*0.015), (int) (winX*0.99), (int) (winY*0.040), new Color(255, (int) (255*(1-hD)), (int) (255*(1-hD))).hashCode());
                guiGraphics.drawString(client.font, Component.literal(new DecimalFormat("#").format(hD*100) + " %"), (int) (winX*0.88), (int) (winY*0.065), new Color(255, (int) (255*(1-hD)), (int) (255*(1-hD))).hashCode());
                break;
            case Modules.BOLT_CASTER:
                double b_count = tool.get(ModDataComponents.BOLT_CASTER_MAG.get());
                double b_max = tool.get(ModDataComponents.BOLT_CASTER_MAG_MAX.get());
                double bD = b_count / b_max;
                guiGraphics.fill((int) (winX*0.99 - winX*0.30*bD), (int) (winY*0.015), (int) (winX*0.99), (int) (winY*0.040), Color.WHITE.hashCode());
                guiGraphics.drawString(client.font, Component.literal(b_count + " / " + b_max), (int) (winX*0.69), (int) (winY*0.065), Color.WHITE.hashCode());
                break;
            default:
        }

        poseStack.pushPose();
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(poseStack.last().pose());
        matrix4fStack.translate(-8, -8, 0);
        RenderSystem.applyModelViewMatrix();
        poseStack.translate(0, 0, 200);
        guiGraphics.renderItem(Modules.valueOf(selected.toUpperCase()).getItem(), (int) (winX*0.97), (int) (winY*0.080));
        matrix4fStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        poseStack.popPose();


    }

}
