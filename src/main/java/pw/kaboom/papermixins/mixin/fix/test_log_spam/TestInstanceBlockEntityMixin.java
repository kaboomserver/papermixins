package pw.kaboom.papermixins.mixin.fix.test_log_spam;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(TestInstanceBlockEntity.class)
public abstract class TestInstanceBlockEntityMixin {
    @WrapMethod(method = "export")
    private static boolean export(final ServerLevel level, final Identifier test,
                                  final Consumer<Component> messageSender, final Operation<Boolean> original) {
        return true; // fail
    }
}
