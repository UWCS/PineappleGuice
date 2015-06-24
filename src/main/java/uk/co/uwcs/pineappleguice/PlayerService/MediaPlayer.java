package uk.co.uwcs.pineappleguice.PlayerService;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.co.uwcs.pineappleguice.QueueService.MediaBucket;
import uk.co.uwcs.pineappleguice.QueueService.MediaItem;
import uk.co.uwcs.pineappleguice.QueueService.MediaQueue;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 *
 */
public class MediaPlayer implements Runnable {

    private Logger logger;
    private MediaQueue mediaQueue;
    private int timeout;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Process p;

    @Nullable
    private MediaItem current;

    @Inject
    public MediaPlayer(MediaQueue queue, @Named("Timeout") int timeout, Logger logger) {
        this.mediaQueue = queue;
        this.timeout = timeout;
        this.logger = logger;
    }

    /**
     * Begin the service that will pull the next item out of the queue and play it.
     */
    @Override
    public void run() {
        System.out.println("Running the player service");
        try {
            while (true) {
                if (mediaQueue.getList().size() != 0) {
                    // There is something to play
                    MediaBucket bucket = mediaQueue.getFirstBucket();
                    if (bucket.hasItemToPlay()) {
                        current = bucket.getItemToPlay();
                        logger.info("Playing " + current.getPath());
                        play(current.getPath());
                        logger.info("Finished playing");
                        current = null;
                    } else {
                        // Queue is empty, move on
                        mediaQueue.removeBucket(bucket.getUuid());
                        logger.info("Removing empty bucket: " + bucket.getUuid());
                    }
                } else {
                    System.out.println("Nothing to play");
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Play a media item.
     * @param mediaPath A location to the media item, which will be passed to the player
     */
    private void play(final String mediaPath) {
        // Reinitialise the executor
        executorService = Executors.newSingleThreadExecutor();
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/mplayer",
                "-fs", "-vo", "sdl" , "-af", "volnorm", "-ass", mediaPath);

        try {
            Future<Process> fp = executorService.submit(() -> {
                p = pb.start();

                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {}  // Read the output somewhere.
                p.destroy();
                return p;
            });
            p = fp.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            p.destroy();
        } finally {
            if (p != null) p.destroy();  // Make sure the player is killed before moving on.
        }
    }

    /**
     * Stop playing the current media item.
     */
    public void kill() {
        if (p != null) {
            p.destroy();
        }
    }

    /**
     * Get the item currently playing.
     * @return Maybe something that is playing at the moment.
     */
    public Optional<MediaItem> nowPlaying() {
        return Optional.ofNullable(current);
    }

}
