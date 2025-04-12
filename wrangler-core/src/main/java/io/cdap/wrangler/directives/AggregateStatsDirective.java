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

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.Arguments;

import java.util.List;

/**
 * A directive for aggregating byte size and time duration statistics.
 */
@Categories(categories = {"aggregate"})
public class AggregateStatsDirective implements Directive, Executor<List<Row>, List<Row>> {
  public static final String NAME = "aggregate-stats";
  private String sizeColumn;
  private String timeColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;
  private String sizeUnit = "MB";
  private String timeUnit = "s";
  private String aggregationType = "total";

  private long totalBytes = 0;
  private long totalNanoseconds = 0;
  private int rowCount = 0;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("size-column", TokenType.COLUMN_NAME);
    builder.define("time-column", TokenType.COLUMN_NAME);
    builder.define("total-size-column", TokenType.COLUMN_NAME);
    builder.define("total-time-column", TokenType.COLUMN_NAME);
    builder.define("size-unit", TokenType.TEXT, false);
    builder.define("time-unit", TokenType.TEXT, false);
    builder.define("aggregation-type", TokenType.TEXT, false);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) {
    if (args.size() < 4) {
      throw new IllegalArgumentException("aggregate-stats requires at least 4 arguments");
    }

    sizeColumn = (String) ((ColumnName) args.value("size-column")).value();
    timeColumn = (String) ((ColumnName) args.value("time-column")).value();
    totalSizeColumn = (String) ((ColumnName) args.value("total-size-column")).value();
    totalTimeColumn = (String) ((ColumnName) args.value("total-time-column")).value();

    if (args.contains("size-unit")) {
      sizeUnit = (String) args.value("size-unit").value();
    }
    if (args.contains("time-unit")) {
      timeUnit = (String) args.value("time-unit").value();
    }
    if (args.contains("aggregation-type")) {
      aggregationType = (String) args.value("aggregation-type").value();
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) {
    for (Row row : rows) {
      int sizeIdx = row.find(sizeColumn);
      int timeIdx = row.find(timeColumn);

      if (sizeIdx != -1) {
        Object sizeValue = row.getValue(sizeIdx);
        if (sizeValue instanceof ByteSize) {
          totalBytes += ((ByteSize) sizeValue).getBytes();
        }
      }

      if (timeIdx != -1) {
        Object timeValue = row.getValue(timeIdx);
        if (timeValue instanceof TimeDuration) {
          totalNanoseconds += ((TimeDuration) timeValue).getNanoseconds();
        }
      }

      rowCount++;
    }

    // Create a single row with the aggregated results
    Row result = new Row();
    
    // Convert total bytes to specified unit
    double totalSize = convertBytesToUnit(totalBytes, sizeUnit);
    result.add(totalSizeColumn, totalSize);

    // Convert total nanoseconds to specified unit
    double totalTime = convertNanosecondsToUnit(totalNanoseconds, timeUnit);
    if ("average".equals(aggregationType)) {
      totalTime = totalTime / rowCount;
    }
    result.add(totalTimeColumn, totalTime);

    return List.of(result);
  }

  private double convertBytesToUnit(long bytes, String unit) {
    switch (unit.toUpperCase()) {
      case "B": return bytes;
      case "KB": return bytes / 1024.0;
      case "MB": return bytes / (1024.0 * 1024.0);
      case "GB": return bytes / (1024.0 * 1024.0 * 1024.0);
      case "TB": return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
      case "PB": return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
      case "EB": return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
      default: throw new IllegalArgumentException("Unsupported size unit: " + unit);
    }
  }

  private double convertNanosecondsToUnit(long nanoseconds, String unit) {
    switch (unit.toLowerCase()) {
      case "ns": return nanoseconds;
      case "ms": return nanoseconds / 1000000.0;
      case "s": return nanoseconds / 1000000000.0;
      case "m": return nanoseconds / (1000000000.0 * 60.0);
      case "h": return nanoseconds / (1000000000.0 * 60.0 * 60.0);
      case "d": return nanoseconds / (1000000000.0 * 60.0 * 60.0 * 24.0);
      default: throw new IllegalArgumentException("Unsupported time unit: " + unit);
    }
  }

  @Override
  public void destroy() {
    // No cleanup needed
  }
} 