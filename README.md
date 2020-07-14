### LogFileParser


**Given:**

Log File: Android bug report
**Sample Line: 08-27 18:52:45.942  4667  4966 I art     : Waiting for a blocking GC Alloc**
- Col #1: Date
- Col #2: Time
- Col #3 Processid
- Col #5: Log Level
- Last Column: Log Message

**Inputs to the program:**

- Process id(Use 4667)
- List of Strings to search
  - WARNING
  - OOM
  - OutOfMemoryError


**Expected Output**

- Find all FATAL crashes(will have the string, "FATAL EXCEPTION" followed by Exception message and Stacktrace)
  - List “Unique Exception messages” with the number of occurrences
  - List stack trace for every unique Fatal exception
- Find all errors(Errors are lines with log level E)
  - List Unique errors with the number of occurrences
- For the given Strings
  - List unique lines containing that string and the number of occurrences
