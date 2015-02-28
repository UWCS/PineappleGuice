package uk.co.uwcs.pineappleguice;

import com.codahale.metrics.JmxReporter;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.Application;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.co.uwcs.pineappleguice.Authentication.PineappleAuthenticator;
import uk.co.uwcs.pineappleguice.HealthCheck.MediaServiceHealthCheck;
import uk.co.uwcs.pineappleguice.PlayerService.MediaPlayer;
import uk.co.uwcs.pineappleguice.QueueService.MediaQueue;
import uk.co.uwcs.pineappleguice.Resources.MusicQueueResource;

import java.util.logging.Logger;

/**
 * Application class for the music server.
 */
public class MusicServer extends Application<PineappleGuiceConfiguration> {

    private final static Logger logger = Logger.getLogger(MusicServer.class.getName());

    public static void main (String... args) throws Exception {
        Injector injector = Guice.createInjector(new MediaModule(logger));
        injector.getInstance(MusicServer.class).run(new String[]{"server"});
    }

    private MediaQueue mediaQueue;
    private MediaPlayer mediaPlayer;

    @Inject
    public MusicServer(MediaQueue mediaQueue, MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaQueue = mediaQueue;
    }

    @Override
    public void initialize(Bootstrap<PineappleGuiceConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public void run(PineappleGuiceConfiguration configuration, Environment environment) throws Exception {
        Thread t = new Thread(mediaPlayer);
        t.start();

        environment.jersey().setUrlPattern("/api/*");
        JmxReporter.forRegistry(environment.metrics()).build().start();
        environment.jersey().register(new MusicQueueResource(mediaQueue , mediaPlayer));
        environment.jersey().register(new BasicAuthProvider<>(
                new PineappleAuthenticator(), "Admin zone!"));
        environment.healthChecks().register("mediaPlayer", new MediaServiceHealthCheck(mediaPlayer));

    }

}
