package org.apache.lens.server.auth;

import org.apache.hadoop.hbase.security.token.AuthenticationTokenIdentifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.Token;

import javax.crypto.SecretKey;
import java.util.concurrent.TimeUnit;

public class AuthTokenSecretManager extends SecretManager<AuthTokenIdentifier> {

  private SecretKey key = createSecretKey("Howdy".getBytes());
  private long tokenMaxLifetime = TimeUnit.DAYS.toHours(12);

  @Override
  protected byte[] createPassword(AuthTokenIdentifier identifier) {
    long now = System.currentTimeMillis();
    identifier.setKeyId(1);
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
    token.setService(new Text("Lens"));
    return token;
  }
}
