package uk.co.uwcs.pineappleguice.QueueService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Queue;
import java.util.UUID;

/**
 * Represents a bucket of MediaItems to be played.
 */
public class MediaBucket {

    @JsonProperty
    private UUID uuid;
    @JsonProperty
    private HashSet<String> usersInBucket;
    @JsonProperty
    public Queue<MediaItem> queue;

    public MediaBucket() {
        this.uuid = UUID.randomUUID();
        this.usersInBucket = Sets.newHashSet();
        this.queue = Queues.newConcurrentLinkedQueue();
    }

    @JsonIgnore
    public UUID getUuid() {
        return uuid;
    }

    @JsonIgnore
    public boolean userInQueue(String username) {
        return usersInBucket.contains(username);
    }

    @JsonIgnore
    public boolean enqueue(MediaItem mediaItem) {
        if (userInQueue(mediaItem.getQueuer_username())) {
            return false;
        }
        usersInBucket.add(mediaItem.getQueuer_username());
        queue.add(mediaItem);
        return true;
    }

    @JsonIgnore
    public boolean hasItemToPlay() {
        return queue.size() > 0;
    }

    @JsonIgnore
    public MediaItem getItemToPlay() {
        Preconditions.checkArgument(queue.size() != 0);
        return queue.remove();
    }

    @JsonIgnore
    public MediaItem peekItemToPlay() {
        Preconditions.checkArgument(queue.size() != 0);
        return queue.peek();
    }

    @JsonIgnore
    public boolean deleteItem(String itemName) {
        for (MediaItem item : queue) {
            if (item.getName().equals(itemName)) {
                queue.remove(item);
                return true;
            }
        }
        return false;
    }


}
