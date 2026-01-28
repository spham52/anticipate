// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

// project headers
#include "sensor_hal.h"

void sensor_hal_init() {

    // initialize PIR sensor pin
    gpio_init(PIR_PIN);
    gpio_set_dir(PIR_PIN, GPIO_IN);
}

bool sensor_hal_poll() {

    return gpio_get(PIR_PIN);
}
