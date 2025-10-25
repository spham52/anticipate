cmake_minimum_required(VERSION 3.13)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

include(${PICO_SDK_PATH}/external/pico_sdk_import.cmake)

project(blink C CXX ASM)
pico_sdk_init()

add_executable(blink
    blink.c
)

# stdio optional
pico_enable_stdio_usb(blink 0)
pico_enable_stdio_uart(blink 0)

# Link both stdlib and the Wi-Fi/LED driver
target_link_libraries(blink
    pico_stdlib
    pico_cyw43_arch_none
)

pico_add_extra_outputs(blink)
