package net.NindyBun.jamt.data;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class GeneratorBlockStates extends BlockStateProvider {
    public GeneratorBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, JustAnotherMultiTool.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.MODIFICATION_TABLE.get(), new ModelFile.UncheckedModelFile(modLoc("block/modification_table")));
    }
}
