package uk.co.uwcs.pineappleguice.Resources;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.core.header.FormDataContentDisposition;
import uk.co.uwcs.pineappleguice.MediaModule;
import uk.co.uwcs.pineappleguice.PlayerService.MediaPlayer;
import uk.co.uwcs.pineappleguice.QueueService.MediaBucket;
import uk.co.uwcs.pineappleguice.QueueService.MediaItem;
import uk.co.uwcs.pineappleguice.QueueService.MediaQueue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sun.jersey.multipart.FormDataParam;
import uk.co.uwcs.pineappleguice.YoutubeDownloader.DownloadService;


/**
 * JSON interface for the music queue.
 */

@Path("/queue")
@Produces("application/json; charset=utf-8")
public class MusicQueueResource {

    private MediaQueue queue;
    private MediaPlayer player;

    @Inject
    public MusicQueueResource(MediaQueue queue, MediaPlayer player) {
        this.queue = queue;
        this.player = player;
    }

    @GET

    public Collection<MediaBucket> getMediaList() {
        return queue.getList();
    }

    @GET
    @Path("/kill")
    public void kill() {
        player.kill();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public MediaItem upload(@FormDataParam("file") final InputStream file,
                            @FormDataParam("file") final FormDataContentDisposition meta,
                            @Context HttpServletRequest request) {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(meta);
        MediaItem mi = new MediaItem.Builder()
                .name(meta.getFileName())
                .queuer_username(request.getRemoteHost())
                .type(MediaItem.MediaType.LOCAL_VIDEO)
                .path(meta.getFileName())
                .build();

        saveFile(file, meta.getFileName());
        queue.enqueue(mi);
        return mi;
    }

    @GET
    @Path("/youtube")
    public MediaItem downloadYoutube(@QueryParam("url") String url,
                                  @Context HttpServletRequest request) throws Exception {
        Injector injector = Guice.createInjector(new MediaModule());
        DownloadService ds = injector.getInstance(DownloadService.class);
        ds.setURL(url);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> stringFuture = service.submit(ds);
        String fileName = stringFuture.get();

        MediaItem mi = new MediaItem.Builder()
                .name(fileName)
                .queuer_username(request.getRemoteHost())
                .type(MediaItem.MediaType.YOUTUBE)
                .path(fileName)
                .build();
        queue.enqueue(mi);
        return mi;
    }

    private void saveFile(InputStream is, String filename) {
        try {
            OutputStream os = new FileOutputStream(new File(filename));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/delete")
    public boolean delete(@PathParam("bucketId") String bucketUUID, @PathParam("itemName") String mediaItem) {
        MediaBucket bucket = queue.getBucketByUuid(UUID.fromString(bucketUUID));
        Preconditions.checkNotNull(bucket);
        return bucket.deleteItem(mediaItem);
    }
}
