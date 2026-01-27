#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "pico_fs.h"

#include "wifi_provisioner.h"
#include "notify_client.h"
#include "sensor_hal.h"

int connect_wifi(const char *ssid, const char *password);

int main() {

    err_t err;
    pico_prov_err_t provision_err;
    pico_prov_credentials_t wifi_credentials = {0};
    
    // initialize all necessary systems + wifi credentials
    provision_err = pico_prov_init(&wifi_credentials);
    if (provision_err != PICO_PROV_OK) {
        printf("[main] pico_prov_init returned error code: %d\n", provision_err);
        return provision_err;
    }

    // set case for beginning provisioning
    if (wifi_credentials.ssid[0] == '\0'/* || gpio_rst_btn_pressed()*/) {
        printf("[main] no credentials extracted, begining provisioning\n");
        
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
            printf("[main] pico_prov_end returned error code: %d\n", provision_err);
            return provision_err;
        }

        // restart pico to connect with newly obtained credentials
    }
    
    /*
    if (connect_wifi(wifi_credentials.ssid, wifi_credentials.password) != 0) {
        printf("[main] failed to connect to WiFi\n");
        return -1;
    }

    // initialize notification client
    notify_client_t *notify_client = notify_client_init();
    if (notify_client == NULL) {
        printf("[main] notify_client_init failed\n");
        return -1;
    }

    // Initialize sensor HAL
    sensor_hal_init();

    printf("[main] sensor listening\n");

    // run sensor HAL
    while (1) {
        sensor_hal_poll();
    }


    // post notification to server (ASSUME SENSOR LOGIC HERE)
    err = notify_client_post_notification(notify_client);
    if (err != ERR_OK) {
        printf("[main] notification post failed with error code: %d\n", err);
        return -1;
    }

    printf("[main] notification posted successfully\n");
    */
    return 0;
}

int connect_wifi(const char *ssid, const char *password) {
    
    // connect to wifi with obtained credentials
    printf("[main] attempting wifi connection with credentials\n");
    printf("    ssid: \"%s\"\n", ssid);
    fflush(stdout);
    printf("    password: \"%s\"\n", password);

    cyw43_arch_enable_sta_mode();
    
    if (cyw43_arch_wifi_connect_timeout_ms(ssid, password, CYW43_AUTH_WPA2_AES_PSK, 30000)) {
        printf("[main] failed to connect.\n");
        return -1;
    }

    printf("[main] connected to WiFi successfully\n");

    return 0;
}