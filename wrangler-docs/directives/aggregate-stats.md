# Aggregate Stats

## Description
The `aggregate-stats` directive aggregates byte sizes and time durations from specified columns, allowing you to calculate totals or averages with configurable output units.

## Syntax
```
aggregate-stats <size-column> <time-column> <total-size-column> <total-time-column> [size-unit] [time-unit] [aggregation-type]
```

## Arguments
* **size-column**: The column containing byte size values
* **time-column**: The column containing time duration values
* **total-size-column**: The column name for the aggregated size result
* **total-time-column**: The column name for the aggregated time result
* **size-unit** (optional): The unit for the output size (default: "MB")
* **time-unit** (optional): The unit for the output time (default: "s")
* **aggregation-type** (optional): Either "total" or "average" (default: "total")

## Supported Units
### Size Units
* B - Bytes
* KB - Kilobytes
* MB - Megabytes
* GB - Gigabytes
* TB - Terabytes
* PB - Petabytes
* EB - Exabytes

### Time Units
* ns - Nanoseconds
* ms - Milliseconds
* s - Seconds
* m - Minutes
* h - Hours
* d - Days

## Examples
### Basic Usage
```
# Parse the input columns
parse-as-byte-size :data_size
parse-as-time-duration :response_time

# Aggregate with default units (MB and seconds)
aggregate-stats :data_size :response_time total_size total_time
```

### Custom Units
```
# Parse the input columns
parse-as-byte-size :data_size
parse-as-time-duration :response_time

# Aggregate with custom units (GB and minutes)
aggregate-stats :data_size :response_time total_size total_time 'GB' 'm'
```

### Average Aggregation
```
# Parse the input columns
parse-as-byte-size :data_size
parse-as-time-duration :response_time

# Calculate averages with custom units
aggregate-stats :data_size :response_time avg_size avg_time 'MB' 's' 'average'
```

## Notes
* The directive requires that the input columns have been previously parsed using `parse-as-byte-size` and `parse-as-time-duration`
* The output is always a single row containing the aggregated values
* When using "average" aggregation type, the time value is divided by the number of rows before unit conversion
* The directive supports all the same units as the individual parsers 