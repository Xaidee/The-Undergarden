package quek.undergarden.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import quek.undergarden.block.DroopvineBlock;
import quek.undergarden.registry.UGBlocks;

import java.util.Random;

public class DroopvineFeature extends Feature<NoneFeatureConfiguration> {

    private static final Direction[] directionArray = Direction.values();

    public DroopvineFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        WorldGenLevel level = pContext.level();
        BlockPos pos = pContext.origin();
        Random random = pContext.random();
        if (!level.isEmptyBlock(pos)) {
            return false;
        } else {
            BlockState blockstate = level.getBlockState(pos.above());
            if (!blockstate.is(UGBlocks.DEPTHROCK.get()) && !blockstate.is(UGBlocks.SHIVERSTONE.get())) {
                return false;
            } else {
                this.placeRoofNetherWart(level, random, pos);
                this.placeRoofDroopvine(level, random, pos);
                return true;
            }
        }
    }

    private void placeRoofNetherWart(LevelAccessor world, Random rand, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos blockpos$mutable1 = new BlockPos.MutableBlockPos();

        for(int i = 0; i < 200; ++i) {
            blockpos$mutable.setWithOffset(pos, rand.nextInt(6) - rand.nextInt(6), rand.nextInt(2) - rand.nextInt(5), rand.nextInt(6) - rand.nextInt(6));
            if (world.isEmptyBlock(blockpos$mutable)) {
                int j = 0;

                for(Direction direction : directionArray) {
                    BlockState blockstate = world.getBlockState(blockpos$mutable1.setWithOffset(blockpos$mutable, direction));
                    if (blockstate.is(UGBlocks.DEPTHROCK.get()) || blockstate.is(UGBlocks.SHIVERSTONE.get())) {
                        ++j;
                    }

                    if (j > 1) {
                        break;
                    }
                }
            }
        }
    }

    private void placeRoofDroopvine(LevelAccessor world, Random rand, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int i = 0; i < 100; ++i) {
            blockpos$mutable.setWithOffset(pos, rand.nextInt(8) - rand.nextInt(8), rand.nextInt(2) - rand.nextInt(7), rand.nextInt(8) - rand.nextInt(8));
            if (world.isEmptyBlock(blockpos$mutable)) {
                BlockState blockstate = world.getBlockState(blockpos$mutable.above());
                if (blockstate.is(UGBlocks.DEPTHROCK.get()) || blockstate.is(UGBlocks.SHIVERSTONE.get())) {
                    int length = Mth.nextInt(rand, 1, 8);
                    if (rand.nextInt(6) == 0) {
                        length *= 2;
                    }

                    if (rand.nextInt(5) == 0) {
                        length = 1;
                    }

                    placeDroopvineColumn(world, rand, blockpos$mutable, length, 17, 25);
                }
            }
        }
    }

    public static void placeDroopvineColumn(LevelAccessor world, Random rand, BlockPos.MutableBlockPos posMutable, int length, int min, int max) {
        for(int i = 0; i <= length; ++i) {
            if (world.isEmptyBlock(posMutable)) {
                if (i == length || !world.isEmptyBlock(posMutable.below())) {
                    world.setBlock(posMutable, UGBlocks.DROOPVINE_TOP.get().defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, min, max)), 2);
                    break;
                }

                world.setBlock(posMutable, UGBlocks.DROOPVINE.get().defaultBlockState().setValue(DroopvineBlock.GLOWY, world.getRandom().nextBoolean()), 2);
            }

            posMutable.move(Direction.DOWN);
        }
    }
}