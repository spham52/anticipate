cmake_minimum_required(VERSION 3.13)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# Inclde the Pico SDK
include(${PICO_SDK_PATH}/external/pico_sdk_import.cmake)

project(pico_anticipate C ASM CXX)
pico_sdk_init()

# Keep your pico-examples path here so subdirs can use it
set(PICO_EXAMPLES_PATH "$ENV{HOME}/pico/pico-examples"
    CACHE PATH "Path to the pico-examples repository")

# 1) Build the provisioner library
add_subdirectory(wifi_provisioner)

# 2) Build your real application entrypoint
add_executable(app
    src/main.c
)

# main.c includes wifi_provisioner.h, so include comes from the library PUBLIC includes.
target_link_libraries(app
    pico_stdlib
    wifi_provisioner
    pico_cyw43_arch_none
)

pico_enable_stdio_usb(app 1)
pico_enable_stdio_uart(app 0)

target_compile_features(app PRIVATE c_std_11)
pico_add_extra_outputs(app)