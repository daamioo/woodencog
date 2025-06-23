package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public class MixinChainConveyorBlockEntity_ChainDestroyed {
    /**
     * @author woodencog
     * @reason Drop any chain from the tag, prefer the one used for the connection if possible
     */
    @Overwrite
    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect) {
        int chainCount = ChainConveyorBlockEntity.getChainCost(target);
        ChainConveyorBlockEntity self = (ChainConveyorBlockEntity)(Object)this;
        Level level = self.getLevel();
        BlockPos worldPosition = self.getBlockPos();
        if (!spawnDrops)
            return;
        // Try to drop a chain from the tag, fallback to vanilla chain
        ItemStack dropStack = ItemStack.EMPTY;
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
        while (chainCount > 0) {
            Block.popResource(level, worldPosition, dropStack.copyWithCount(Math.min(chainCount, 64)));
            chainCount -= 64;
        }
    }
}
