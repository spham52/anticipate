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

    // set pull-up resistor on PIR sensor pin
    gpio_pull_up(PIR_PIN);
}

bool sensor_hal_poll() {

    for (int i = 0; i < 10; i++) {
        if (!gpio_get(PIR_PIN)) {    // check if motion remains detected throughout loop
            return false;
        }
        sleep_ms(10);
    }

    return true;
}