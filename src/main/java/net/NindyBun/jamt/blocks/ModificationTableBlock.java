package net.NindyBun.jamt.blocks;

import net.NindyBun.jamt.Registries.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ModificationTableBlock extends Block implements EntityBlock{
    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ModificationTableBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(2.0F)
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shape.getFromFacing(pState.getValue(HorizontalDirectionalBlock.FACING));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModEntities.MODIFICATION_TABLE_ENTITY.get().create(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    private enum Shape {
        NORTH(Stream.of(
                Block.box(0, 0, 0, 16, 12, 16),
                Block.box(1, 12, 1, 15, 13, 15)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        EAST(Stream.of(
                Block.box(0, 0, 0, 16, 12, 16),
                Block.box(1, 12, 1, 15, 13, 15)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        SOUTH(Stream.of(
                Block.box(0, 0, 0, 16, 12, 16),
                Block.box(1, 12, 1, 15, 13, 15)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()),
        WEST(Stream.of(
                Block.box(0, 0, 0, 16, 12, 16),
                Block.box(1, 12, 1, 15, 13, 15)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());

        private final VoxelShape shape;

        Shape(VoxelShape shape) {
            this.shape = shape;
        }

        public static VoxelShape getFromFacing(Direction facing) {
            return switch (facing) {
                case NORTH -> NORTH.shape;
                case EAST -> EAST.shape;
                case SOUTH -> SOUTH.shape;
                case WEST -> WEST.shape;
                default -> throw new IllegalStateException("Invalid facing");
            };
        }
    }
}
