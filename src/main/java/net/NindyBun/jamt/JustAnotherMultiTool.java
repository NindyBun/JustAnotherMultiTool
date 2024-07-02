package net.NindyBun.jamt;

import net.NindyBun.jamt.Network.PacketHandler;
import net.NindyBun.jamt.Registries.*;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.capabilities.EnergyCapability;
import net.NindyBun.jamt.data.Generator;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.NindyBun.jamt.entities.projectiles.BoltCaster.BoltCasterRenderer;
import net.NindyBun.jamt.entities.projectiles.PlasmaSpitter.PlasmaSpitterRenderer;
import net.NindyBun.jamt.events.MultiToolUI;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.NindyBun.jamt.screens.ModificationTableScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JustAnotherMultiTool.MODID)
public class JustAnotherMultiTool
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "jamt";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public JustAnotherMultiTool(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModContainers.register(modEventBus);
        ModDataComponents.register(modEventBus);
        modEventBus.addListener(Generator::gatherData);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(PacketHandler::registerPackets);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (itemstack, context) -> new EnergyCapability(itemstack, ((AbstractMultiTool)itemstack.getItem()).getEnergyMax()),
                ModItems.C_MULTITOOL.get()
            );
        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> ((ModificationTableEntity) blockEntity).handler,
                ModBlocks.MODIFICATION_TABLE.get()
                );
    }

    @SubscribeEvent
    public void rightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = ToolMethods.getTool(event.getEntity());
        if (stack.getItem() instanceof AbstractMultiTool) {
            if (this.stackIsAnnoying(event.getLevel(), event.getEntity().getMainHandItem())
                    || this.stackIsAnnoying(event.getLevel(), event.getEntity().getOffhandItem())
                    || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof RedStoneOreBlock) {
                event.setCanceled(true);
            }
        }
    }

    private boolean stackIsAnnoying(Level level, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem))
            return false;

        Block block = ((BlockItem) stack.getItem()).getBlock();
        return block.defaultBlockState().getLightEmission(level, BlockPos.ZERO) > 0;
        /*return block instanceof TorchBlock || block instanceof LanternBlock || block.equals(Blocks.GLOWSTONE)
                || block instanceof RedstoneLampBlock || block instanceof EndRodBlock;*/
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void clientStuff(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.BOLT_CASTER_ENTITY.get(), BoltCasterRenderer::new);
            EntityRenderers.register(ModEntities.PLASMA_SPITTER_ENTITY.get(), PlasmaSpitterRenderer::new);
        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiLayersEvent event) {
            event.registerAbove(VanillaGuiLayers.CROSSHAIR, new ResourceLocation(JustAnotherMultiTool.MODID, "multitool_guilayer"), MultiToolUI::render);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModContainers.MODIFICATION_TABLE_CONTAINER.get(), ModificationTableScreen::new);
        }
    }
}
