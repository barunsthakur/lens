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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.TokenIdentifier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class AuthTokenIdentifier extends TokenIdentifier {
  public static final Text AUTH_TOKEN_TYPE = new Text("LENS_DELEGATION_TOKEN");

  protected Text username;
  protected long issueDate;
  protected long expirationDate;

  public AuthTokenIdentifier() {}

  public AuthTokenIdentifier(String username) {
    this.username = new Text(username);
  }

  public AuthTokenIdentifier(String username, long issueDate, long expirationDate) {
    this.username = new Text(username);
    this.issueDate = issueDate;
    this.expirationDate = expirationDate;
  }

  @Override
  public Text getKind() {
    return AUTH_TOKEN_TYPE;
  }

  @Override
  public UserGroupInformation getUser() {
    if (username == null || StringUtils.isBlank(username.toString())) {
      return null;
    }
    return UserGroupInformation.createRemoteUser(username.toString());
  }

  @Override
  public void write(DataOutput out) throws IOException {
    username.write(out);
    WritableUtils.writeVLong(out, issueDate);
    WritableUtils.writeVLong(out, expirationDate);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    username.readFields(in);
    issueDate = WritableUtils.readVLong(in);
    expirationDate = WritableUtils.readVLong(in);
  }
}
