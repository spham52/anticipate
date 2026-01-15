#!/bin/bash

# enable error checking to bail if compiling fails
set -e

# change to project root
cd "$(dirname "$0")"

# ensure clean build wiping
rm -rf build

# invoke CMake with pico_w board type
cmake -S . -B build \
  -DPICO_SDK_PATH=$HOME/pico/pico-sdk \
  -DPICO_BOARD=pico_w \

# Build with all cores
cmake --build build --parallel

echo
echo "Compiled UF2 file location:"
echo "   $(realpath build/pico_wifi_provision.uf2)"
echo