package net.chauvedev.woodencog.mixin;

import com.simibubi.create.foundation.events.InputEvents;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InputEvents.class, remap = false)
public class MixinInputEvents {
    @Redirect(
        method = "onClickInput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
        )
    )
    private static boolean woodencog$allowTFCChains(ItemStack stack, net.minecraft.world.item.Item item) {
        return stack.is(ModTags.Items.CHAINS);
    }
}
