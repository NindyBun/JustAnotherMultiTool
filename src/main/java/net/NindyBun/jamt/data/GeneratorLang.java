package net.NindyBun.jamt.data;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModCreativeTabs;
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
        add(ModItems.C_MULTITOOL.get(), "Class C Multi-Tool");

    }
}
