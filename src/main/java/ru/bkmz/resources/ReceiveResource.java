package ru.bkmz.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.RateLimiter;
import com.sun.org.apache.regexp.internal.RE;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/receive")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class ReceiveResource {
    private RateLimiter rateLimiter = RateLimiter.create(30);

    @POST
    public Response add(@Valid String message) {
        if (rateLimiter.tryAcquire()) {
            return Response.created(UriBuilder.fromResource(ReceiveResource.class)
                    .build(ImmutableMap.of("value", message)))
                    .build();
        } else {
            return Response.status(429).build();
        }
    }
}
