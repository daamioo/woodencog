package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.extensions.IChainData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(value = ChainConveyorBlock.class, remap = false)
public abstract class MixinChainConveyorBlock extends KineticBlock {

    protected MixinChainConveyorBlock(Properties properties) {
        super(properties);
    }

    /**
     * @author woodencog
     * @reason When a chain conveyor is wrenched, return the correct type of chain to the player instead of always vanilla chains.
     */
    @Overwrite
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return super.onSneakWrenched(state, context);

        ((ChainConveyorBlock)(Object)this).withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> {
            be.cancelDrops = true;
            if (player.isCreative())
                return;

            if (be instanceof IChainData chainData) {
                // Use the stored chain types to return the correct items to the player
                Map<BlockPos, ItemStack> connectionChainTypes = chainData.woodencog$getConnectionChainTypes();
                for (Map.Entry<BlockPos, ItemStack> entry : connectionChainTypes.entrySet()) {
                    BlockPos targetPos = entry.getKey();
                    ItemStack chainStack = entry.getValue();
                    int chainCost = ChainConveyorBlockEntity.getChainCost(targetPos);

                    if (chainStack == null || chainStack.isEmpty()) {
                        chainStack = new ItemStack(Items.CHAIN); // Fallback
                    }

                    while (chainCost > 0) {
                        int amount = Math.min(chainCost, chainStack.getMaxStackSize());
                        player.getInventory().placeItemBackInInventory(chainStack.copyWithCount(amount));
                        chainCost -= amount;
                    }
                }
            }
        });

        return super.onSneakWrenched(state, context);
    }
}