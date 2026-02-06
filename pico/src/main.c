// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "pico_fs.h"
#include "hardware/watchdog.h"  // for device reset

// project headers
#include "wifi_provisioner.h"
#include "notify_client.h"
#include "button_hal.h"
#include "sensor_hal.h"

// config define for Wikilift's logging library
#define RP2040
#include "wl_log.h"

int main() {

    err_t err;
    pico_prov_err_t provision_err;
    pico_prov_credentials_t wifi_credentials = {0};
    
    // initialize all necessary subsystems
    if (stdio_init_all() < 0 || cyw43_arch_init() < 0) {
        return -1;
    }
    sleep_ms(2000);
    wl_log_init();

    provision_err = pico_prov_init(&wifi_credentials);
    if (provision_err != PICO_PROV_OK) {
        WL_LOGE("main", "pico_prov_init failed with error code: %d", provision_err);
        return -1;
    }
    
    // set case for beginning provisioning
    button_hal_init();
    if (wifi_credentials.ssid[0] == '\0' || button_hal_pressed()) {
        WL_LOGI("main", "empty credentials or prov button pressed, beginning provisioning");

        provision_err = pico_prov_begin(&wifi_credentials);
        if (provision_err != PICO_PROV_OK) {
            return provision_err;
        }

        // poll wifi chip (further polls captive portal)
        while(wifi_credentials.ssid_state == 0) {
            cyw43_arch_poll();
            sleep_ms(1);
        }

        // end pico provisioning (stores passed credentials to flash storage)
        provision_err = pico_prov_end(&wifi_credentials);
        if (provision_err != PICO_PROV_OK) {
            WL_LOGE("main", "pico_prov_end failed with error code: %d", provision_err);
            return provision_err;
        }

        // reboot pico to connect with newly obtained credentials
        watchdog_enable(1, 1); 
        while(1); 
    }

    if (notify_client_connect_wifi(wifi_credentials.ssid, wifi_credentials.password) != ERR_OK) {
        WL_LOGE("main", "failed to connect to WiFi");
        return -1;
    }

    // initialize notification client
    notify_client_t *notify_client = notify_client_init();
    if (notify_client == NULL) {
        WL_LOGE("main", "notify_client_init failed");
        return -1;
    }

    // Initialize sensor HAL
    sensor_hal_init();
    WL_LOGI("main", "sensor HAL initialized, listening for motion...");

    // run sensor HAL
    while (1) {
        if (sensor_hal_is_active()) {

            WL_LOGI("main", "motion detected, posting notification");
            
            err = notify_client_post_notification(notify_client);
            if (err != ERR_OK) {
                WL_LOGE("main", "notification post failed with error code: %d", err);
            }
            else {
                WL_LOGI("main", "notification post successful");
            }
            
            WL_LOGI("main", "sensor listening");
        }
    }

    return 0;
}