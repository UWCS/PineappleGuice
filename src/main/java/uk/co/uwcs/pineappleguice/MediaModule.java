package uk.co.uwcs.pineappleguice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.guice.AtmosphereGuiceServlet;
import uk.co.uwcs.pineappleguice.QueueService.MediaQueue;

import java.util.logging.Logger;

/**
 * Module configuration
 */
public class MediaModule extends AbstractModule {

    private Logger logger;

    public MediaModule(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void configure() {
        bind(MediaQueue.class).in(Singleton.class);  // Have one media queue for this app.
        // Maintained here for legacy reasons (consult MrWilson).
        // Set as 9:07. This is the length of Jeff Wayne's "War of the Worlds", Track 1. These two things are unrelated.
        bindConstant().annotatedWith(Names.named("Timeout")).to(541);  // Timeout for player.
        bindConstant().annotatedWith(Names.named("downloaderPath")).to("/usr/local/bin/youtube-dl");  // Path to download script.
    }


}
