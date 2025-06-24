package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.extensions.IChainData;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public abstract class MixinChainConveyorBlockEntity extends KineticBlockEntity implements IChainData {

    @Shadow public Set<BlockPos> connections;
    @Shadow public boolean cancelDrops;
    @Shadow public List<ChainConveyorPackage> loopingPackages;
    @Shadow public Map<BlockPos, List<ChainConveyorPackage>> travellingPackages;
    @Shadow protected abstract void drop(ChainConveyorPackage box);
    @Shadow BlockPos chainDestroyedEffectToSend;

    @Unique
    private final Map<BlockPos, ItemStack> woodencog$connectionChainTypes = new HashMap<>();

    // Dummy constructor for mixin inheritance
    public MixinChainConveyorBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Map<BlockPos, ItemStack> woodencog$getConnectionChainTypes() {
        return woodencog$connectionChainTypes;
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void woodencog$writeConnectionChainTypes(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        ListTag listTag = new ListTag();
        woodencog$connectionChainTypes.forEach((pos, stack) -> {
            if (stack.isEmpty()) return;
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("Pos", NbtUtils.writeBlockPos(pos));
            entryTag.put("Chain", stack.save(new CompoundTag()));
            listTag.add(entryTag);
        });
        compound.put("ConnectionChainTypes", listTag);
        WoodenCog.LOGGER.debug("Saving chain types for BE at {}: {}", this.getBlockPos(), listTag);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void woodencog$readConnectionChainTypes(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        woodencog$connectionChainTypes.clear();
        if (compound.contains("ConnectionChainTypes", Tag.TAG_LIST)) {
            ListTag listTag = compound.getList("ConnectionChainTypes", Tag.TAG_COMPOUND);
            for (Tag t : listTag) {
                if (t instanceof CompoundTag entryTag) {
                    BlockPos pos = NbtUtils.readBlockPos(entryTag.getCompound("Pos"));
                    ItemStack stack = ItemStack.of(entryTag.getCompound("Chain"));
                    woodencog$connectionChainTypes.put(pos, stack);
                }
            }
        }
        WoodenCog.LOGGER.debug("Loading chain types for BE at {}: {}", this.getBlockPos(), woodencog$connectionChainTypes);
    }

    @Inject(method = "removeConnectionTo(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"))
    private void woodencog$removeConnectionChainType(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos relativePos = pos.subtract(this.getBlockPos());
        if (woodencog$connectionChainTypes.remove(relativePos) != null) {
            WoodenCog.LOGGER.debug("Removed chain type for connection from {} to {}", this.getBlockPos(), pos);
        }
    }

    /**
     * @author woodencog
     * @reason Allow any chain in the tag, but only consume the specific type used.
     */
    @Overwrite
    public static boolean getChainsFromInventory(Player player, ItemStack chain, int cost, boolean simulate) {
        int remaining = cost;
        Inventory inv = player.getInventory();
        int size = inv.items.size();
        for (int j = 0; j <= size + 1; j++) {
            int i = j;
            boolean offhand = j == size + 1;
            if (j == size) i = inv.selected;
            else if (offhand) i = 0;
            else if (j == inv.selected) continue;

            ItemStack stackInSlot = (offhand ? inv.offhand : inv.items).get(i);
            if (!ItemStack.isSameItemSameTags(stackInSlot, chain)) continue;
            if (remaining <= 0) break;

            int count = stackInSlot.getCount();
            int toRemove = Math.min(remaining, count);
            if (!simulate && toRemove > 0) {
                ItemStack newItem = stackInSlot.copy();
                newItem.setCount(count - toRemove);
                if (offhand) player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, newItem);
                else inv.setItem(i, newItem);
            }
            remaining -= toRemove;
        }
        return remaining <= 0;
    }

    /**
     * @author woodencog
     * @reason Drop the specific chain type used for the connection.
     */
    @Overwrite
    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect) {
        ChainConveyorBlockEntity self = (ChainConveyorBlockEntity) (Object) this;
        int chainCount = ChainConveyorBlockEntity.getChainCost(target);
        if (sendEffect) {
            this.chainDestroyedEffectToSend = target;
            self.sendData();
        }
        if (!spawnDrops) return;

        Level level = self.getLevel();
        BlockPos worldPosition = self.getBlockPos();

        ItemStack dropStack = woodencog$connectionChainTypes.get(target);
        if (dropStack == null || dropStack.isEmpty()) {
            WoodenCog.LOGGER.debug("No specific chain type found for destroyed connection at {}, falling back.", self.getBlockPos());
            dropStack = new ItemStack(Items.CHAIN); // Simple fallback
        } else {
            WoodenCog.LOGGER.debug("Dropping specific chain type {} for destroyed connection at {}", dropStack.getItem(), self.getBlockPos());
        }

        final ItemStack finalDropStack = dropStack.copy();
        if (!self.forPointsAlongChains(target, chainCount, vec -> level.addFreshEntity(new ItemEntity(level, vec.x, vec.y, vec.z, finalDropStack.copy())))) {
            int remaining = chainCount;
            while (remaining > 0) {
                Block.popResource(level, worldPosition, finalDropStack.copyWithCount(Math.min(remaining, 64)));
                remaining -= 64;
            }
        }
    }

    /**
     * @author woodencog
     * @reason The original destroy logic can fail to drop custom chains because other game logic may clear the connection data before drops are handled. This overwrite ensures drops happen correctly and in the right order.
     */
    @Overwrite
    public void destroy() {
        super.destroy();
        for (BlockPos relativePos : new HashSet<>(connections)) {
            this.chainDestroyed(relativePos, !this.cancelDrops, false);
            BlockEntity otherBE = level.getBlockEntity(worldPosition.offset(relativePos));
            if (otherBE instanceof ChainConveyorBlockEntity otherCCBE) {
                otherCCBE.removeConnectionTo(worldPosition);
            }
        }
        for (ChainConveyorPackage box : loopingPackages) drop(box);
        for (Map.Entry<BlockPos, List<ChainConveyorPackage>> entry : travellingPackages.entrySet())
            for (ChainConveyorPackage box : entry.getValue()) drop(box);
    }
}