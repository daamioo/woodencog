package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(value = ChainConveyorConnectionHandler.class, remap = false)
public class MixinChainConveyorConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|MixinChainConveyorConnectionHandler");
    /**
     * @author woodencog
     * @reason Use tag-based chain check for client connection logic
     */
    @Overwrite
    private static boolean isChain(ItemStack itemStack) {
        boolean result = ChainHelper.isChain(itemStack);
        LOGGER.debug("[MixinChainConveyorConnectionHandler] isChain: {} -> {}", net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(itemStack.getItem()), result);
        return result;
    }
}
