package org.apache.lens.oozie;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.oozie.action.ActionExecutor.Context;
import org.apache.oozie.action.hadoop.Credentials;
import org.apache.oozie.action.hadoop.CredentialsProperties;

public class LensCredentials extends Credentials {

  private final static Configuration CONF = new Configuration(false);
  static {
    CONF.addResource("lens-client-site.xml");
  }

  @Override
  public void addtoJobConf(JobConf jobConf, CredentialsProperties credentialsProperties, Context context) throws Exception {

    Token<? extends TokenIdentifier> dt = new Token<>();
    String delegationToken = null;
    dt.decodeFromUrlString(delegationToken);

    jobConf.getCredentials().addToken(dt.getService(), dt);
  }
}
