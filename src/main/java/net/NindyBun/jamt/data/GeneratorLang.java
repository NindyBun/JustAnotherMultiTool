package net.NindyBun.jamt.data;

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

        add(ModItems.C_MULTITOOL.get(), "Class C Multi-Tool");
        add(ModItems.MINING_LASER.get(), "Tool: Mining Laser");

        add(ModBlocks.MODIFICATION_TABLE.get(), "Modification Table");

    }
}
