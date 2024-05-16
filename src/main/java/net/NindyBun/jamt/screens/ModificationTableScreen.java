package net.NindyBun.jamt.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

import java.awt.*;

public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private ResourceLocation GUI = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modification_table.png");
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

        //this.scrollingModules.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        int relX = this.width/2;
        int rely = this.height/2;

        //pGuiGraphics.drawCenteredString(font, Component.translatable("text."+JustAnotherMultiTool.MODID+".modification_table"), relX, rely - 100, Color.BLACK.hashCode());

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width-this.imageWidth)/2;
        int relY = (this.height-this.imageHeight)/2;
        pGuiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void init() {
        super.init();
        //this.scrollingModules = new ScrollingModules(Minecraft.getInstance(), this.imageWidth-14, 72, topPos+7, leftPos+7, this);
        //this.addRenderableWidget(this.scrollingModules);
    }



    private static class ScrollingModules extends ScrollPanel implements NarratableEntry {
        ModificationTableScreen screen;
        Modules module = null;

        ScrollingModules(Minecraft client, int width, int height, int top, int left, ModificationTableScreen screen) {
            super(client, width, height, top, left);
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
            return (int) Math.ceil(this.screen.container.getModuleCache().size()/7f)*20;
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
            int x = (entryRight-this.width) + 3;
            int y = relativeY;

            int index = 0;
            for (Modules module : this.screen.container.getModuleCache()) {
                guiGraphics.blitSprite(module.getImage(), x, y, 32, 32);
                x += 22;
                index++;
                if (index % 7 == 0) {
                    y += 20;
                    x = (entryRight-this.width) + 3;
                }
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);

        }
    }
}
