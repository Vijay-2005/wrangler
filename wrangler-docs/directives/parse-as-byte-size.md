# Parse as Byte Size

## Description
The `parse-as-byte-size` directive parses string values representing byte sizes with units into `ByteSize` objects. This allows for easy manipulation and aggregation of byte sizes in your recipes.

## Syntax
```
parse-as-byte-size <column>
```

## Arguments
* **column**: The column to parse as byte size.

## Supported Units
The parser supports the following byte size units:
* B - Bytes
* KB - Kilobytes (1024 bytes)
* MB - Megabytes (1024 * 1024 bytes)
* GB - Gigabytes (1024 * 1024 * 1024 bytes)
* TB - Terabytes (1024 * 1024 * 1024 * 1024 bytes)
* PB - Petabytes (1024 * 1024 * 1024 * 1024 * 1024 bytes)
* EB - Exabytes (1024 * 1024 * 1024 * 1024 * 1024 * 1024 bytes)

## Examples
```
parse-as-byte-size :data_size
```

This will parse values like:
* "10B" → 10 bytes
* "1KB" → 1024 bytes
* "1.5MB" → 1572864 bytes
* "2GB" → 2147483648 bytes

## Notes
* The parser is case-insensitive for units (e.g., "kb", "KB", "Kb" are all valid)
* Decimal values are supported (e.g., "1.5MB")
* The parsed values can be used with the `aggregate-stats` directive for aggregation 