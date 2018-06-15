/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.lens.server.auth;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.hadoop.security.token.Token;

import com.google.common.collect.ImmutableList;

@Path("delegation_token")
@Authenticate
public class DelegationTokenResource {
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
