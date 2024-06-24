package net.NindyBun.jamt.data;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class GeneratorSounds extends SoundDefinitionsProvider {

    protected GeneratorSounds(PackOutput output, ExistingFileHelper helper) {
        super(output, JustAnotherMultiTool.MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(ModSounds.ERROR.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".tool_error")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.ERROR.get().getLocation().getPath()))));

        add(ModSounds.MINING_LASER_START.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_start")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.MINING_LASER_START.get().getLocation().getPath()))));

        add(ModSounds.MINING_LASER_LOOP.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_loop")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.MINING_LASER_LOOP.get().getLocation().getPath()))));

        add(ModSounds.MINING_LASER_END.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_end")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.MINING_LASER_END.get().getLocation().getPath()))));

        add(ModSounds.BOLT_CASTER.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".bolt_caster")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.BOLT_CASTER.get().getLocation().getPath()))));

        add(ModSounds.PLASMA_SPITTER.get(), SoundDefinition.definition()
                .subtitle("subtitles." + JustAnotherMultiTool.MODID + ".plasma_spitter")
                .with(sound(new ResourceLocation(JustAnotherMultiTool.MODID, ModSounds.PLASMA_SPITTER.get().getLocation().getPath()))));
    }
}
