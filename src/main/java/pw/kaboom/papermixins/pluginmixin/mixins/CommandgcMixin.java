package pw.kaboom.papermixins.pluginmixin.mixins;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.Commandgc;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.bukkit.Server;
import org.spongepowered.asm.mixin.Mixin;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

@PluginMixin("Essentials")
@Mixin(Commandgc.class)
public abstract class CommandgcMixin {
    @WrapMethod(method = "run")
    private void run(Server server, CommandSource sender, String commandLabel, String[] args, Operation<Void> original) {
        System.out.println("Hello from Commandgc!");

        for (int i = 0; i < 16; i++) {
            original.call(server, sender, commandLabel, args);
        }
    }
}