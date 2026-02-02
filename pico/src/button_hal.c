// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

// project headers
#include "button_hal.h"

void button_hal_init() {

    // initialize button pin
    gpio_init(BUTTON_PIN);
    gpio_set_dir(BUTTON_PIN, GPIO_IN);

    // set pull-up resistor on button pin
    gpio_pull_up(BUTTON_PIN);
}

bool button_hal_pressed() {

    for (int i = 0; i < 10; i++) {
        if (gpio_get(BUTTON_PIN)) {    // returns 1 if not pressed, 0 if pressed
            return false;
        }
        sleep_ms(2);
    }

    return true;
}