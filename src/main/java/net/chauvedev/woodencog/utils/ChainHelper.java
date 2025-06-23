package net.chauvedev.woodencog.utils;

import net.minecraft.world.item.ItemStack;


public class ChainHelper {
    public static boolean isChain(ItemStack stack) {
        boolean result = stack.is(ModTags.Items.CHAINS);
      
        return result;
    }
}
