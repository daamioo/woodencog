package net.chauvedev.woodencog.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface IChainData {
    Map<BlockPos, ItemStack> woodencog$getConnectionChainTypes();
}