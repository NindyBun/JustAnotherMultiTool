package net.NindyBun.jamt.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.containers.ModificationTableContainer;
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
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.awt.*;
import java.rmi.registry.Registry;

public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private ResourceLocation GUI = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modification_table_2.png");
    private ResourceLocation SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot.png");
    private ResourceLocation LOCKED_SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/locked_slot.png");
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

    private static class ScrollingModules extends ScrollPanel implements NarratableEntry {
        ModificationTableScreen screen;
        Modules module = null;

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
            return (int) Math.ceil(this.screen.container.getModuleCache().size()/10f)*18-(!this.screen.container.getModuleCache().isEmpty() ? 4 : 0);
        }

        @Override
        public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_294830_) {
            if (this.getContentHeight() < height)
                return false;
            return super.mouseScrolled(p_94686_, p_94687_, p_94688_, p_294830_);
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            Modules currentModule = null;
            int x = (entryRight-this.width) + 1;
            int y = relativeY - 3;

            int index = 0;
            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {

                guiGraphics.blit(new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot.png"), x-1, y-1, 0, 0, 32, 32, 18, 18);
                guiGraphics.renderItem(item.getDefaultInstance(), x, y);

                if (isMouseOver(mouseX, mouseY) && (mouseX > x && mouseX < x + 15 && mouseY > y && mouseY < y + 15)) {
                    guiGraphics.fill(x, y, x+16, y+16, new Color(Color.GRAY.getRed()/255f, Color.GRAY.getGreen()/255f, Color.GRAY.getBlue()/255f, 0.3f).hashCode());
                    currentModule = module;
                }
                if (index == 60) break;
                x += 18;
                index++;
                if (index % 10 == 0) {
                    y += 18;
                    x = (entryRight-this.width) + 1;
                }
            }

            if (currentModule == null || !currentModule.equals(this.module))
                this.module = currentModule;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
