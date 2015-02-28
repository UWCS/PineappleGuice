package uk.co.uwcs.pineappleguice.HealthCheck;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import uk.co.uwcs.pineappleguice.PlayerService.MediaPlayer;

/**
 * Simple healthcheck that looks if there is an instance of a player.
 */
public class MediaServiceHealthCheck extends HealthCheck {

    private MediaPlayer player;

    public MediaServiceHealthCheck(MediaPlayer player) {
        this.player = player;
    }

    @Override
    protected Result check() throws Exception {
        if (player != null) {
            return Result.healthy();
        }
        return Result.unhealthy("MediaPlayer is null!");
    }
}
