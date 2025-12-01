#!/bin/sh

SOURCE_FILE="$1"
MEMORY_LIMIT_KB="$2"

WORKDIR=/work
cd "$WORKDIR" || exit 1


python3 -m py_compile "$SOURCE_FILE" 2> compile_error.txt
COMPILE_EXIT=$?

if [ $COMPILE_EXIT -ne 0 ]; then
  echo "COMPILE_ERROR"
  cat compile_error.txt
  exit 0
fi

echo "OK"
exit 0
