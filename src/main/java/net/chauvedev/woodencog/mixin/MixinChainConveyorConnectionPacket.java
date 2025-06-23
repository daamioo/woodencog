package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(value = ChainConveyorConnectionPacket.class, remap = false)
public class MixinChainConveyorConnectionPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|MixinChainConveyorConnectionPacket");
    @Shadow private BlockPos targetPos;
    @Shadow private boolean connect;
    @Shadow private ItemStack chain;

    /**
     * @author woodencog
     * @reason Use tag for chain checks and consumption
     */
    @Overwrite
    protected void applySettings(ServerPlayer player, ChainConveyorBlockEntity be) {
        LOGGER.info("[MixinChainConveyorConnectionPacket] applySettings called: player={}, be={}, targetPos={}, connect={}, chain={}", player.getName().getString(), be.getBlockPos(), targetPos, connect, net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(chain.getItem()));
        int maxRange = com.simibubi.create.infrastructure.config.AllConfigs.server().kinetics.maxChainConveyorLength.get() + 16;
        if (!be.getBlockPos().closerThan(targetPos, maxRange - 16 + 1)) {
            LOGGER.warn("[MixinChainConveyorConnectionPacket] Out of range");
            return;
        }
        if (!(be.getLevel().getBlockEntity(targetPos) instanceof ChainConveyorBlockEntity clbe)) {
            LOGGER.warn("[MixinChainConveyorConnectionPacket] Target is not a ChainConveyorBlockEntity");
            return;
        }

        if (connect && !player.isCreative()) {
            int chainCost = ChainConveyorBlockEntity.getChainCost(targetPos.subtract(be.getBlockPos()));
            boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory(player, chain, chainCost, true);
            LOGGER.info("[MixinChainConveyorConnectionPacket] Checking for chains: cost={}, hasEnough={}", chainCost, hasEnough);
            if (!hasEnough)
                return;
            ChainConveyorBlockEntity.getChainsFromInventory(player, chain, chainCost, false);
            LOGGER.info("[MixinChainConveyorConnectionPacket] Consumed chains for connection");
        }

        if (!connect) {
            if (!player.isCreative()) {
                int chainCost = ChainConveyorBlockEntity.getChainCost(targetPos.subtract(be.getBlockPos()));
                for (var item : net.minecraftforge.registries.ForgeRegistries.ITEMS.getValues()) {
                    ItemStack stack = new ItemStack(item);
                    if (!stack.is(net.chauvedev.woodencog.utils.ModTags.Items.CHAINS)) continue;
                    int left = chainCost;
                    while (left > 0) {
                        player.getInventory().placeItemBackInInventory(stack.copyWithCount(Math.min(left, 64)));
                        LOGGER.info("[MixinChainConveyorConnectionPacket] Returned {} chains to player", Math.min(left, 64));
                        left -= 64;
                    }
                    break;
                }
            }
            be.chainDestroyed(targetPos.subtract(be.getBlockPos()), false, true);
            be.getLevel().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.CHAIN_BREAK, net.minecraft.sounds.SoundSource.BLOCKS);
            LOGGER.info("[MixinChainConveyorConnectionPacket] Broke connection and played sound");
        }

        if (connect) {
            if (!clbe.addConnectionTo(be.getBlockPos())) {
                LOGGER.warn("[MixinChainConveyorConnectionPacket] Failed to add connection to clbe");
                return;
            }
        } else
            clbe.removeConnectionTo(be.getBlockPos());

        if (connect) {
            if (!be.addConnectionTo(targetPos)) {
                LOGGER.warn("[MixinChainConveyorConnectionPacket] Failed to add connection to be, rolling back");
                clbe.removeConnectionTo(be.getBlockPos());
            }
        } else
            be.removeConnectionTo(targetPos);
    }
}
