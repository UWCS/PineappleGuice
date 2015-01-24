package uk.co.uwcs.pineappleguice.Resources;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;

import java.io.IOException;

/**
 * Created by rayhaan on 22/01/15.
 */
@AtmosphereHandlerService(path = "/chat")
public class WebSocketService implements AtmosphereHandler {

    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {
        AtmosphereRequest request = resource.getRequest();
        resource.suspend();
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {

    }

    @Override
    public void destroy() {

    }



}
