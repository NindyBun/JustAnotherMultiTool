package net.NindyBun.jamt.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Generator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput provider = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new GeneratorLang(provider, "en_us"));
        generator.addProvider(event.includeClient(), new GeneratorItemModels(provider, fileHelper));

        generator.addProvider(event.includeServer(), new GeneratorSounds(provider, fileHelper));
        generator.addProvider(event.includeServer(), new GeneratorBlockStates(provider, fileHelper));
        generator.addProvider(event.includeServer(), new LootTableProvider(provider, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(GeneratorDrops::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(event.includeServer(), new GeneratorBlockTags(provider, lookupProvider, fileHelper));
    }
}
