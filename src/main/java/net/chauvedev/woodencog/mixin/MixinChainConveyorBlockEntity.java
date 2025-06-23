package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public class MixinChainConveyorBlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger("WoodenCog|MixinChainConveyorBlockEntity");
    /**
     * @author woodencog
     * @reason Allow any chain in the tag
     */
    @Overwrite
    public static boolean getChainsFromInventory(Player player, ItemStack chain, int cost, boolean simulate) {
        LOGGER.info("[MixinChainConveyorBlockEntity] getChainsFromInventory called: player={}, chain={}, cost={}, simulate={}", player.getName().getString(), net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(chain.getItem()), cost, simulate);
        int found = 0;
        Inventory inv = player.getInventory();
        int size = inv.items.size();
        for (int j = 0; j <= size + 1; j++) {
            int i = j;
            boolean offhand = j == size + 1;
            if (j == size)
                i = inv.selected;
            else if (offhand)
                i = 0;
            else if (j == inv.selected)
                continue;
            ItemStack stackInSlot = (offhand ? inv.offhand : inv.items).get(i);
            LOGGER.debug("[MixinChainConveyorBlockEntity] Checking slot {} (offhand={}): {} x{}", i, offhand, net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stackInSlot.getItem()), stackInSlot.getCount());
            if (!stackInSlot.is(ModTags.Items.CHAINS))
                continue;
            if (found >= cost)
                continue;
            int count = stackInSlot.getCount();
            if (!simulate) {
                int remainingItems = count - Math.min(cost - found, count);
                if (i == inv.selected)
                    stackInSlot.setTag(null);
                ItemStack newItem = ItemHandlerHelper.copyStackWithSize(stackInSlot, remainingItems);
                if (offhand)
                    player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, newItem);
                else
                    inv.setItem(i, newItem);
                LOGGER.info("[MixinChainConveyorBlockEntity] Consumed {} chains from slot {} (offhand={})", Math.min(cost - found, count), i, offhand);
            }
            found += count;
        }
        LOGGER.info("[MixinChainConveyorBlockEntity] Found {} chains, needed {}: {}", found, cost, found >= cost);
        return found >= cost;
    }
}
