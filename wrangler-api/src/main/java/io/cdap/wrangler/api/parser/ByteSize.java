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
 * A {@link Token} type for representing byte sizes with units.
 */
@PublicEvolving
public class ByteSize implements Token {
  private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)([KkMmGgTtPpEe]?[Bb])");
  private static final long[] UNIT_MULTIPLIERS = {
    1L,                    // B
    1024L,                 // KB
    1024L * 1024L,         // MB
    1024L * 1024L * 1024L, // GB
    1024L * 1024L * 1024L * 1024L, // TB
    1024L * 1024L * 1024L * 1024L * 1024L, // PB
    1024L * 1024L * 1024L * 1024L * 1024L * 1024L // EB
  };

  private final String value;
  private final long bytes;

  public ByteSize(String value) {
    this.value = value;
    Matcher matcher = BYTE_SIZE_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    double number = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2).toUpperCase();
    
    int unitIndex = 0;
    switch (unit) {
      case "B": unitIndex = 0; break;
      case "KB": unitIndex = 1; break;
      case "MB": unitIndex = 2; break;
      case "GB": unitIndex = 3; break;
      case "TB": unitIndex = 4; break;
      case "PB": unitIndex = 5; break;
      case "EB": unitIndex = 6; break;
      default: throw new IllegalArgumentException("Unsupported byte size unit: " + unit);
    }

    this.bytes = (long) (number * UNIT_MULTIPLIERS[unitIndex]);
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("value", value);
    object.addProperty("bytes", bytes);
    return object;
  }

  public long getBytes() {
    return bytes;
  }

  public String getOriginalValue() {
    return value;
  }
} 
