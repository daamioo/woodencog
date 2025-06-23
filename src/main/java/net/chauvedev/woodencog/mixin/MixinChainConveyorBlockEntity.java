package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.utils.ChainHelper;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public class MixinChainConveyorBlockEntity {
    /**
     * @author woodencog
     * @reason Allow any chain in the tag
     */
    @Overwrite
    public static boolean getChainsFromInventory(Player player, ItemStack chain, int cost, boolean simulate) {
        int remaining = cost;
        Inventory inv = player.getInventory();
        int size = inv.items.size();
        // Only count/consume chains of the same item type as 'chain'
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
            if (!stackInSlot.is(ModTags.Items.CHAINS) || !ItemStack.isSameItemSameTags(stackInSlot, chain))
                continue;
            if (remaining <= 0)
                break;
            int count = stackInSlot.getCount();
            int toRemove = Math.min(remaining, count);
            if (!simulate && toRemove > 0) {
                ItemStack newItem = stackInSlot.copy();
                newItem.setCount(count - toRemove);
                if (offhand)
                    player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, newItem);
                else
                    inv.setItem(i, newItem);
            }
            remaining -= toRemove;
        }
        return remaining <= 0;
    }
}
