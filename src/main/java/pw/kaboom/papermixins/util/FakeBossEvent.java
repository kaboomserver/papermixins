package pw.kaboom.papermixins.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.UUID;

public final class FakeBossEvent extends BossEvent {
    private FakeBossEvent(final UUID id, final Component name, final BossBarColor color,
                          final BossBarOverlay overlay) {
        super(id, name, color, overlay);
    }

    public static FakeBossEvent cloneWithoutName(final BossEvent event) {
        final FakeBossEvent fake = new FakeBossEvent(event.getId(), Component.empty(), event.getColor(), event.getOverlay());
        fake.progress = event.getProgress();
        fake.darkenScreen = event.shouldDarkenScreen();
        fake.playBossMusic = event.shouldPlayBossMusic();
        fake.createWorldFog = event.shouldCreateWorldFog();

        return fake;
    }
}
