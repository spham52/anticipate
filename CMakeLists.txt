cmake_minimum_required(VERSION 3.13)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# ---- Pico SDK ----
include(${PICO_SDK_PATH}/external/pico_sdk_import.cmake)

# ---- declaring project ----
# Note: We declare C and assembly as the project language types
# Note ++: littlefs CMakesList.txt enables c++, thus we do it here too
project(pico_wifi_provision C ASM CXX)
pico_sdk_init()

# ---- declaring a PICO_EXAMPLES_PATH for later sourcing ----
set(PICO_EXAMPLES_PATH "$ENV{HOME}/pico/pico-examples" CACHE PATH "Path to the pico-examples repository")

# ---- sourcing of executables ----
add_executable(pico_wifi_provision    
    src/example_main.c
    src/wifi_provisioner.c
    src/pico_fs.c
    ${PICO_EXAMPLES_PATH}/pico_w/wifi/access_point/dhcpserver/dhcpserver.c
    src/pico_dhcp.c
    src/pico_captive_portal.c
)

# ---- adding library files ----
# There are 3 stages to doing this...
# 1. targeting where the header files in example_main.c are
# 2. compiling said files
# 3. linking of the compiled files

# ---- linking directories to be included via headers ----
# NOTE: This doesn't actually compile the included header files for you
# BIGGER NOTE: Integrated DHCP servers lwipopts file sets NO_SYS as false, causing
#              ifdefs to try and compile OS arch files, thus we make sure to override
#              this option via our custom lwipopts
target_include_directories(pico_wifi_provision PRIVATE
    
    # ensure preferred lwipopts file is targeted by having it precede examples path directories
    ${CMAKE_CURRENT_LIST_DIR}/src

    # target the lwip opts file configured for access point usage
    ${PICO_EXAMPLES_PATH}/pico_w/wifi

    # pico examples dhcp server implementation
    ${PICO_EXAMPLES_PATH}/pico_w/wifi/access_point/dhcpserver

    # littlefs-lib headers (root + include/)
    ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib
    ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib/include

    # stdinit-lib (root + include/)
    ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib
    ${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib/include
)

# ---- actual compiling of the included directory files ----
# NOTE: This doesn't actually link the to the compiled files once done
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/littlefs-lib)
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/libs/pico-littlefs/stdinit-lib)

# enabling of usb stdio
pico_enable_stdio_usb(pico_wifi_provision 1)
pico_enable_stdio_uart(pico_wifi_provision 0)

# ---- actual linking of the compile files ----
target_link_libraries(pico_wifi_provision
    pico_stdlib
    pico_cyw43_arch_lwip_threadsafe_background
    stdinit-lib
    littlefs-lib
)

# ---- specify which language + version features to compile with ----
target_compile_features(pico_wifi_provision PRIVATE c_std_11)

# ---- ensure that CMake compiles a UF2 output ----
# NOTE: This is super important. Otherwise we will just have a 
#       standalone binary executable.
pico_add_extra_outputs(pico_wifi_provision)