/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.directives;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsDirectiveTest {
  @Test
  public void testAggregateStatsTotal() {
    List<Row> rows = Arrays.asList(
      createRow("10MB", "100ms"),
      createRow("5MB", "200ms"),
      createRow("15MB", "300ms")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, rows);
    Assert.assertEquals(1, results.size());
    
    Row result = results.get(0);
    Assert.assertEquals(30.0, result.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(0.6, result.getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testAggregateStatsAverage() {
    List<Row> rows = Arrays.asList(
      createRow("10MB", "100ms"),
      createRow("5MB", "200ms"),
      createRow("15MB", "300ms")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_size :response_time total_size_mb total_time_sec 'MB' 's' 'average'"
    };

    List<Row> results = TestingRig.execute(recipe, rows);
    Assert.assertEquals(1, results.size());
    
    Row result = results.get(0);
    Assert.assertEquals(30.0, result.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(0.2, result.getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testDifferentUnits() {
    List<Row> rows = Arrays.asList(
      createRow("1024KB", "1s"),
      createRow("1MB", "1000ms")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, rows);
    Assert.assertEquals(1, results.size());
    
    Row result = results.get(0);
    Assert.assertEquals(2.0, result.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(2.0, result.getValue("total_time_sec"), 0.001);
  }

  private Row createRow(String size, String time) {
    Row row = new Row();
    row.add("data_size", new ByteSize(size));
    row.add("response_time", new TimeDuration(time));
    return row;
  }
} 