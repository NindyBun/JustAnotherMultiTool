package net.NindyBun.jamt.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class MultiToolInventory {
    private List<MultiToolSlot> inventory_map = new ArrayList<>();

    MultiToolInventory() {
        init(new int[]{
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        });
    }

    MultiToolInventory(MultiToolClasses letter) {
        init(letter.default_slots);
    }

    public MultiToolInventory(int[] inventory_map) {
        init(inventory_map);
    }

    public List<MultiToolSlot> get_inventory_map() {
        return this.inventory_map;
    }

    private void init(int[] pos) {
        for (int i = 0; i < 60; i++) {
            this.inventory_map.add(new MultiToolSlot(pos[i], i));
        }
    }

    public void set_module(int slot, Modules module) {
        this.inventory_map.get(slot).set_module(module);
    }

    public Modules get_module(int slot) {
        return this.inventory_map.get(slot).get_module();
    }

    public record MultiToolInventoryCODEC(String moduleName, int slotPosition, int slotState) {
        public static final Codec<MultiToolInventory.MultiToolInventoryCODEC> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("moduleName").forGetter(MultiToolInventory.MultiToolInventoryCODEC::moduleName),
                        Codec.INT.fieldOf("slotPosition").forGetter(MultiToolInventory.MultiToolInventoryCODEC::slotPosition),
                        Codec.INT.fieldOf("slotState").forGetter(MultiToolInventory.MultiToolInventoryCODEC::slotState)
                ).apply(instance, MultiToolInventory.MultiToolInventoryCODEC::new)
        );
        public static final Codec<List<MultiToolInventory.MultiToolInventoryCODEC>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, MultiToolInventory.MultiToolInventoryCODEC> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                MultiToolInventory.MultiToolInventoryCODEC::moduleName,
                ByteBufCodecs.INT,
                MultiToolInventoryCODEC::slotPosition,
                ByteBufCodecs.INT,
                MultiToolInventoryCODEC::slotState,
                MultiToolInventory.MultiToolInventoryCODEC::new
        );
    }
}
