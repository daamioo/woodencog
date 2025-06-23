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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public class MixinChainConveyorBlockEntity_ChainDestroyed {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|MixinChainConveyorBlockEntity_ChainDestroyed");
    /**
     * @author woodencog
     * @reason Drop any chain from the tag, prefer the one used for the connection if possible
     */
    @Overwrite
    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect) {
        LOGGER.info("[MixinChainConveyorBlockEntity_ChainDestroyed] chainDestroyed called: target={}, spawnDrops={}, sendEffect={}", target, spawnDrops, sendEffect);
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
                LOGGER.info("[MixinChainConveyorBlockEntity_ChainDestroyed] Using chain item for drop: {}", net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()));
                break;
            }
        }
        if (dropStack.isEmpty()) {
            dropStack = new ItemStack(Blocks.CHAIN.asItem());
            LOGGER.info("[MixinChainConveyorBlockEntity_ChainDestroyed] Fallback to vanilla chain for drop");
        }
        while (chainCount > 0) {
            Block.popResource(level, worldPosition, dropStack.copyWithCount(Math.min(chainCount, 64)));
            LOGGER.info("[MixinChainConveyorBlockEntity_ChainDestroyed] Dropped {} chains at {}", Math.min(chainCount, 64), worldPosition);
            chainCount -= 64;
        }
    }
}
