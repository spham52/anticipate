#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "pico_fs.h"

#include "wifi_provisioner.h"
#include "notify_client.h"

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

        // restart pico to connect with newly obtained credentials
    }
    else {
        printf("[main] attempting wifi connection with credentials\n");
        printf("    ssid: \"%s\"\n", wifi_credentials.ssid);
        fflush(stdout);
        printf("    password: \"%s\"\n", wifi_credentials.password);

        cyw43_arch_enable_sta_mode();
        
        if (cyw43_arch_wifi_connect_timeout_ms(wifi_credentials.ssid, wifi_credentials.password, CYW43_AUTH_WPA2_AES_PSK, 30000)) {
            printf("[main] failed to connect.\n");
            exit(1);
        }

        printf("[main] connected to WiFi successfully\n");
    }

    // initialize notification client
    notify_client_t *notify_client = notify_client_init();
    if (notify_client == NULL) {
        printf("[main] notify_client_init failed\n");
        return -1;
    }

    // post notification to server (ASSUME SENSOR LOGIC HERE)
    err_t error = notify_client_post_notification(notify_client);
    if (error != ERR_OK) {
        printf("[main] notification post failed with error code: %d\n", error);
        return -1;
    }

    printf("[main] notification posted successfully\n");

    return 0;
}