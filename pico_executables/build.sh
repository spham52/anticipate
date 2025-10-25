#!/bin/bash
set -e

# Go to project root (where this script lives)
cd "$(dirname "$0")"

# Clean build folder (optional: remove if you want incremental builds)
rm -rf build

# Configure for Pico W
/snap/bin/cmake -S . -B build \
  -DPICO_SDK_PATH=$HOME/pico/pico-sdk \
  -DPICO_BOARD=pico_w \
  -DCMAKE_EXPORT_COMPILE_COMMANDS=ON

# Build with all cores
/snap/bin/cmake --build build --parallel

echo
echo "✅ Build finished. UF2 file is here:"
echo "   $(realpath build/blink.uf2)"
echo
echo "👉 To flash: copy it to /media/cooper/RPI-RP2/"

