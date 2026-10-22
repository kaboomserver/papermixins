package pw.kaboom.papermixins.mixin.perf.asynchronous_teleporter;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {
	@Shadow
	@Final
	private static SimpleCommandExceptionType INVALID_POSITION;

	@WrapMethod(method = "teleportToPos")
	private static int wrapTeleportToPos(
		final CommandSourceStack source,
		final Collection<? extends Entity> entities,
		final ServerLevel level,
		final Coordinates destination,
		final @Nullable Coordinates rotation,
		final @Nullable LookAt lookAt,
		final Operation<Integer> original
	) throws CommandSyntaxException {
		final Vec3 pos = destination.getPosition(source);
		final ChunkPos targetChunk = new ChunkPos((int) pos.x() >> 4, (int) pos.z() >> 4);

		// we don't call performTeleport() in the Brigadier context
		// this check must be performed manually, or running /tp with an illegal pos will silently fail
		final BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
		if (!Level.isInSpawnableBounds(blockPos)) {
			throw INVALID_POSITION.create();
		}

		level.getChunkSource().addTicketAndLoadWithRadius(TicketType.POST_TELEPORT, targetChunk, 2)
			.thenAcceptAsync(
				_ -> {
					try {
						original.call(source, entities, level, destination, rotation, lookAt);
					} finally {
						level.getChunkSource().removeTicketWithRadius(TicketType.POST_TELEPORT, targetChunk, 2);
					}
				},
				level.getServer()
			);

		return Command.SINGLE_SUCCESS;
	}
}
