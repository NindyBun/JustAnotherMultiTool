package net.NindyBun.jamt.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MultiToolInventory {
    private List<MultiToolSlot> inventory_map = new ArrayList<>();

    public MultiToolInventory() {
        init(new int[]{
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        });
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

    public void deserialize_slot_content(MultiToolInventoryCODEC data) {
        MultiToolSlot slot = this.get_inventory_map().get(data.slotPosition());
        slot.set_state(data.slotState());
        slot.set_module(Modules.valueOf(data.moduleName().toUpperCase()));
        slot.set_itemStack(data.itemStack().is(ModItems.EMPTY.get()) ? ItemStack.EMPTY : data.itemStack());
    }

    public MultiToolInventoryCODEC serialize_slot_content(int index) {
        MultiToolSlot slot = this.inventory_map.get(index);
        return new MultiToolInventoryCODEC(slot.get_module().getName(), slot.get_index(), slot.get_state(), slot.get_itemStack().isEmpty() ? Modules.EMPTY.getItem() : slot.get_itemStack());
    }

    public record MultiToolInventoryCODEC(String moduleName, int slotPosition, int slotState, ItemStack itemStack) {
        public static final Codec<MultiToolInventory.MultiToolInventoryCODEC> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("moduleName").forGetter(MultiToolInventory.MultiToolInventoryCODEC::moduleName),
                        Codec.INT.fieldOf("slotPosition").forGetter(MultiToolInventory.MultiToolInventoryCODEC::slotPosition),
                        Codec.INT.fieldOf("slotState").forGetter(MultiToolInventory.MultiToolInventoryCODEC::slotState),
                        ItemStack.CODEC.fieldOf("itemStack").forGetter(MultiToolInventory.MultiToolInventoryCODEC::itemStack)
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
                ItemStack.STREAM_CODEC,
                MultiToolInventoryCODEC::itemStack,
                MultiToolInventory.MultiToolInventoryCODEC::new
        );
    }
}
