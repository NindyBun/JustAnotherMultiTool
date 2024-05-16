package net.NindyBun.jamt.Tools;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolMethods {
    public static List<Modules.ModuleData> initModuleData(ItemStack stack) {
        List<Modules.ModuleData> moduleData = new ArrayList<>();
        Arrays.stream(Modules.values()).toList().forEach(module -> {
            moduleData.add(new Modules.ModuleData(module.name, module.currentLvl, module.maxLvl, module.upgradeMaterials, module.upgradeMaterialMultiplier, module.equipped, module.equippable));
        });
        stack.set(ModDataComponents.MODULES.get(), moduleData);
        return moduleData;
    }

    public static void updateModuleData(ItemStack stack, Modules module) {
        List<Modules.ModuleData> moduleData = stack.getOrDefault(ModDataComponents.MODULES.get(), ToolMethods.initModuleData(stack));

        moduleData.forEach(data -> {
            if (data.moduleName().equals(module.name))
                data = new Modules.ModuleData(module.name, module.currentLvl, module.maxLvl, module.upgradeMaterials, module.upgradeMaterialMultiplier, module.equipped, module.equippable);
        });

        stack.set(ModDataComponents.MODULES.get(), moduleData);
    }

    public static List<Modules> getModules(ItemStack stack) {
        List<Modules.ModuleData> moduleData = stack.getOrDefault(ModDataComponents.MODULES.get(), ToolMethods.initModuleData(stack));

        List<Modules> modules = new ArrayList<>();

        for (Modules.ModuleData data : moduleData) {
            Modules module = Modules.valueOf(data.moduleName().toUpperCase());
            if (module == null)
                continue;
            module.sync(data);
            modules.add(module);
        }

        return modules;
    }
}
