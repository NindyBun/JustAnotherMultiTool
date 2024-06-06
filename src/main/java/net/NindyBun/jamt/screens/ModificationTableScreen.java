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
import java.util.*;
import java.util.List;

public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private final ResourceLocation GUI = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modification_table_2.png");
    private final ResourceLocation SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot2.png");
    private final ResourceLocation LOCKED_SLOT = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/item/modules/slot.png");
    private BlockPos blockEntityPos;
    private ModificationTableContainer container;
    private Inventory playerInventory;
    private ScrollingModules scrollingModules;
    private int type_count = 0;
    private boolean type_overloaded = false;

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

        pGuiGraphics.drawString(font,
                Component.translatable("screen."+JustAnotherMultiTool.MODID+".tool_amount")
                .append(Component.translatable(""+type_count).withColor((type_count > 4 || type_overloaded) ? Color.red.hashCode() : Color.WHITE.hashCode()))
                .append("/4"), relX+this.imageWidth/2+12, relY-this.imageHeight/2+6, Color.WHITE.hashCode());

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width-this.imageWidth)/2;
        int relY = (this.height-this.imageHeight)/2;
        pGuiGraphics.blit(GUI, relX-23, relY, 0, 0, this.imageWidth+31, this.imageHeight);
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

        if (!tool.isEmpty() && tool.getItem() instanceof AbstractMultiTool && !held.isEmpty()) {
            if (this.scrollingModules.isMouseOver(pMouseX, pMouseY) && ((held.getItem() instanceof ModuleCard && this.scrollingModules.slot.get_state() == 1) || (held.is(ModItems.SLOT_UNLOCKER.get()) && this.scrollingModules.slot.get_state() == 0))) {
                ItemStack old = this.scrollingModules.slot.get_itemStack();
                PacketDistributor.sendToServer(new InsertModule.InsertModuleData(this.blockEntityPos, held, this.scrollingModules.slot.get_index()));
                //this.menu.setCarried(old);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static class ScrollingModules extends ScrollPanel implements NarratableEntry {
        ModificationTableScreen screen;
        MultiToolSlot slot = MultiToolSlot.EMPTY;

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
            MultiToolSlot currentSlot = MultiToolSlot.EMPTY;
            int x = (entryRight-this.width) + 1;
            int y = relativeY - 3;

            int type_count = 0;

            List<MultiToolSlot> modules = this.screen.container.getInventory().get_inventory_map();
            Map<String, SlotDiff> map = new HashMap<>();

            int index = 0;
            for (MultiToolSlot slot : modules) {
                guiGraphics.blit(slot.get_state() == 1 ? this.screen.SLOT : this.screen.LOCKED_SLOT, x-1, y-1, 0, 0, 18, 18, 18, 18);

                if (!slot.get_itemStack().isEmpty()) {
                    guiGraphics.renderItem(slot.get_itemStack(), x, y);
                    type_count += (slot.get_module().getType().equals("tool") ? 1 : 0);
                    map.put(slot.get_module().getName(), map.getOrDefault(slot.get_module().getName(), new SlotDiff()).add_dupe_count().add_pos(x, y));
                }
                this.screen.type_count = type_count;

                if (isMouseOver(mouseX, mouseY) && (mouseX > x-2 && mouseX < x + 17 && mouseY > y-2 && mouseY < y + 17)) {
                    if ((slot.get_state() == 1 && !this.screen.menu.getCarried().is(ModItems.SLOT_UNLOCKER.get())) || (slot.get_state() == 0 && this.screen.menu.getCarried().is(ModItems.SLOT_UNLOCKER.get()))) {
                        guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, new Color(Color.GRAY.getRed() / 255f, Color.GRAY.getGreen() / 255f, Color.GRAY.getBlue() / 255f, 0.3f).hashCode());
                        currentSlot = slot;
                    }
                }

                x += 18;
                index++;
                if (index % 10 == 0) {
                    y += 18;
                    x = (entryRight-this.width) + 1;
                }
            }

            boolean type_overloaded = type_count > 4;
            for (Map.Entry<String, SlotDiff> entry : map.entrySet()) {
                String name = entry.getKey();
                SlotDiff diff = entry.getValue();
                if (diff.get_dupe_count() > 1) {
                    type_overloaded = true;
                    for (int[] pos : diff.get_positions()) {
                        guiGraphics.fill(pos[0] - 1, pos[1] - 1, pos[0] + 17, pos[1] + 17, Color.RED.hashCode());
                    }
                }
            }
            this.screen.type_overloaded = type_overloaded;

            if (currentSlot.get_itemStack().isEmpty() || !currentSlot.get_itemStack().equals(this.slot.get_itemStack()))
                this.slot = currentSlot;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || this.slot.get_itemStack().isEmpty() || !this.screen.menu.getCarried().isEmpty())
                return false;
            ItemStack old = this.screen.scrollingModules.slot.get_itemStack();
            PacketDistributor.sendToServer(new ExtractModule.ExtractModuleData(this.screen.blockEntityPos, this.slot.get_index()));
            //this.screen.menu.setCarried(old);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            if (!this.slot.get_itemStack().isEmpty())
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Lists.transform(this.slot.get_itemStack().getTooltipLines(Item.TooltipContext.EMPTY, this.screen.getMinecraft().player, TooltipFlag.Default.NORMAL), Component::getVisualOrderText), mouseX, mouseY);
        }
    }

    private static class SlotDiff {
        private List<int[]> pos = new ArrayList<>();
        private int dupes = 0;

        private SlotDiff add_dupe_count() {
            this.dupes += 1;
            return this;
        }

        private SlotDiff add_pos(int x, int y) {
            this.pos.add(new int[]{x, y});
            return this;
        }

        private int get_dupe_count() {
            return this.dupes;
        }

        private List<int[]> get_positions() {
            return this.pos;
        }
    }
}
