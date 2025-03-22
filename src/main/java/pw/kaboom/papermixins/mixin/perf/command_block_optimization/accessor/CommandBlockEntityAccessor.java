package pw.kaboom.papermixins.mixin.perf.command_block_optimization.accessor;

import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CommandBlockEntity.class)
public interface CommandBlockEntityAccessor {
  @Invoker
  void invokeScheduleTick();
}
