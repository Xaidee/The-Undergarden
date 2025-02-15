package quek.undergarden.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;
import quek.undergarden.registry.UGBlocks;
import quek.undergarden.registry.UGFluids;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

public class UGCaveWorldCarver extends CaveWorldCarver {

    public UGCaveWorldCarver(Codec<CaveCarverConfiguration> configCodec) {
        super(configCodec);
        this.replaceableBlocks = ImmutableSet.of(
                UGBlocks.DEPTHROCK.get(),
                UGBlocks.SHIVERSTONE.get(),
                UGBlocks.DEEPTURF_BLOCK.get(),
                UGBlocks.ASHEN_DEEPTURF_BLOCK.get(),
                UGBlocks.FROZEN_DEEPTURF_BLOCK.get(),
                UGBlocks.DEEPSOIL.get(),
                UGBlocks.DEPTHROCK_COAL_ORE.get(),
                UGBlocks.DEPTHROCK_IRON_ORE.get(),
                UGBlocks.DEPTHROCK_GOLD_ORE.get(),
                UGBlocks.DEPTHROCK_DIAMOND_ORE.get(),
                UGBlocks.DEPTHROCK_CLOGGRUM_ORE.get(),
                UGBlocks.SHIVERSTONE_FROSTSTEEL_ORE.get(),
                UGBlocks.DEPTHROCK_UTHERIUM_ORE.get(),
                UGBlocks.DEPTHROCK_REGALIUM_ORE.get(),
                UGBlocks.SEDIMENT.get(),
                UGBlocks.COARSE_DEEPSOIL.get()
        );
        this.liquids = ImmutableSet.of(
                Fluids.WATER
        );
    }

    @Override
    protected float getThickness(Random random) {
        return super.getThickness(random) * 3;
    }

    @Override
    protected boolean carveEllipsoid(CarvingContext pContext, CaveCarverConfiguration pConfig, ChunkAccess pChunk, Function<BlockPos, Holder<Biome>> pBiomeAccessor, Aquifer pAquifer, double pX, double pY, double pZ, double pHorizontalRadius, double pVerticalRadius, CarvingMask pCarvingMask, WorldCarver.CarveSkipChecker pSkipChecker) {
        ChunkPos chunkPos = pChunk.getPos();
        double middleX = chunkPos.getMiddleBlockX();
        double middleZ = chunkPos.getMiddleBlockZ();
        double d2 = 16.0D + pHorizontalRadius * 2.0D;
        if (!(Math.abs(pX - middleX) > d2) && !(Math.abs(pZ - middleZ) > d2)) {
            int minX = chunkPos.getMinBlockX();
            int minZ = chunkPos.getMinBlockZ();
            int k = Math.max(Mth.floor(pX - pHorizontalRadius) - minX - 1, 0);
            int l = Math.min(Mth.floor(pX + pHorizontalRadius) - minX, 15);
            int i1 = Math.max(Mth.floor(pY - pVerticalRadius) - 1, pContext.getMinGenY() + 1);
            int j1 = pChunk.isUpgrading() ? 0 : 7;
            int k1 = Math.min(Mth.floor(pY + pVerticalRadius) + 1, pContext.getMinGenY() + pContext.getGenDepth() - 1 - j1);
            int l1 = Math.max(Mth.floor(pZ - pHorizontalRadius) - minZ - 1, 0);
            int i2 = Math.min(Mth.floor(pZ + pHorizontalRadius) - minZ, 15);
            if (this.hasDisallowedLiquid(pChunk, k, l, i1, k1, l1, i2)) {
                return false;
            }
            else {
                boolean flag = false;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                for(int j2 = k; j2 <= l; ++j2) {
                    int k2 = chunkPos.getBlockX(j2);
                    double d3 = ((double)k2 + 0.5D - pX) / pHorizontalRadius;

                    for(int l2 = l1; l2 <= i2; ++l2) {
                        int i3 = chunkPos.getBlockZ(l2);
                        double d4 = ((double)i3 + 0.5D - pZ) / pHorizontalRadius;
                        if (!(d3 * d3 + d4 * d4 >= 1.0D)) {
                            MutableBoolean mutableboolean = new MutableBoolean(false);

                            for(int j3 = k1; j3 > i1; --j3) {
                                double d5 = ((double)j3 - 0.5D - pY) / pVerticalRadius;
                                if (!pSkipChecker.shouldSkip(pContext, d3, d5, d4, j3) && !pCarvingMask.get(j2, j3, l2)) {
                                    pCarvingMask.set(j2, j3, l2);
                                    blockpos$mutableblockpos.set(k2, j3, i3);
                                    flag |= this.carveBlock(pContext, pConfig, pChunk, pBiomeAccessor, pCarvingMask, blockpos$mutableblockpos, blockpos$mutableblockpos1, pAquifer, mutableboolean);
                                }
                            }
                        }
                    }
                }

                return flag;
            }
        }
        else {
            return false;
        }
    }

    @Override
    protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, CarvingMask carvingMask, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface) {
        BlockState chunkState = chunk.getBlockState(pos);
        if (chunkState.is(UGBlocks.DEEPTURF_BLOCK.get()) || chunkState.is(UGBlocks.FROZEN_DEEPTURF_BLOCK.get()) || chunkState.is(UGBlocks.ASHEN_DEEPTURF_BLOCK.get())) {
            reachedSurface.setTrue();
        }

        if (!this.canReplaceBlock(chunkState)) {
            return false;
        }
        else {
            BlockState carveState = this.getCarveState(context, config, pos);
            if (carveState == null) {
                return false;
            }
            else {
                chunk.setBlockState(pos, carveState, false);
                if (aquifer.shouldScheduleFluidUpdate() && !carveState.getFluidState().isEmpty()) {
                    chunk.markPosForPostprocessing(pos);
                }

                if (reachedSurface.isTrue()) {
                    checkPos.setWithOffset(pos, Direction.DOWN);
                    if (chunk.getBlockState(checkPos).is(UGBlocks.DEEPSOIL.get())) {
                        context.topMaterial(biomeAccessor, chunk, checkPos, !carveState.getFluidState().isEmpty()).ifPresent((state) -> {
                            chunk.setBlockState(checkPos, state, false);
                            if (!state.getFluidState().isEmpty()) {
                                chunk.markPosForPostprocessing(checkPos);
                            }
                        });
                    }
                }
                return true;
            }
        }
    }

    @Nullable
    private BlockState getCarveState(CarvingContext context, CaveCarverConfiguration config, BlockPos pos) {
        if (pos.getY() <= config.lavaLevel.resolveY(context)) {
            return UGFluids.VIRULENT_MIX_SOURCE.get().defaultFluidState().createLegacyBlock();
        }
        else {
            return CAVE_AIR;
        }
    }

    protected boolean hasDisallowedLiquid(ChunkAccess chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        ChunkPos chunkpos = chunk.getPos();
        int minBlockX = chunkpos.getMinBlockX();
        int minBlockZ = chunkpos.getMinBlockZ();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for(int x = minX; x <= maxX; ++x) {
            for(int z = minZ; z <= maxZ; ++z) {
                for(int y = minY - 1; y <= maxY + 1; ++y) {
                    mutablePos.set(minBlockX + x, y, minBlockZ + z);
                    if (this.liquids.contains(chunk.getFluidState(mutablePos).getType())) {
                        return true;
                    }

                    if (y != maxY + 1 && !isEdge(x, z, minX, maxX, minZ, maxZ)) {
                        y = maxY;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isEdge(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        return x == minX || x == maxX || z == minZ || z == maxZ;
    }
}