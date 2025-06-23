package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorConnectionHandler.class, remap = false)
public class MixinChainConveyorConnectionHandler {
    /**
     * @author woodencog
     * @reason Use tag-based chain check for client connection logic
     */
    @Overwrite
    private static boolean isChain(ItemStack itemStack) {
        return ChainHelper.isChain(itemStack);
    }
}
