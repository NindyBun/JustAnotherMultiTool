package net.NindyBun.jamt.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.kinds.Kind1;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.packets.SaveModuleSelection;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = JustAnotherMultiTool.MODID)
public class MultiToolRadialMenu extends Screen {
    private int selected;
    private String savedName;
    private List<Modules> modules;

    public MultiToolRadialMenu(ItemStack stack) {
        super(Component.literal("Title"));
        this.modules = ToolMethods.get_module_tools(stack);
        this.savedName = stack.getOrDefault(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName());
        this.selected = ToolMethods.getModuleIndex(this.modules, this.savedName);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int numberOfSlices = this.modules.size();
        if (numberOfSlices == 0)
            return;

        float radiusIn = 20;
        float radiusOut = radiusIn * 2;
        int x = width / 2;
        int y = height / 2;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 0);
        drawBackground(numberOfSlices, pMouseX, pMouseY, x, y, radiusIn, radiusOut);
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        drawItem(pGuiGraphics, numberOfSlices, x, y, radiusIn, radiusOut);
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        drawToolTip(pGuiGraphics, numberOfSlices, x, y, radiusIn, radiusOut);
        pGuiGraphics.pose().popPose();
    }

    public void drawToolTip(GuiGraphics guiGraphics, int sections, int x, int y, float radiusIn, float radiusOut){
        for (int j = 0; j < sections; j++) {
            float start = (((j - 0.5f) / (float) sections) + 0.25f) * 360;
            float end = (((j + 0.5f) / (float) sections) + 0.25f) * 360;
            float itemRadius = (radiusIn+radiusOut)/2;
            float middle = (float) Math.toRadians(start+end)/2;
            float midX = x - itemRadius * (float) Math.cos(middle);
            float midY = y - itemRadius * (float) Math.sin(middle);
            if (selected == j)
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Lists.transform(this.modules.get(j).getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.Default.NORMAL), Component::getVisualOrderText), (int)midX, (int)midY);
        }
    }

    public void drawItem(GuiGraphics graphics, int sections, int x, int y, float radiusIn, float radiusOut){
        PoseStack poseStack = graphics.pose();
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(poseStack.last().pose());
        matrix4fStack.translate(-8, -8, 0);
        RenderSystem.applyModelViewMatrix();

        poseStack.translate(0, 0, 200);
        for (int j = 0; j < sections; j++) {
            float start = (((j - 0.5f) / (float) sections) + 0.25f) * 360;
            float end = (((j + 0.5f) / (float) sections) + 0.25f) * 360;
            float itemRadius = (radiusIn+radiusOut)/2;
            float middle = (float) Math.toRadians(start+end)/2;
            float midX = x - itemRadius * (float) Math.cos(middle);
            float midY = y - itemRadius * (float) Math.sin(middle);
            ItemStack stack = this.modules.get(j).getItem();
            graphics.renderItem(stack, (int)midX, (int)midY);
            //this.itemRenderer.renderGuiItemDecorations(this.font, stack, (int)midX, (int)midY, "");
        }
        matrix4fStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    public void drawBackground(int sections, int mouseX, int mouseY, int x, int y, float radiusIn, float radiusOut){
        RenderSystem.enableBlend();
        //RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int j = 0; j < sections; j++){
            float s0 = (((0 - 0.5f) / (float) sections) + 0.25f) * 360;
            double angle = Math.toDegrees(Math.atan2(y-mouseY, x-mouseX)); //Angle the mouse makes with the screen's equator
            double distance = Math.sqrt(Math.pow(x-mouseX, 2) + Math.pow(y-mouseY, 2)); //Distance of the mouse from the center of the screen
            if (angle < s0) {
                angle += 360;
            }
            float start = (((j - 0.5f) / (float) sections) + 0.25f) * 360;
            float end = (((j + 0.5f) / (float) sections) + 0.25f) * 360;
            if ((distance >= radiusOut && distance < radiusIn) || distance < radiusIn || distance >= radiusOut)
                selected = -1;
            if (angle >= start && angle < end && distance >= radiusIn && distance < radiusOut) {
                selected = j;
                break;
            }
        }

        for (int j = 0; j < sections; j++){
            float start = (((j - 0.5f) / (float) sections) + 0.25f) * 360;
            float end = (((j + 0.5f) / (float) sections) + 0.25f) * 360;

            if (this.selected == j)
                drawPieArc(buffer, x, y, 0, radiusIn, radiusOut, start, end, 255, 255, 255, 64);
            else
                drawPieArc(buffer, x, y, 0, radiusIn, radiusOut, start, end, 0, 0, 0, 64);

            if (this.savedName.equals(this.modules.get(j).getName()))
                drawPieArc(buffer, x, y, 0, radiusIn, radiusOut, start, end, 0, 255, 0, 64);

        }

        tesselator.end();
        RenderSystem.disableBlend();
        //RenderSystem.enableTexture();
    }

    public void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a){
        float angle = endAngle - startAngle;
        int sections = (int)Math.max(1, Math.nextUp(angle / 5.0F));

        startAngle = (float) Math.toRadians(startAngle);
        endAngle = (float) Math.toRadians(endAngle);
        angle = endAngle - startAngle;

        for (int i = 0; i < sections; i++)
        {
            float angle1 = startAngle + (i / (float) sections) * angle;
            float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

            //subtracting goes top clockwise
            //addition goes bottom clockwise
            float pos1InX = x - radiusIn * (float) Math.cos(angle1);
            float pos1InY = y - radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x - radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y - radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x - radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y - radiusOut * (float) Math.sin(angle2);
            float pos2InX = x - radiusIn * (float) Math.cos(angle2);
            float pos2InY = y - radiusIn * (float) Math.sin(angle2);

            buffer.vertex(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.processClick();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void processClick() {
        if (selected != -1) PacketDistributor.sendToServer(new SaveModuleSelection.SaveModuleSelectionData(this.modules.get(this.selected).getName()));
        onClose();
    }

    @SubscribeEvent
    public static  void overlayEvent(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof MultiToolRadialMenu) {
            if (event.getLayer() == VanillaGuiLayers.CROSSHAIR) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public void removed() {
        super.removed();
        ClientEvents.wipeOpen();
    }

    @Override
    public void tick() {
        super.tick();
        if (!ClientEvents.isKeyDown(ClientEvents.multitool_key)){
            Minecraft.getInstance().setScreen(null);
            ClientEvents.wipeOpen();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
