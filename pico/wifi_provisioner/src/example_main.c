#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

#include "wifi_provisioner.h"

int main() {

    pico_prov_err_t err;
    pico_prov_credentials_t wifi_credentials = {0};

    // initialize all necessary systems + wifi credentials
    err = pico_prov_init(&wifi_credentials);
    if (err != PICO_PROV_OK) {
        printf("[main] pico_prov_init returned error code: %d\n", err);
        return err;
    }

    // set case for beginning provisioning
    if (wifi_credentials.ssid[0] == '\0'/* || gpio_rst_btn_pressed()*/) {
        printf("[main] no credentials extracted, begining provisioning\n");
        
        err = pico_prov_begin(&wifi_credentials);
        if (err != PICO_PROV_OK) {
            return err;
        }

        // set case for polling wifi chip (further polls captive portal)
        while(wifi_credentials.ssid_state == 0) {
            cyw43_arch_poll();
            sleep_ms(1);
        }

        // end pico provisioning (stores passed credentials to flash storage)
        err = pico_prov_end(&wifi_credentials);
        if (err != PICO_PROV_OK) {
            printf("[main] pico_prov_end returned error code: %d\n", err);
            return err;
        }
    }
    else {
        printf("[main] attempting wifi connection with credentials\n");
        printf("    ssid: \"%s\"\n    password: \"%s\"\n", wifi_credentials.ssid, wifi_credentials.password);
    }

    return 0;
}