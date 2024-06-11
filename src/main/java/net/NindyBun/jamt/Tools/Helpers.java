package net.NindyBun.jamt.Tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Helpers {

    public static String fancyNumber(int number) {
        if (number < 1000)
            return String.valueOf(number);
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f%c",
                number / Math.pow(1000, exp),
                "kMGTPE_____".charAt(exp - 1));
    }

}
