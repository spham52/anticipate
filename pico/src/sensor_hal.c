// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

// project headers
#include "sensor_hal.h"

void sensor_hal_init() {
    // initialize stdio
    stdio_init_all();

    // initialize cyw43 wifi chip
    if (cyw43_arch_init()) {
        printf("[sensor_hal] failed to initialize cyw43_arch\n");
        return;
    }

    // initialize PIR sensor pin
    gpio_init(PIR_PIN);
    gpio_set_dir(PIR_PIN, GPIO_IN);

    // initialize onboard LED pin
    gpio_init(LED_PIN);
    gpio_set_dir(LED_PIN, GPIO_OUT);
}
