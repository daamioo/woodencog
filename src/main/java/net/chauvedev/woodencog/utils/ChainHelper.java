package net.chauvedev.woodencog.utils;

import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|ChainHelper");
    public static boolean isChain(ItemStack stack) {
        boolean result = stack.is(ModTags.Items.CHAINS);
        LOGGER.debug("[ChainHelper] isChain check: {} -> {}", net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()), result);
        return result;
    }
}
