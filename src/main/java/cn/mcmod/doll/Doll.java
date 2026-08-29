package cn.mcmod.doll;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.stream.Stream;

public class Doll extends HorizontalDirectionalBlock {
  final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Shapes.join(
      Stream.of(
          Block.box(4, 8.8, 5.4, 12, 16.8, 13.4),
          Block.box(3.5, 8.2, 4.9, 12.5, 17.2, 13.9)
      ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
      Shapes.join(
          Block.box(5.82, 2.28, 8, 10.14, 8.76, 10.16),
          Block.box(5.67, 2.13, 7.85, 10.29, 8.91, 10.31),
          BooleanOp.OR),
      BooleanOp.OR
  ));
  
  public Doll(Properties properties) {
    super(properties.sound(SoundType.WOOL).noOcclusion());
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }
  
  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPES.get(state.getValue(Doll.FACING));
  }
  
  @Override
  protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
    return Block.simpleCodec(Doll::new);
  }
  
  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }
    
    final String line = ModMain.SPLASHES.get(level.getRandom().nextInt(0, ModMain.SPLASHES.size()));
    
    player.sendSystemMessage(Component.literal(line));
    
    return InteractionResult.SUCCESS_SERVER;
  }
  
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }
  
  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }
}
