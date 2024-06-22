package net.NindyBun.jamt.data;

import net.NindyBun.jamt.Registries.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class GeneratorDrops extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(ModBlocks.MODIFICATION_TABLE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = new ArrayList<>();
        knownBlocks.addAll(ModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList());
        return knownBlocks;
    }
}
