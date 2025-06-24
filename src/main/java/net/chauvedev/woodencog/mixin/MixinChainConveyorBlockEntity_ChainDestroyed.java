package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.extensions.IChainData;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public class MixinChainConveyorBlockEntity_ChainDestroyed {
    /**
     * @author woodencog
     * @reason Drop the specific chain type used for the connection.
     */
    @Overwrite
    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect) {
        ChainConveyorBlockEntity self = (ChainConveyorBlockEntity)(Object)this;
        if (!spawnDrops)
            return;

        int chainCount = ChainConveyorBlockEntity.getChainCost(target);
        Level level = self.getLevel();
        BlockPos worldPosition = self.getBlockPos();

        ItemStack dropStack = ItemStack.EMPTY;
        if (self instanceof IChainData) {
            Map<BlockPos, ItemStack> chainTypes = ((IChainData) self).woodencog$getConnectionChainTypes();
            ItemStack storedStack = chainTypes.get(target);
            if (storedStack != null && !storedStack.isEmpty()) {
                dropStack = storedStack.copy();
                WoodenCog.LOGGER.debug("Dropping specific chain type {} for destroyed connection at {}", dropStack.getItem(), self.getBlockPos());
            }
        }

        if (dropStack.isEmpty()) {
            WoodenCog.LOGGER.debug("No specific chain type found for destroyed connection at {}, falling back.", self.getBlockPos());
            // Fallback logic from original mixin
            for (var item : net.minecraftforge.registries.ForgeRegistries.ITEMS.getValues()) {
                ItemStack stack = new ItemStack(item);
                if (stack.is(ModTags.Items.CHAINS)) {
                    dropStack = stack;
                    break;
                }
            }
            if (dropStack.isEmpty()) {
                dropStack = new ItemStack(Blocks.CHAIN.asItem());
            }
        }
        while (chainCount > 0) {
            Block.popResource(level, worldPosition, dropStack.copyWithCount(Math.min(chainCount, 64)));
            chainCount -= 64;
        }
    }
}
