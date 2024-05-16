package net.NindyBun.jamt.Enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.NindyBun.jamt.BiggerStreamCodec;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Supplier;

public enum Modules {
    MINING_LASER("mining_laser", 1, 1, List.of(new String[]{
            Tags.Items.INGOTS_IRON.toString()
    }), true, ModItems.MINING_LASER);

    public final String name;
    public int currentLvl;
    public final int maxLvl;
    public final List<String> upgradeMaterials;
    public final double upgradeMaterialMultiplier = 1.5;
    public boolean equipped = false;
    public boolean equippable;
    public Supplier<Item> item;


    Modules(String name, int currentLvl, int maxLvl, List<String> upgradeMaterials, boolean equippable, Supplier<Item> item) {
        this.name = name;
        this.currentLvl = currentLvl;
        this.maxLvl = maxLvl;
        this.upgradeMaterials = upgradeMaterials;
        this.equippable = equippable;
        this.item = item;
    }

    public ItemStack getItem() {
        return new ItemStack(this.item.get());
    }

    public void setCurrentLvl(int level) {
        this.currentLvl = level;
    }

    public ResourceLocation getImage() {
        return new ResourceLocation(JustAnotherMultiTool.MODID, "textures/gui/modules/"+this.name+".png");
    }

    public void sync(ModuleData data) {
        this.currentLvl = data.currentLvl();
        this.equipped = data.equipped();
        this.equippable = data.equippable();
    }

    public record ModuleData(String moduleName, int currentLvl, int maxLvl, List<String> upgradeMaterials, double upgradeMaterialMultiplier, boolean equipped, boolean equippable) {
        public static final Codec<ModuleData> CODEC = RecordCodecBuilder.create(
          instance -> instance.group(
                  Codec.STRING.fieldOf("moduleName").forGetter(ModuleData::moduleName),
                  Codec.INT.fieldOf("currentLvl").forGetter(ModuleData::currentLvl),
                  Codec.INT.fieldOf("maxLvl").forGetter(ModuleData::maxLvl),
                  Codec.STRING.listOf().fieldOf("materials").forGetter(ModuleData::upgradeMaterials),
                  Codec.DOUBLE.fieldOf("multiplier").forGetter(ModuleData::upgradeMaterialMultiplier),
                  Codec.BOOL.fieldOf("equipped").forGetter(ModuleData::equipped),
                  Codec.BOOL.fieldOf("equippable").forGetter(ModuleData::equippable)
          ).apply(instance, ModuleData::new)
        );
        public static final Codec<List<ModuleData>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, ModuleData> STREAM_CODEC = BiggerStreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ModuleData::moduleName,
                ByteBufCodecs.VAR_INT,
                ModuleData::currentLvl,
                ByteBufCodecs.VAR_INT,
                ModuleData::maxLvl,
                ByteBufCodecs.fromCodec(Codec.STRING.listOf()),
                ModuleData::upgradeMaterials,
                ByteBufCodecs.DOUBLE,
                ModuleData::upgradeMaterialMultiplier,
                ByteBufCodecs.BOOL,
                ModuleData::equipped,
                ByteBufCodecs.BOOL,
                ModuleData::equippable,
                ModuleData::new
        );
    }
}
