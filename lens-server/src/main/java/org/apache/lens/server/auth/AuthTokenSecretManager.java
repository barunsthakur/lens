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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.apache.commons.io.Charsets;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.Token;

public class AuthTokenSecretManager extends SecretManager<AuthTokenIdentifier> {

  private SecretKey key = createSecretKey("Howdy".getBytes(Charsets.UTF_8));
  private long tokenMaxLifetime = TimeUnit.DAYS.toHours(12);

  @Override
  protected byte[] createPassword(AuthTokenIdentifier identifier) {
    long now = System.currentTimeMillis();
    identifier.setIssueDate(now);
    identifier.setExpirationDate(now + tokenMaxLifetime);
    return createPassword(identifier.getBytes(), key);
  }

  @Override
  public byte[] retrievePassword(AuthTokenIdentifier identifier) throws InvalidToken {
    long now = System.currentTimeMillis();
    if (identifier.expirationDate < now) {
      throw new InvalidToken("Token has expired");
    }
    return createPassword(identifier.getBytes(), key);
  }

  @Override
  public AuthTokenIdentifier createIdentifier() {
    return new AuthTokenIdentifier();
  }

  public Token<AuthTokenIdentifier> generateToken(String username) {
    AuthTokenIdentifier ident =
      new AuthTokenIdentifier(username);
    Token<AuthTokenIdentifier> token = new Token<>(ident, this);
//    token.setService(new Text("Lens"));
    return token;
  }

  public void verifyToken(AuthTokenIdentifier identifier, byte[] passwd) throws InvalidToken {
    byte[] retrievePassword = retrievePassword(identifier);
    if (!Arrays.equals(passwd, retrievePassword)) {
      throw new InvalidToken("token (" + identifier
        + ") is invalid, password doesn't match");
    }
  }
}
