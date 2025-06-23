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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChainConveyorBlock.class, remap = false)
public class MixinChainConveyorBlock {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|MixinChainConveyorBlock");
    @Redirect(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
        )
    )
    private boolean woodencog$allowTFCChains(ItemStack stack, net.minecraft.world.item.Item item) {
        boolean result = stack.is(net.chauvedev.woodencog.utils.ModTags.Items.CHAINS);
        LOGGER.info("[MixinChainConveyorBlock] Checking if stack {} is chain: {}", net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()), result);
        return result;
    }
}
