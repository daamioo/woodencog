package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChainConveyorBlock.class, remap = false)
public class MixinChainConveyorBlock_SneakWrenched {
    // Remove unused imports

    // Remove unused parameters warning by using cir to cancel if needed, and add a comment for state/context
    @Inject(
        method = "onSneakWrenched",
        at = @At("HEAD"),
        cancellable = true
    )
    private void woodencog$allowTFCChainsSneakWrench(
        BlockState state, net.minecraft.world.item.context.UseOnContext context, CallbackInfoReturnable<InteractionResult> cir
    ) {
        Player player = context.getPlayer();
        if (player == null) {
            cir.cancel();
            return;
        }
        // Patch: Return a chain from the tag, not just vanilla chain
        if (!player.isCreative()) {
            for (var item : net.minecraftforge.registries.ForgeRegistries.ITEMS.getValues()) {
                ItemStack stack = new ItemStack(item);
                if (ChainHelper.isChain(stack)) {
                    player.getInventory().placeItemBackInInventory(stack.copyWithCount(1));
                    org.slf4j.LoggerFactory.getLogger("WoodenCog|MixinChainConveyorBlock_SneakWrenched").info("[MixinChainConveyorBlock_SneakWrenched] Returned chain {} to player {}", net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item), player.getName().getString());
                    break;
                }
            }
        }
        // Optionally, cancel the rest of the method if you want to fully override
        // cir.setReturnValue(InteractionResult.SUCCESS);
        // cir.cancel();
    }
}
