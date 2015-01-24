package uk.co.uwcs.pineappleguice.PlayerService;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.co.uwcs.pineappleguice.QueueService.MediaBucket;
import uk.co.uwcs.pineappleguice.QueueService.MediaItem;
import uk.co.uwcs.pineappleguice.QueueService.MediaQueue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 *
 */
public class MediaPlayer implements Runnable {

    private final static Logger logger = Logger.getLogger(MediaPlayer.class.getName());

    private MediaQueue mediaQueue;

    private int timeout;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public Future<?> playerTask = null;
    private Process p;

    private MediaItem current;

    @Inject
    public MediaPlayer(MediaQueue queue, @Named("Timeout") int timeout) {
        this.mediaQueue = queue;
        this.timeout = timeout;
    }

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
                        logger.warning("Playing " + current.getPath());
                        play(current.getPath());
                        executorService = Executors.newSingleThreadExecutor();
                    } else {
                        mediaQueue.removeBucket(bucket.getUuid());
                        logger.warning("Removing empty bucket: " + bucket.getUuid());
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

    private void play(final String mediaPath) throws ExecutionException {
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/mplayer", "-fs", mediaPath);

        try {
            Future<Process> fp = executorService.submit(() -> {
                p = pb.start();

                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {}  // Read the output somewhere.
                executorService.shutdownNow();
                return p;
            });
            p = fp.get(timeout, TimeUnit.SECONDS);
            executorService.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (p != null) p.destroy();
        }
    }

    public void kill() {
        if (p != null) {
            p.destroy();
            executorService.shutdownNow();
        }
    }
}
