package net.NindyBun.jamt.items;

import com.google.common.collect.ImmutableList;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModSounds;
import net.NindyBun.jamt.Tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AbstractMultiTool extends Item {
    private final MultiToolClasses letter;
    private ModSounds.LoopMiningLaserSound loopMiningLaserSound;

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

    @OnlyIn(Dist.CLIENT)
    public void loopSound(Player player, ItemStack stack) {
        if (loopMiningLaserSound == null) {
            loopMiningLaserSound = new ModSounds.LoopMiningLaserSound(player, 1f, player.level().random);
            Minecraft.getInstance().getSoundManager().play(loopMiningLaserSound);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return InteractionResultHolder.pass(stack);

        String selected = stack.getOrDefault(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName());
        if (ToolMethods.get_module_tools(stack).size() > 4 || stack.getOrDefault(ModDataComponents.OVERLOADED.get(), false) || selected.equals(Modules.EMPTY.getName())) {
            if (world.isClientSide) player.playSound(ModSounds.ERROR.get(), 0.45f, 1f);
            return InteractionResultHolder.fail(stack);
        }

        //energy.receiveEnergy(-Constants.MULTITOOL_BASEPOWER, false);
        if (selected.equals(Modules.MINING_LASER.getName())) {
            if (world.isClientSide) player.playSound(ModSounds.MINING_LASER_START.get(), 1f, 1f);
            player.startUsingItem(usedHand);
            return InteractionResultHolder.pass(stack);
        }
        if (selected.equals(Modules.BOLT_CASTER.getName())) {
            if (stack.getOrDefault(ModDataComponents.COOLDOWN.get(), 0) == 0) {
                if (!world.isClientSide) {
                    stack.set(ModDataComponents.FIRE_RATE.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE));
                    useBoltCaster(world, player, stack);
                    player.startUsingItem(usedHand);
                }
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player)) return;
        Player player = (Player) livingEntity;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        String selected = stack.getOrDefault(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName());
        if (selected.equals(Modules.EMPTY.getName())) return;

        //energy.receiveEnergy(-Constants.MULTITOOL_BASEPOWER, false);
        if (selected.equals(Modules.MINING_LASER.getName())) {
            if (world.isClientSide) {
                this.loopSound(player, stack);
            }
            if (!world.isClientSide)
                useMiningLaser(world, player, stack);
            return;
        }
        if (selected.equals(Modules.BOLT_CASTER.getName())) {
            if (!world.isClientSide) {
                useBoltCaster(world, player, stack);
            }
            return;
        }

    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLevel.isClientSide) {
            if (this.loopMiningLaserSound != null) {
                if (!this.loopMiningLaserSound.isStopped()) {
                    pLivingEntity.playSound(ModSounds.MINING_LASER_END.get(), 1f, 1f);
                }
                this.loopMiningLaserSound = null;
            }
        }

        if (pLivingEntity instanceof Player)
            pLivingEntity.stopUsingItem();
    }

    private Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pAmmo) {
            ArrowItem arrowitem = (ArrowItem) (pAmmo.getItem() instanceof ArrowItem ? pAmmo.getItem() : Items.ARROW);
            return arrowitem.createArrow(pLevel, pAmmo, pShooter);
    }

    private void useBoltCaster(Level world, Player player, ItemStack stack) {
        int fireRate = (int) Modules.valueOf(stack.getOrDefault(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName()).toUpperCase()).getGroup().get(Modules.Group.FIRE_RATE);
        int currentTick = Math.min(stack.getOrDefault(ModDataComponents.FIRE_RATE.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE)) + 1, fireRate);
        stack.set(ModDataComponents.FIRE_RATE.get(), currentTick);
        if (currentTick != fireRate) return;

        //Projectile projectile = this.createProjectile(world, player, Items.ARROW.getDefaultInstance());
        //EntityHitResult hitResult = VectorFunctions.getEntityLookingAt(player, 16);
        //this.shoot(world, player, 10f, 0, hitResult == null ? null : hitResult.getEntity());

        Projectile projectile = this.createProjectile(world, player, Items.ARROW.getDefaultInstance());
        projectile.shootFromRotation(player, player.getRotationVector().x, player.getRotationVector().y, 0, 3.15f, 0);
        world.addFreshEntity(projectile);

        stack.set(ModDataComponents.FIRE_RATE.get(), 0);
        stack.set(ModDataComponents.COOLDOWN.get(), 20);
    }

    private void useMiningLaser(Level world, Player player, ItemStack stack) {
        EntityHitResult entityHitResult = VectorFunctions.getEntityLookingAt(player, player.blockInteractionRange());
        if (entityHitResult != null) {
            Entity entity = entityHitResult.getEntity();
            entity.hurt(new DamageSource(world.damageSources().generic().typeHolder(), player, player), 2);
            return;
        }

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

    private void mineBlock(Level world, BlockPos blockPos, ItemStack stack) {
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
        if (pStack.getOrDefault(ModDataComponents.COOLDOWN.get(), 0) > 0) {
            pStack.set(ModDataComponents.COOLDOWN.get(), Math.max(0, pStack.getOrDefault(ModDataComponents.COOLDOWN.get(), 0)-1));
        }

        if (!ToolMethods.isHoldingTool(player)) {
            return;
        }
        if (!ToolMethods.isUsingTool(player)) {
            pStack.set(ModDataComponents.FIRE_RATE.get(), 0);
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
