package uk.co.uwcs.pineappleguice.Authentication;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

/**
 * Authenticates the pineapples.
 */
public class PineappleAuthenticator implements Authenticator<BasicCredentials, PineapplePrincipal> {

    private static final String key = ""; // TODO(rayhaan) implement actual authentication.

    @Override
    public Optional<PineapplePrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (credentials.getPassword().equals(key)) {
            return Optional.fromNullable(new PineapplePrincipal(credentials.getUsername()));
        }
        return Optional.fromNullable(null);
    }
}
