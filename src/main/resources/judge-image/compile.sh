#!/bin/sh

SOURCE_FILE="$1"
MEMORY_LIMIT_KB="$2"

WORKDIR=/work
cd "$WORKDIR" || exit 1

BINARY_FILE="main"


g++ -O2 -std=c++17 "$SOURCE_FILE" -o "$BINARY_FILE" 2> compile_error.txt
COMPILE_EXIT=$?

if [ $COMPILE_EXIT -ne 0 ]; then
  echo "COMPILE_ERROR"
  cat compile_error.txt
  exit 0
fi

echo "OK"
exit 0
