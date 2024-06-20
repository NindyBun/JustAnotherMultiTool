package net.NindyBun.jamt.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput provider = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new GeneratorLang(provider, "en_us"));
        generator.addProvider(event.includeClient(), new GeneratorItemModels(provider, fileHelper));
        generator.addProvider(event.includeClient(), new GeneratorSounds(provider, fileHelper));

        generator.addProvider(event.includeServer(), new GeneratorBlockStates(provider, fileHelper));
    }
}
