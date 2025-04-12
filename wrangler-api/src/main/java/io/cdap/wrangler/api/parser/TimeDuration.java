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

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Token} type for representing time durations with units.
 */
@PublicEvolving
public class TimeDuration implements Token {
  private static final Pattern TIME_DURATION_PATTERN = 
  Pattern.compile("(\\d+(?:\\.\\d+)?)([Nn][Ss]|[Mm][Ss]|[Ss]|[Mm]|[Hh]|[Dd])");
  private static final long[] UNIT_MULTIPLIERS = {
    1L,                    // ns
    1000L,                 // ms
    1000L * 1000L,         // s
    1000L * 1000L * 60L,   // m
    1000L * 1000L * 60L * 60L, // h
    1000L * 1000L * 60L * 60L * 24L // d
  };

  private final String value;
  private final long nanoseconds;

  public TimeDuration(String value) {
    this.value = value;
    Matcher matcher = TIME_DURATION_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    double number = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2).toLowerCase();
    
    int unitIndex = 0;
    switch (unit) {
      case "ns": unitIndex = 0; break;
      case "ms": unitIndex = 1; break;
      case "s": unitIndex = 2; break;
      case "m": unitIndex = 3; break;
      case "h": unitIndex = 4; break;
      case "d": unitIndex = 5; break;
      default: throw new IllegalArgumentException("Unsupported time duration unit: " + unit);
    }

    this.nanoseconds = (long) (number * UNIT_MULTIPLIERS[unitIndex]);
  }

  @Override
  public Object value() {
    return nanoseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("value", value);
    object.addProperty("nanoseconds", nanoseconds);
    return object;
  }

  public long getNanoseconds() {
    return nanoseconds;
  }

  public String getOriginalValue() {
    return value;
  }
} 
