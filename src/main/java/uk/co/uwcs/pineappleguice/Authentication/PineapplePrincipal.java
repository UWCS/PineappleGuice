package uk.co.uwcs.pineappleguice.Authentication;

import java.util.List;

/**
 * Token representing a user in the pineapple.
 */
public class PineapplePrincipal {
    private String username;
    private List<String> roles;

    public PineapplePrincipal(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public boolean userHasRole(String role) {
        return roles.contains(role);
    }

    public String getUsername() {
        return username;
    }

}
