package net.NindyBun.jamt.data;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModBlocks;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GeneratorItemModels extends ItemModelProvider {
    public GeneratorItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JustAnotherMultiTool.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBlockModel(ModBlocks.MODIFICATION_TABLE);

        ModItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof AbstractMultiTool) return;
            if (item.get().getDefaultInstance().is(ModItems.MODIFICATION_TABLE_ITEM.get())) return;
            String path = item.getId().getPath();
            singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/modules/"+path));
        });

        ModItems.MODULES.getEntries().forEach(module -> {
            String path = module.getId().getPath();
            singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/modules/"+path));
        });
    }

    private void registerBlockModel(DeferredHolder<Block, ?> block) {
        String path = block.getId().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }
}
