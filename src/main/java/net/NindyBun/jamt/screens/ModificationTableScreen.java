package net.NindyBun.jamt.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.packets.ExtractModule;
import net.NindyBun.jamt.Network.packets.InsertModule;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.NindyBun.jamt.containers.MultiToolSlot;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.NindyBun.jamt.items.ModuleCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.awt.*;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private final ResourceLocation GUI = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modification_table_2.png");
    private final ResourceLocation SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot2.png");
    private final ResourceLocation LOCKED_SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot.png");
    private BlockPos blockEntityPos;
    private ModificationTableContainer container;
    private Inventory playerInventory;
    private ScrollingModules scrollingModules;

    public ModificationTableScreen(ModificationTableContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.container = pMenu;
        this.blockEntityPos = container.getTE().getBlockPos();
        this.playerInventory = pPlayerInventory;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.scrollingModules.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        int relX = this.width/2;
        int relY = this.height/2;

        //pGuiGraphics.drawCenteredString(font, Component.translatable("screen."+JustAnotherMultiTool.MODID+".modification_table"), relX+0, relY+0, Color.GRAY.hashCode());

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width-this.imageWidth)/2;
        int relY = (this.height-this.imageHeight)/2;
        pGuiGraphics.blit(GUI, relX-23, relY, 0, 0, this.imageWidth+29, this.imageHeight);
    }

    @Override
    protected void init() {
        super.init();
        this.scrollingModules = new ScrollingModules(Minecraft.getInstance(), 18*10, 18*3, topPos+16, leftPos-2, this);
        this.addRenderableWidget(this.scrollingModules);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        ItemStack held = this.menu.getCarried();
        ItemStack tool = this.container.slots.get(0).getItem();
        if (!tool.isEmpty() && tool.getItem() instanceof AbstractMultiTool && !held.isEmpty() && held.getItem() instanceof ModuleCard) {
            if (this.scrollingModules.isMouseOver(pMouseX, pMouseY)) {
                Modules old = this.scrollingModules.slot == null ? null : this.scrollingModules.slot.get_module();
                PacketDistributor.sendToServer(new InsertModule.InsertModuleData(this.blockEntityPos, held, this.scrollingModules.slot.get_index()));
                this.menu.setCarried(old == null ? ItemStack.EMPTY : old.getItem());
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static class ScrollingModules extends ScrollPanel implements NarratableEntry {
        ModificationTableScreen screen;
        MultiToolSlot slot = null;

        ScrollingModules(Minecraft client, int width, int height, int top, int left, ModificationTableScreen screen) {
            super(client, width, height, top, left, 4, 0);
            this.screen = screen;
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

        }

        @Override
        protected int getContentHeight() {
            return (int) Math.ceil(this.screen.container.getInventory().get_inventory_map().size()/10f)*18-(!this.screen.container.getInventory().get_inventory_map().isEmpty() ? 4 : 0);
        }

        @Override
        public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_294830_) {
            if (this.getContentHeight() < height)
                return false;
            return super.mouseScrolled(p_94686_, p_94687_, p_94688_, p_294830_);
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            MultiToolSlot currentSlot = null;
            int x = (entryRight-this.width) + 1;
            int y = relativeY - 3;

            List<MultiToolSlot> map = this.screen.container.getInventory().get_inventory_map();
            int index = 0;
            for (MultiToolSlot slot : map) {
                guiGraphics.blit(slot.get_state() == 1 ? this.screen.SLOT : this.screen.LOCKED_SLOT, x-1, y-1, 0, 0, 18, 18, 18, 18);

                if (slot.get_module() != null)
                    guiGraphics.renderItem(slot.get_module().getItem(), x, y);

                if (isMouseOver(mouseX, mouseY) && (mouseX > x && mouseX < x + 15 && mouseY > y && mouseY < y + 15) && slot.get_state() == 1) {
                    guiGraphics.fill(x, y, x+16, y+16, new Color(Color.GRAY.getRed()/255f, Color.GRAY.getGreen()/255f, Color.GRAY.getBlue()/255f, 0.3f).hashCode());
                    currentSlot = slot;
                }

                x += 18;
                index++;
                if (index % 10 == 0) {
                    y += 18;
                    x = (entryRight-this.width) + 1;
                }
            }

            if (currentSlot == null || (this.slot != null && !currentSlot.get_module().equals(this.slot.get_module())))
                this.slot = currentSlot;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || this.slot == null)
                return false;
            //PacketDistributor.sendToServer(new ExtractModule(this.screen.blockEntityPos, this.module.getName()));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            if (this.slot != null)
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Lists.transform(this.slot.get_module().getItem().getTooltipLines(Item.TooltipContext.EMPTY, this.screen.getMinecraft().player, TooltipFlag.Default.NORMAL), Component::getVisualOrderText), mouseX, mouseY);
        }
    }
}
