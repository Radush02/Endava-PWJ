#!/bin/sh
set -e

SOURCE_FILE="$1"
INPUT_FILE="$2" 
TIME_LIMIT_MS="$3" 
MEMORY_LIMIT_KB="$4" 

WORKDIR=/work
cd "$WORKDIR"

BINARY_FILE="main"


g++ -O2 -std=c++17 "$SOURCE_FILE" -o "$BINARY_FILE" 2> compile_error.txt
COMPILE_EXIT=$?

if [ $COMPILE_EXIT -ne 0 ]; then
  echo "COMPILE_ERROR"
  cat compile_error.txt
  exit 0
fi



TIME_LIMIT_SEC=$((TIME_LIMIT_MS / 1000))
if [ "$TIME_LIMIT_SEC" -le 0 ]; then
  TIME_LIMIT_SEC=1
fi

ulimit -v "$MEMORY_LIMIT_KB"

timeout "$TIME_LIMIT_SEC" ./"$BINARY_FILE" \
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

echo "OK"
cat program_output.txt
