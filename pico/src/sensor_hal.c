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

    // initialize onboard LED pin
    gpio_init(LED_PIN);
    gpio_set_dir(LED_PIN, GPIO_OUT);
}

void sensor_hal_poll() {

    if (gpio_get(PIR_PIN)) {
        printf("Motion Detected!\n");
        gpio_put(LED_PIN, 1); // LED on
        sleep_ms(1000);       // Debounce/Hold time
    }

    gpio_put(LED_PIN, 0); // LED off
}
