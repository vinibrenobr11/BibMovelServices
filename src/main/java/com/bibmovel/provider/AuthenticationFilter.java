package com.bibmovel.provider;

import com.bibmovel.values.Internals;

import java.util.List;
import java.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * This filter verify the access permissions for a user
 * based on a key provided in request
 * */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        //Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        //Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if (authorization == null) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
        } else {

            String encodedKey = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            if (encodedKey.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")) {
                String key = new String(Base64.getDecoder().decode(encodedKey));

                if (!key.equals(Internals.authKey)) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                }
            } else {
                requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            }
        }
    }
}