package net.NindyBun.jamt.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput provider = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new GeneratorLang(provider, "en_us"));
        generator.addProvider(event.includeClient(), new GeneratorItemModels(provider, fileHelper));

        generator.addProvider(event.includeServer(), new GeneratorBlockStates(provider, fileHelper));
    }
}
