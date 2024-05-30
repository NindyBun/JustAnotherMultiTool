package net.NindyBun.jamt.items;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ModuleCard extends Item {
    private Modules module;

    public ModuleCard(Modules module) {
        super(new Properties().stacksTo(1));
        this.module = module;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.translatable(this.module.getToolTip()).withStyle(ChatFormatting.GRAY));
    }

    public Modules getModule() {
        return this.module;
    }
}
