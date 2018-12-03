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
package org.apache.lens.oozie;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.oozie.action.ActionExecutor.Context;
import org.apache.oozie.action.hadoop.Credentials;
import org.apache.oozie.action.hadoop.CredentialsProperties;

public class LensCredentials extends Credentials {

  private static final Configuration CONF = new Configuration(false);
  static {
    CONF.addResource("lens-client-site.xml");
  }

  @Override
  public void addtoJobConf(JobConf jobConf, CredentialsProperties credentialsProperties, Context context)
    throws Exception {

    Token<? extends TokenIdentifier> dt = new Token<>();
    String delegationToken = null;
    dt.decodeFromUrlString(delegationToken);

    jobConf.getCredentials().addToken(dt.getService(), dt);
  }
}
