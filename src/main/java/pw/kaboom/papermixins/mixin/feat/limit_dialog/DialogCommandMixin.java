package pw.kaboom.papermixins.mixin.feat.limit_dialog;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pw.kaboom.papermixins.util.DialogUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Mixin(DialogCommand.class)
public abstract class DialogCommandMixin {
    @Unique
    private static final long RATELIMIT_NANOS = TimeUnit.SECONDS.toNanos(1);
    @Unique
    private static final Map<ServerPlayer, Long> papermixins$simpleFuckingRatelimit = new WeakHashMap<>();

    @ModifyVariable(method = "showDialog", at = @At("HEAD"), name = "dialog", argsOnly = true)
    private static Holder<Dialog> modifyShowDialog(final Holder<Dialog> dialog) {
        return Holder.direct(DialogUtil.modifyDialog(dialog.value()));
    }

    @WrapMethod(method = "showDialog")
    private static int wrapShowDialog(
        final CommandSourceStack sender,
        final Collection<ServerPlayer> targets,
        final Holder<Dialog> dialog,
        final Operation<Integer> original
    ) {
        final long now = System.nanoTime();
        final List<ServerPlayer> nextTargets = new ArrayList<>();
        for (final ServerPlayer target : targets) {
            final Long lastUsed = papermixins$simpleFuckingRatelimit.get(target);

            if (lastUsed != null && (now - lastUsed) < RATELIMIT_NANOS) {
                continue;
            }

            papermixins$simpleFuckingRatelimit.put(target, now);
            nextTargets.add(target);
        }
        return original.call(sender,nextTargets,dialog);
    }
}
