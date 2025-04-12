# Parse as Time Duration

## Description
The `parse-as-time-duration` directive parses string values representing time durations with units into `TimeDuration` objects. This allows for easy manipulation and aggregation of time durations in your recipes.

## Syntax
```
parse-as-time-duration <column>
```

## Arguments
* **column**: The column to parse as time duration.

## Supported Units
The parser supports the following time duration units:
* ns - Nanoseconds
* ms - Milliseconds (1000 nanoseconds)
* s - Seconds (1000 * 1000 nanoseconds)
* m - Minutes (1000 * 1000 * 60 nanoseconds)
* h - Hours (1000 * 1000 * 60 * 60 nanoseconds)
* d - Days (1000 * 1000 * 60 * 60 * 24 nanoseconds)

## Examples
```
parse-as-time-duration :response_time
```

This will parse values like:
* "100ns" → 100 nanoseconds
* "500ms" → 500000000 nanoseconds
* "1.5s" → 1500000000 nanoseconds
* "2m" → 120000000000 nanoseconds
* "1h" → 3600000000000 nanoseconds
* "1d" → 86400000000000 nanoseconds

## Notes
* The parser is case-insensitive for units (e.g., "ms", "MS", "Ms" are all valid)
* Decimal values are supported (e.g., "1.5s")
* The parsed values can be used with the `aggregate-stats` directive for aggregation 