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
package org.apache.lens.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class LensDelegationTokenClient {
  private final LensConnectionParams params;
  private final LensConnection connection;

  public LensDelegationTokenClient() {
    LensClientConfig config = new LensClientConfig();
    this.params = new LensConnectionParams(config);
    this.connection = new LensConnection(params);
  }

  private WebTarget getDelegationTokenWebTarget(Client client) {
    return client.target(params.getBaseConnectionUrl()).path(
      params.getDelegationTokenResourcePath());
  }

  public String getDelegationToken(String user) {
    WebTarget target = getDelegationTokenWebTarget(connection.buildClient());
    String delegationToken = target.path("/").queryParam("user", user).request().get(String.class);
    return delegationToken;
  }
}
