package pw.kaboom.papermixins.mixin.fix.test_log_spam;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TestBlockEntity.class)
public abstract class TestBlockEntityMixin {
    @WrapMethod(method = "log")
    private void log(final Operation<Void> original) {
        // Don't log it
    }
}
