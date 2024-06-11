package net.NindyBun.jamt.items;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.Constants;
import net.NindyBun.jamt.Tools.Helpers;
import net.NindyBun.jamt.Tools.VectorFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class AbstractMultiTool extends Item {
    private final MultiToolClasses letter;
    public AbstractMultiTool(MultiToolClasses letter) {
        super(new Item.Properties().stacksTo(1).setNoRepair());
        this.letter = letter;
    }

    public MultiToolClasses getLetter() {
        return this.letter;
    }

    public int getEnergyMax() {
        return Constants.MULTITOOL_BASEMAXPOWER;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.getEnergyMax();
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null)
            return false;
        return energy.getEnergyStored() < energy.getMaxEnergyStored();
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null)
            return 13;
        return Math.min(13 * energy.getEnergyStored() / energy.getMaxEnergyStored(), 13);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null)
            super.getBarColor(pStack);
        return Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> tooltip, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltip, pTooltipFlag);

        boolean sneakPressed = Screen.hasShiftDown();
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        MutableComponent energyText = !sneakPressed
                ? Component.translatable("tooltip." + JustAnotherMultiTool.MODID + ".energy", Helpers.fancyNumber(energy.getEnergyStored()), Helpers.fancyNumber(energy.getMaxEnergyStored()))
                : Component.translatable("tooltip." + JustAnotherMultiTool.MODID + ".energy", String.format("%,d", energy.getEnergyStored()), String.format("%,d", energy.getMaxEnergyStored()));
        tooltip.add(energyText.withStyle(ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return InteractionResultHolder.pass(stack);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player)) return;
        Player player = (Player) livingEntity;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        if (!world.isClientSide) {
            //energy.receiveEnergy(-Constants.MULTITOOL_BASEPOWER, false);
            BlockHitResult hit = VectorFunctions.getLookingAt(player, stack, 20);
            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);
        }
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return super.getDestroySpeed(pStack, pState);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE)) {
            return !state.is(Tiers.IRON.getIncorrectBlocksForDrops());
        }
        return false;
    }
}
