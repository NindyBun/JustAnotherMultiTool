package net.NindyBun.jamt.data;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModBlocks;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class GeneratorLang extends LanguageProvider {
    public GeneratorLang(PackOutput output, String locale) {
        super(output, JustAnotherMultiTool.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("creativetab." + JustAnotherMultiTool.MODID + ".jamt_tab", "Just Another Multi-Tool");
        add("tooltip." + JustAnotherMultiTool.MODID + ".energy", "Energy: %d/%d");
        add("screen." + JustAnotherMultiTool.MODID + ".modification_table", "Modification Table");
        add("screen." + JustAnotherMultiTool.MODID + ".tool_amount", "Tools: ");

        add("subtitles." + JustAnotherMultiTool.MODID + ".tool_error", "Tool Malfunctioned");
        add("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_start", "Mining Laser Started");
        add("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_loop", "Mining Laser Looping");
        add("subtitles." + JustAnotherMultiTool.MODID + ".mining_laser_end", "Mining Laser Ended");

        add(ModItems.EMPTY.get(), "");
        add(ModItems.SLOT_UNLOCKER.get(), "Slot Unlocker");

        add(ModItems.C_MULTITOOL.get(), "Class C Multi-Tool");
        add(ModItems.MINING_LASER.get(), "Tool: Mining Laser");
        add(ModItems.BOLT_CASTER.get(), "Tool: Bolt Caster");


        add(ModBlocks.MODIFICATION_TABLE.get(), "Modification Table");

    }
}
