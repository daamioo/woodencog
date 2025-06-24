package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.extensions.IChainData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public abstract class MixinChainConveyorBlockEntity_Data implements IChainData {

    @Unique
    private final Map<BlockPos, ItemStack> woodencog$connectionChainTypes = new HashMap<>();

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
        WoodenCog.LOGGER.debug("Saving chain types for BE at {}: {}", ((ChainConveyorBlockEntity)(Object)this).getBlockPos(), listTag);
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
        WoodenCog.LOGGER.debug("Loading chain types for BE at {}: {}", ((ChainConveyorBlockEntity)(Object)this).getBlockPos(), woodencog$connectionChainTypes);
    }

    @Inject(method = "removeConnectionTo(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"))
    private void woodencog$removeConnectionChainType(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ChainConveyorBlockEntity self = (ChainConveyorBlockEntity)(Object)this;
        BlockPos relativePos = pos.subtract(self.getBlockPos());
        if (woodencog$connectionChainTypes.remove(relativePos) != null) {
            WoodenCog.LOGGER.debug("Removed chain type for connection from {} to {}", self.getBlockPos(), pos);
        }
    }
}