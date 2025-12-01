#!/bin/sh

SOURCE_FILE="$1"
INPUT_FILE="$2"
TIME_LIMIT_MS="$3"

WORKDIR=/work
cd "$WORKDIR" || exit 1

TIME_LIMIT_SEC=$(echo "$TIME_LIMIT_MS" | awk '{print $1/1000}')


timeout "$TIME_LIMIT_SEC" python3 "$SOURCE_FILE" \
    < "$INPUT_FILE" \
    > program_output.txt \
    2> runtime_error.txt

RUN_EXIT=$?

if [ $RUN_EXIT -eq 124 ]; then
  echo "TIME_LIMIT_EXCEEDED"
  exit 0
elif [ $RUN_EXIT -ne 0 ]; then
  echo "RUNTIME_ERROR"
  cat runtime_error.txt
  exit 0
fi
if [ $RUN_EXIT -eq 137 ]; then
  echo "MEMORY_LIMIT_EXCEEDED"
  exit 0
fi
cat program_output.txt
