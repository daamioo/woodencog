package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.extensions.IChainData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ChainConveyorConnectionPacket.class, remap = false)
public class MixinChainConveyorConnectionPacket {
    @Shadow private BlockPos targetPos;
    @Shadow private boolean connect;
    @Shadow private ItemStack chain;
    
    /**
     * @author woodencog
     * @reason Use tag for chain checks and consumption, and store specific chain type
     */
    @Overwrite
    protected void applySettings(ServerPlayer player, ChainConveyorBlockEntity be) {
        int maxRange = com.simibubi.create.infrastructure.config.AllConfigs.server().kinetics.maxChainConveyorLength.get() + 16;
        if (!be.getBlockPos().closerThan(targetPos, maxRange - 16 + 1)) {
            return;
        }
        if (!(be.getLevel().getBlockEntity(targetPos) instanceof ChainConveyorBlockEntity clbe)) {
            return;
        }

        BlockPos bePos = be.getBlockPos();
        BlockPos clbePos = clbe.getBlockPos();
        BlockPos relativePos = clbePos.subtract(bePos);

        if (connect && !player.isCreative()) {
            int chainCost = ChainConveyorBlockEntity.getChainCost(relativePos);
            boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory(player, chain, chainCost, true);
            if (!hasEnough)
                return;
            ChainConveyorBlockEntity.getChainsFromInventory(player, chain, chainCost, false);
        }

        if (!connect) { // disconnect
            if (!player.isCreative()) {
                int chainCost = ChainConveyorBlockEntity.getChainCost(relativePos);
                ItemStack storedChain = ((IChainData) be).woodencog$getConnectionChainTypes().get(relativePos);
                ItemStack refundStack = (storedChain != null && !storedChain.isEmpty()) ? storedChain.copy() : new ItemStack(net.minecraft.world.level.block.Blocks.CHAIN.asItem());

                WoodenCog.LOGGER.debug("Refunding chain type {} for connection from {} to {}", refundStack.getItem(), bePos, clbePos);

                int left = chainCost;
                while (left > 0) {
                    player.getInventory().placeItemBackInInventory(refundStack.copyWithCount(Math.min(left, 64)));
                    left -= 64;
                }
            }
            be.chainDestroyed(relativePos, false, true);
            be.getLevel().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.CHAIN_BREAK, net.minecraft.sounds.SoundSource.BLOCKS);
        }

        if (connect) {
            if (!clbe.addConnectionTo(be.getBlockPos())) {
                return;
            }
        } else {
            clbe.removeConnectionTo(be.getBlockPos());
        }

        if (connect) {
            if (!be.addConnectionTo(targetPos)) { // Fails, so rollback
                clbe.removeConnectionTo(be.getBlockPos()); 
            } else {
                // Connection successful, store chain type
                BlockPos clbeRelative = bePos.subtract(clbePos);
                ItemStack chainType = chain.copy();
                chainType.setCount(1);

                ((IChainData) be).woodencog$getConnectionChainTypes().put(relativePos, chainType);
                ((IChainData) clbe).woodencog$getConnectionChainTypes().put(clbeRelative, chainType);
                WoodenCog.LOGGER.debug("Set chain type to {} for connection between {} and {}", chainType.getItem(), bePos, clbePos);
            }
        } else {
            be.removeConnectionTo(targetPos);
        }
    }
}
