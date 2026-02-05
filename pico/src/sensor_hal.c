// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

// project headers
#include "sensor_hal.h"

void sensor_hal_init() {

    iterations_before_active = 0;

    // initialize PIR sensor pin
    gpio_init(PIR_PIN);
    gpio_set_dir(PIR_PIN, GPIO_IN);

    // set pull-up resistor on PIR sensor pin
    gpio_pull_up(PIR_PIN);

    // sleep for 5s to ensure no sticking on startup
    sleep_ms(5000);
}

bool sensor_hal_is_active() {

    if (iterations_before_active > 0) {
        iterations_before_active--;
        sleep_ms(500);

        return false;
    }

    for (int i = 0; i < 10; i++) {
        if (!gpio_get(PIR_PIN)) {    // check if motion remains detected throughout loop
            return false;
        }
        sleep_ms(10);
    }

    iterations_before_active = 6;
    return true;
}