package net.NindyBun.jamt.items;

import com.google.common.collect.ImmutableList;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.Optional;

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
            BlockHitResult hit = VectorFunctions.getLookingAt(player, player.blockInteractionRange());
            BlockPos lastPos = stack.getOrDefault(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
            if (hit.getType() == HitResult.Type.MISS || !lastPos.equals(new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)) && !lastPos.equals(hit.getBlockPos()) || !ToolMethods.canToolAffect(stack, world, hit.getBlockPos())) {
                stack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
                stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
                return;
            }
            BlockPos pos = hit.getBlockPos();
            stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(pos.getX(), pos.getY(), pos.getZ()));

            ImmutableList<BlockPos> area = player.isCrouching() ? ImmutableList.of(pos) : VectorFunctions.getBreakableArea(stack, pos, player, 1);
            float s = 0F;
            for (BlockPos p : area) {
                s += world.getBlockState(p).getDestroySpeed(world, p);
            }
            float blockDestroySpeed = s/area.size();
            float toolDestroySpeed = Tiers.IRON.getSpeed();
            float finalDestroySpeed = toolDestroySpeed/(blockDestroySpeed*30);
            float currentDestorySpeed = stack.getOrDefault(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F) + finalDestroySpeed;
            stack.set(ModDataComponents.DESTROY_PROGRESS.get(), currentDestorySpeed);

            int destroyStage = Math.min(currentDestorySpeed > 0.0F ? (int)(currentDestorySpeed * 10.0F) : -1, 9);
            if (destroyStage != 9) {
                return;
            }

            for (BlockPos blockPos : area) {
                mineBlock(world, blockPos, stack);
            }

            stack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
            stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));

        }
    }

    public void mineBlock(Level world, BlockPos blockPos, ItemStack stack) {
        BlockState blockState = world.getBlockState(blockPos);
        world.destroyBlock(blockPos, false);
        List<ItemStack> blockDrops = blockState.getDrops(new LootParams.Builder((ServerLevel) world)
                .withParameter(LootContextParams.TOOL, stack)
                .withParameter(LootContextParams.ORIGIN, blockPos.getCenter())
                .withParameter(LootContextParams.BLOCK_STATE, blockState));
        int blockXP = blockState.getExpDrop(world, world.random, blockPos,0, 0);
        if (!blockDrops.isEmpty()){
            blockDrops.forEach(drop -> {
                world.addFreshEntity(new ItemEntity(world, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, drop.copy()));
            });
        }
        blockState.getBlock().popExperience((ServerLevel) world, blockPos, blockXP);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!(pEntity instanceof Player)) {
            return;
        }
        Player player = (Player)pEntity;
        if (!ToolMethods.isHoldingTool(player)) {
            return;
        }
        if (!player.isUsingItem()) {
            pStack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
            pStack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE) || state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return !state.is(Tiers.IRON.getIncorrectBlocksForDrops());
        }
        return false;
    }
}
