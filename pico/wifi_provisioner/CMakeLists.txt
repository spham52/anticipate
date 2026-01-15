cmake_minimum_required(VERSION 3.13)

# ---- declaring a PICO_EXAMPLES_PATH for later sourcing ----
set(PICO_EXAMPLES_PATH "$ENV{HOME}/pico/pico-examples" CACHE PATH "Path to the pico-examples repository")

add_library(wifi_provisioner STATIC
    src/wifi_provisioner.c
    src/pico_fs.c
    src/pico_dhcp.c
    src/pico_captive_portal.c

    # Add the dhcp server from pico examples as library source
    ${PICO_EXAMPLES_PATH}/pico_w/wifi/access_point/dhcpserver/dhcpserver.c
)

# Expose headers to anything that links this lib
target_include_directories(wifi_provisioner
    PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}/src
        ${PICO_EXAMPLES_PATH}/pico_w/wifi
        # ^^^ Contains lwipopts_example_common.h that my lwipopts.h builds on 
    PRIVATE
        # Set example libs as private to ensure my lwipopts is used instead

        ${PICO_EXAMPLES_PATH}/pico_w/wifi/access_point/dhcpserver

        # littlefs and stdinit headers
        ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib
        ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib/include
        ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib
        ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib/include
)

# Build the third-party libs
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib)
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib)

# Link what provisioner needs
target_link_libraries(wifi_provisioner
    PUBLIC
        pico_stdlib
        pico_cyw43_arch_lwip_threadsafe_background
        littlefs-lib
        stdinit-lib
)