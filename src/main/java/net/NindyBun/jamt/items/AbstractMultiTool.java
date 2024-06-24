package net.NindyBun.jamt.items;

import com.google.common.collect.ImmutableList;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModSounds;
import net.NindyBun.jamt.Tools.*;
import net.NindyBun.jamt.entities.projectiles.BoltCaster.BoltCasterEntity;
import net.NindyBun.jamt.entities.projectiles.PlasmaSpitter.PlasmaSpitterEntity;
import net.NindyBun.jamt.entities.projectiles.PlasmaSpitter.PlasmaSpitterRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class AbstractMultiTool extends Item {
    private final MultiToolClasses letter;
    private ModSounds.LoopMiningLaserSound loopMiningLaserSound;

    public AbstractMultiTool(MultiToolClasses letter) {
        super(new Item.Properties().stacksTo(1).setNoRepair()
                .component(ModDataComponents.COOLDOWN.get(), 0)
                .component(ModDataComponents.FIRE_RATE.get(), 0)
                .component(ModDataComponents.DESTROY_PROGRESS.get(), 0f)
                .component(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE))
                .component(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName())
                .component(ModDataComponents.OVERLOADED.get(), false)
                .component(ModDataComponents.BURST_AMOUNT.get(), 0)
                .component(ModDataComponents.ACTIVE.get(), false)
        );
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
            loopMiningLaserSound = new ModSounds.LoopMiningLaserSound(player, 0.6f, player.level().random);
            Minecraft.getInstance().getSoundManager().play(loopMiningLaserSound);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.get(ModDataComponents.ACTIVE.get())) return InteractionResultHolder.pass(stack);

        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return InteractionResultHolder.pass(stack);

        String selected = stack.get(ModDataComponents.SELECTED_MODULE.get());
        if (ToolMethods.get_module_tools(stack).size() > 4 || stack.get(ModDataComponents.OVERLOADED.get()) || selected.equals(Modules.EMPTY.getName())) {
            if (world.isClientSide) player.playSound(ModSounds.ERROR.get(), 0.40f, 1f);
            return InteractionResultHolder.fail(stack);
        }

        //energy.receiveEnergy(-Constants.MULTITOOL_BASEPOWER, false);
        if (selected.equals(Modules.MINING_LASER.getName())) {
            if (world.isClientSide) player.playSound(ModSounds.MINING_LASER_START.get(), 0.6f, 1f);
        }
        stack.set(ModDataComponents.ACTIVE.get(), true);

        return InteractionResultHolder.pass(stack);
    }

    /*@Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player)) return;
        Player player = (Player) livingEntity;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        String selected = stack.get(ModDataComponents.SELECTED_MODULE.get());
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
            if (stack.get(ModDataComponents.COOLDOWN.get()) == 0f) {
                if (!world.isClientSide) {
                    stack.set(ModDataComponents.BURST_AMOUNT.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.BURST_AMOUNT));
                    stack.set(ModDataComponents.FIRE_RATE.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE));
                }
            } else {
                return;
            }
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
    }*/

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (!(pEntity instanceof Player)) {
            return;
        }
        Player player = (Player)pEntity;
        String selected = pStack.get(ModDataComponents.SELECTED_MODULE.get());

        boolean isActive = ToolMethods.isUsingTool(player);

        if (pStack.get(ModDataComponents.COOLDOWN.get()) > 0) {
            pStack.set(ModDataComponents.COOLDOWN.get(), Math.max(0, pStack.get(ModDataComponents.COOLDOWN.get())-1));
        }

        if (!ToolMethods.isHoldingTool(player)) {
            if (selected.equals(Modules.BOLT_CASTER.getName())) {
                pStack.set(ModDataComponents.BURST_AMOUNT.get(), 0);
            }
            return;
        }

        switch (Modules.valueOf(selected.toUpperCase())) {
            case Modules.MINING_LASER:
                if (pLevel.isClientSide && isActive) {
                    this.loopSound(player, pStack);
                }
                if (!pLevel.isClientSide && isActive)
                    useMiningLaser(pLevel, player, pStack);

                if (pLevel.isClientSide) {
                    if (this.loopMiningLaserSound != null && !isActive) {
                        if (!this.loopMiningLaserSound.isStopped()) {
                            player.playSound(ModSounds.MINING_LASER_END.get(), 0.6f, 1f);
                        }
                        this.loopMiningLaserSound = null;
                    }
                }
                break;
            case Modules.BOLT_CASTER:
                if (pStack.get(ModDataComponents.COOLDOWN.get()) == 0f && isActive) {
                    if (!pLevel.isClientSide) {
                        pStack.set(ModDataComponents.BURST_AMOUNT.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.BURST_AMOUNT));
                        pStack.set(ModDataComponents.FIRE_RATE.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE));
                    }
                }
                if (pStack.get(ModDataComponents.BURST_AMOUNT.get()) > 0) {
                    useBoltCaster(pLevel, player, pStack);
                }
                break;
            case Modules.PLASMA_SPITTER:
                if (pStack.get(ModDataComponents.COOLDOWN.get()) == 0f && isActive) {
                    if (!pLevel.isClientSide) {
                        pStack.set(ModDataComponents.FIRE_RATE.get(), (int) Modules.PLASMA_SPITTER.getGroup().get(Modules.Group.FIRE_RATE));
                    }
                }
                if (isActive) {
                    usePlasmaSpitter(pLevel, player, pStack);
                }
                break;
            default:

        }

        if (!ToolMethods.isUsingTool(player)) {
            pStack.set(ModDataComponents.ACTIVE.get(), false);
            if (selected.equals(Modules.MINING_LASER.getName())) {
                pStack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
                pStack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
            }
        }
    }

    private void usePlasmaSpitter(Level world, Player player, ItemStack stack) {
        int fireRate = (int) Modules.PLASMA_SPITTER.getGroup().get(Modules.Group.FIRE_RATE);
        int currentTick = Math.min(stack.get(ModDataComponents.FIRE_RATE.get()) + 1, fireRate);

        if (currentTick != fireRate) {
            stack.set(ModDataComponents.FIRE_RATE.get(), currentTick);
            return;
        }

        if (!world.isClientSide) {
            PlasmaSpitterEntity projectile = new PlasmaSpitterEntity(world, player);
            projectile.shootFromRotation(player, player.getRotationVector().x, player.getRotationVector().y, 0, (float) Modules.PLASMA_SPITTER.getGroup().get(Modules.Group.SPEED), (float) Modules.PLASMA_SPITTER.getGroup().get(Modules.Group.INACCURACY));
            world.addFreshEntity(projectile);
        }

        if (world.isClientSide) player.playSound(ModSounds.PLASMA_SPITTER.get(), 0.5f, 1f);
        stack.set(ModDataComponents.FIRE_RATE.get(), 0);
        stack.set(ModDataComponents.COOLDOWN.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.COOLDOWN));
    }

    private void useBoltCaster(Level world, Player player, ItemStack stack) {
        int fireRate = (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE);
        int currentTick = Math.min(stack.get(ModDataComponents.FIRE_RATE.get()) + 1, fireRate);

        if (currentTick != fireRate) {
            stack.set(ModDataComponents.FIRE_RATE.get(), currentTick);
            return;
        }

        if (!world.isClientSide) {
            BoltCasterEntity projectile = new BoltCasterEntity(world, player, null);
            projectile.shootFromRotation(player, player.getRotationVector().x, player.getRotationVector().y, 0, (float) Modules.BOLT_CASTER.getGroup().get(Modules.Group.SPEED), (float) Modules.BOLT_CASTER.getGroup().get(Modules.Group.INACCURACY));
            world.addFreshEntity(projectile);
        }

        if (world.isClientSide) player.playSound(ModSounds.BOLT_CASTER.get(), 0.5f, 1f);
        stack.set(ModDataComponents.FIRE_RATE.get(), 0);
        stack.set(ModDataComponents.BURST_AMOUNT.get(), stack.get(ModDataComponents.BURST_AMOUNT.get()) - 1);
        stack.set(ModDataComponents.COOLDOWN.get(), (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.COOLDOWN));
    }

    private void useMiningLaser(Level world, Player player, ItemStack stack) {
        EntityHitResult entityHitResult = VectorFunctions.getEntityLookingAt(player, player.blockInteractionRange());
        if (entityHitResult != null) {
            Entity entity = entityHitResult.getEntity();
            entity.hurt(new DamageSource(world.damageSources().generic().typeHolder(), player, player), (float) Modules.MINING_LASER.getGroup().get(Modules.Group.BASE_DAMAGE));
            return;
        }

        BlockHitResult hit = VectorFunctions.getLookingAt(player, player.blockInteractionRange());
        BlockPos lastPos = stack.get(ModDataComponents.LAST_BLOCKPOS.get());
        if (hit.getType() == HitResult.Type.MISS || !lastPos.equals(new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)) && !lastPos.equals(hit.getBlockPos()) || !ToolMethods.canToolAffect(stack, world, hit.getBlockPos())) {
            stack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
            stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
            return;
        }
        BlockPos pos = hit.getBlockPos();
        stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(pos.getX(), pos.getY(), pos.getZ()));

        ImmutableList<BlockPos> area = player.isCrouching() ? ImmutableList.of(pos) : VectorFunctions.getBreakableArea(stack, pos, player, (int) Modules.MINING_LASER.getGroup().get(Modules.Group.RADIUS));
        if (area.isEmpty()) {
            stack.set(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
            stack.set(ModDataComponents.LAST_BLOCKPOS.get(), new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
            return;
        }

        float s = 0F;
        for (BlockPos p : area) {
            s += world.getBlockState(p).getDestroySpeed(world, p);
        }

        float blockDestroySpeed = s/area.size();
        float toolDestroySpeed = (float) Modules.MINING_LASER.getGroup().get(Modules.Group.MINING_SPEED);
        float finalDestroySpeed = toolDestroySpeed/(blockDestroySpeed*30);
        float currentDestorySpeed = stack.get(ModDataComponents.DESTROY_PROGRESS.get());

        int destroyStage = Math.min(currentDestorySpeed > 0.0F ? (int)(currentDestorySpeed * 10.0F) : -1, 9);
        if (destroyStage != 9) {
            stack.set(ModDataComponents.DESTROY_PROGRESS.get(), currentDestorySpeed + finalDestroySpeed);
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
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE) || state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return !state.is(Tiers.IRON.getIncorrectBlocksForDrops());
        }
        return false;
    }
}
