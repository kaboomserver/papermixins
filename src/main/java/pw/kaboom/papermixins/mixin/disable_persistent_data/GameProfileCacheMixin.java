package pw.kaboom.papermixins.mixin.disable_persistent_data;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.players.GameProfileCache;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(GameProfileCache.class)
public abstract class GameProfileCacheMixin {
    // We can't directly reference the type as it has private access,
    // and since we don't do anything besides use it as a type argument
    // it isn't really worth it to access widen.
    // Therefore, as type parameters don't really exist beyond compile
    // time, we'll just return a generic new array list and it'll
    // work anyway.

    @WrapMethod(method = "load")
    private List<?> load(final Operation<List<?>> original) {
        return Lists.newArrayList();
    }

    @WrapMethod(method = "save")
    private void save(final boolean asyncSave, final Operation<Void> original) {
        // Just don't save it.
    }
}
