package org.apache.lens.server.auth;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.security.token.Token;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.List;

@Path("auth")
@Authenticate
public class AuthResource {
  AuthTokenSecretManager manager = new AuthTokenSecretManager();


  @Context
  private SecurityContext securityContext;

  private static final List<String> SUPER_USERS = ImmutableList.of("Oozie");

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getDelegationToken(@QueryParam("user") String user) throws IOException {
    if (SUPER_USERS.contains(securityContext.getUserPrincipal().getName())) {
      Token<AuthTokenIdentifier> authTokenIdentifierToken = manager.generateToken(user);
      String delegationToken = authTokenIdentifierToken.encodeToUrlString();
      return delegationToken;
    } else {
      //error
      throw new IOException();
    }
  }

  public void gogo(String dele) throws IOException {
    Token<AuthTokenIdentifier> dt = new Token<>();
    dt.decodeFromUrlString(dele);
    AuthTokenIdentifier authTokenIdentifier = dt.decodeIdentifier();
  }
}
