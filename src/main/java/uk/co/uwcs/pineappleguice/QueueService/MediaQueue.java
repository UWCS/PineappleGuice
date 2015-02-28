package uk.co.uwcs.pineappleguice.QueueService;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.logging.Logger;

/**
 * The queue of items itself.
 */
public class MediaQueue {

    private final static Logger logger = Logger.getLogger(MediaQueue.class.getName());

    @JsonProperty
    private LinkedHashMap<UUID, MediaBucket> queue;

    public MediaQueue() {
        this.queue = new LinkedHashMap<>();
    }

    public Collection<MediaBucket> getList() {
        return queue.values();
    }

    public MediaBucket getBucketByUuid(UUID uuid) {
        return queue.get(uuid);
    }

    public UUID enqueue(MediaItem mediaItem) {
        for (MediaBucket mb : queue.values()) {
            if (!mb.userInQueue(mediaItem.getQueuer_username())) {
                mb.enqueue(mediaItem);
                return mb.getUuid();
            }
        }
        MediaBucket newBucket = new MediaBucket();
        newBucket.enqueue(mediaItem);
        queue.put(newBucket.getUuid(), newBucket);
        return newBucket.getUuid();
    }

    public void removeBucket(UUID uuid) {
        queue.remove(uuid);
    }


    public MediaBucket getFirstBucket() {
        return queue.entrySet().iterator().next().getValue();
    }

}
