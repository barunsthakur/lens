package org.apache.lens.driver.cube;

/*
 * #%L
 * Grill Cube Driver
 * %%
 * Copyright (C) 2014 Inmobi
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.lens.api.GrillException;
import org.apache.lens.api.query.QueryHandle;
import org.apache.lens.api.query.QueryStatus;
import org.apache.lens.driver.cube.CubeGrillDriver;
import org.apache.lens.server.api.driver.DriverQueryPlan;
import org.apache.lens.server.api.driver.GrillDriver;
import org.apache.lens.server.api.driver.GrillResultSet;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class TestCubeDriver {

  Configuration conf = new Configuration();
  CubeGrillDriver cubeDriver;

  @BeforeTest
  public void beforeTest() throws Exception {
    cubeDriver = new CubeGrillDriver(conf);
    conf.setInt("mock.driver.test.val", 5);
  }

  @AfterTest
  public void afterTest() throws Exception {
  }

  @Test
  public void testCubeDriver() throws Exception {
    String addQ = "add jar xyz.jar";
    GrillResultSet result = cubeDriver.execute(addQ, conf);
    Assert.assertNotNull(result);
    Assert.assertEquals(((MockDriver)cubeDriver.getDrivers().get(0)).query, addQ);

    String setQ = "set xyz=random";
    result = cubeDriver.execute(setQ, conf);
    Assert.assertNotNull(result);
    Assert.assertEquals(((MockDriver)cubeDriver.getDrivers().get(0)).query, setQ);

    String query = "select name from table";
    DriverQueryPlan plan = cubeDriver.explain(query, conf);
    String planString = plan.getPlan();
    Assert.assertEquals(query, planString);

    result = cubeDriver.execute(query, conf);
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getMetadata());

    QueryHandle handle = cubeDriver.executeAsync(query, conf);
    Assert.assertEquals(cubeDriver.getStatus(handle).getStatus(),
        QueryStatus.Status.SUCCESSFUL); 
    Assert.assertFalse(cubeDriver.cancelQuery(handle));

    cubeDriver.closeQuery(handle);

    Throwable th = null;
    try {
      cubeDriver.getStatus(handle);
    } catch (GrillException e) {
      th = e;
    }
    Assert.assertNotNull(th);
  }

  @Test
  public void testCubeDriverReadWrite() throws Exception {
    // Test read/write for cube driver
    CubeGrillDriver cubeDriver = new CubeGrillDriver(conf);
    ByteArrayOutputStream driverOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(driverOut);
    cubeDriver.writeExternal(out);
    out.close();
    System.out.println(Arrays.toString(driverOut.toByteArray()));
    
    ByteArrayInputStream driverIn = new ByteArrayInputStream(driverOut.toByteArray());
    conf.setInt("mock.driver.test.val", -1);
    CubeGrillDriver newDriver = new CubeGrillDriver(conf);
    newDriver.readExternal(new ObjectInputStream(driverIn));
    driverIn.close();
    Assert.assertEquals(newDriver.getDrivers().size(), cubeDriver.getDrivers().size());
    
    for (GrillDriver driver : newDriver.getDrivers()) {
      if (driver instanceof MockDriver) {
        MockDriver md = (MockDriver) driver;
        Assert.assertEquals(md.getTestIOVal(), 5);
      }
    }
  }
  
  @Test
  public void testCubeDriverRestart() throws Exception {
    // Test read/write for cube driver
    CubeGrillDriver cubeDriver = new CubeGrillDriver(conf);
    String query = "select name from table";
    QueryHandle handle = cubeDriver.executeAsync(query, conf);

    ByteArrayOutputStream driverOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(driverOut);
    cubeDriver.writeExternal(out);
    out.close();
    System.out.println(Arrays.toString(driverOut.toByteArray()));
    
    ByteArrayInputStream driverIn = new ByteArrayInputStream(driverOut.toByteArray());
    CubeGrillDriver newDriver = new CubeGrillDriver(conf);
    newDriver.readExternal(new ObjectInputStream(driverIn));
    driverIn.close();
    Assert.assertEquals(newDriver.getDrivers().size(), cubeDriver.getDrivers().size());
    
    Assert.assertEquals(cubeDriver.getStatus(handle).getStatus(),
        QueryStatus.Status.SUCCESSFUL); 
  }

}
