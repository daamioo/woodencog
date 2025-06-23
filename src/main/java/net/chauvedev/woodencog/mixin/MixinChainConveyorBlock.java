package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChainConveyorBlock.class, remap = false)
public class MixinChainConveyorBlock {
    @Redirect(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
        )
    )
    private boolean woodencog$allowTFCChains(ItemStack stack, net.minecraft.world.item.Item unused) {
        return stack.is(net.chauvedev.woodencog.utils.ModTags.Items.CHAINS);
    }
}


