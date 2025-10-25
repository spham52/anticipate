#include "pico/stdlib.h"

// Pico W LED lives on the Wi-Fi chip:
#ifdef CYW43_WL_GPIO_LED_PIN
#include "pico/cyw43_arch.h"
#endif

#define LED_DELAY_MS 250  // 250 on + 250 off = 2 blinks per second

// Perform initialisation
int pico_led_init(void) {
#if defined(PICO_DEFAULT_LED_PIN)
    gpio_init(PICO_DEFAULT_LED_PIN);
    gpio_set_dir(PICO_DEFAULT_LED_PIN, GPIO_OUT);
    return PICO_OK;
#elif defined(CYW43_WL_GPIO_LED_PIN)
    // Init the CYW43 driver (no Wi-Fi stack in this arch)
    return cyw43_arch_init();  // returns 0 (PICO_OK) on success
#else
    return -1;
#endif
}

static inline void pico_set_led(bool on) {
#if defined(PICO_DEFAULT_LED_PIN)
    gpio_put(PICO_DEFAULT_LED_PIN, on);
#elif defined(CYW43_WL_GPIO_LED_PIN)
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, on);
#endif
}

int main() {
    stdio_init_all();  // optional, safe

    int rc = pico_led_init();
    if (rc != PICO_OK) {
        // If init failed, DO NOT panic-blink; just idle visibly slow so it’s obvious
        while (true) {
            sleep_ms(1000);
        }
    }

    while (true) {
        pico_set_led(true);
        sleep_ms(LED_DELAY_MS);
        pico_set_led(false);
        sleep_ms(LED_DELAY_MS);
    }
}
