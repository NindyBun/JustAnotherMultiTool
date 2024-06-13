package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, JustAnotherMultiTool.MODID);

    public static final Supplier<SoundEvent> ERROR = SOUNDS.register("tool_error",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(JustAnotherMultiTool.MODID, "tool_error")));

    public static final Supplier<SoundEvent> MINING_LASER_START = SOUNDS.register("mining_laser_start",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(JustAnotherMultiTool.MODID, "mining_laser_start")));

    public static final Supplier<SoundEvent> MINING_LASER_LOOP = SOUNDS.register("mining_laser_loop",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(JustAnotherMultiTool.MODID, "mining_laser_loop")));

    public static final Supplier<SoundEvent> MINING_LASER_END = SOUNDS.register("mining_laser_end",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(JustAnotherMultiTool.MODID, "mining_laser_end")));

    public static void register(IEventBus event) {
        SOUNDS.register(event);
    }

    public static class LoopMiningLaserSound extends AbstractTickableSoundInstance {
        private final Player player;

        public LoopMiningLaserSound(Player player, float volume, RandomSource source) {
            super(ModSounds.MINING_LASER_LOOP.get(), SoundSource.PLAYERS, source);
            this.player = player;
            this.looping = true;
            this.delay = 0;
            this.volume = volume;
            this.x = (float) player.getX();
            this.y = (float) player.getY();
            this.z = (float) player.getZ();
        }

        @Override
        public void tick() {
            if (!ToolMethods.isUsingTool(player)) {
                this.stop();
            } else {
                this.x = (float) player.getX();
                this.y = (float) player.getY();
                this.z = (float) player.getZ();
            }
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }
    }
}
