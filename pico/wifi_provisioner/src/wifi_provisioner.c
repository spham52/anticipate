// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms,i ssid_ stdio...
#include "pico/cyw43_arch.h"    // wifi chip library

// project headers
#include "wifi_provisioner.h"
#include "pico_fs.h"            // abstraction of flash file i/o functions
#include "pico_dhcp.h"
#include "pico_captive_portal.h"

// buffer length = 
// max length of SSID (32 chars) + space + max length of password(63) = 96
char credentials_buffer[96] = {0};

portal_server_t *portal_server;

pico_prov_err_t pico_prov_init(pico_prov_credentials_t *wifi_credentials) {

    // indication of initialization via both serial output and led
    printf("\n[pico_prov] intializing\n");
    blink(250);
    blink(250);
    blink(250);

    // mount the file system for credentails extraction
    if (pico_fs_init() < 0){
        return PICO_PROV_ERR_FS_MOUNT;
    }

    // actual reading of credentials file
    if (pico_fs_read_file(CREDENTIALS_PATH, credentials_buffer, 96) < 0) {
        printf("[pico_prov] reading from file failed\n");
        return PICO_PROV_ERR_FS_READ;
    }

    // processing retrieved credentials
    sort_credentials_buffer(wifi_credentials);
    printf("[pico_prov] credentials read from flash storage: \"%s\"\n", credentials_buffer);
    fflush(stdout);

    return PICO_PROV_OK;
}

pico_prov_err_t pico_prov_begin(pico_prov_credentials_t *credentials) {
    
    // ensure reset of credentials buffer
    credentials->ssid[0] = '\0';
    credentials->password[0] = '\0';
    credentials->ssid_state = 0;

    // set parameters for the access point
    const char *ssid = "anticipate_wifi";
    const char *password = "anticipate1234";
    
    // start access point
    cyw43_arch_enable_ap_mode(ssid, password, CYW43_AUTH_WPA2_MIXED_PSK);
    printf("[pico_prov] Wifi Access Point started with SSID: %s\n", ssid);

    // begin listening dhcp server
    pico_dhcp_start();

    // init captive web portal
    portal_server = pico_captive_portal_init();
    if (!portal_server) {
        return PICO_PROV_ERR_PORTAL_INIT;
    }

    // starting of web portal
    if (pico_captive_portal_start(portal_server, credentials) < 0) {
        return PICO_PROV_ERR_PORTAL_START;
    }

    printf("[pico_prov] captive web portal listening on: http://192.168.4.1:80/\n");

    return PICO_PROV_OK;
}

pico_prov_err_t pico_prov_end(pico_prov_credentials_t *wifi_credentials) {

    // end listening servers
    pico_captive_portal_close(portal_server);
    pico_dhcp_stop();
    cyw43_arch_disable_ap_mode();

    // writing to flash storage
    char *final_credentials_buffer = wifi_credentials->ssid;

    // append buffer depending on if a password is included
    if (wifi_credentials->password[0] != '\0') {
        final_credentials_buffer = strcat(wifi_credentials->ssid, " ");
        final_credentials_buffer = strcat(final_credentials_buffer, wifi_credentials->password);
    }
    uint8_t err = pico_fs_write_file(CREDENTIALS_PATH, final_credentials_buffer, strlen(final_credentials_buffer));

    if (err == -1) {
        printf("[pico_prov] failed to write to file\n");
        return PICO_PROV_ERR_FS_WRITE;
    }

    return PICO_PROV_OK;
}

void sort_credentials_buffer(pico_prov_credentials_t *wifi_credentials) {

    // case for empty credentials
    if (credentials_buffer[0] == '\0') {
        wifi_credentials->ssid[0] = '\0';
        wifi_credentials->password[0] = '\0';
        return;
    }

    // externally declare indexes for persistance between loops
    int i = 0;
    int x = 0;

    // SSID extracting
    for (;i < 32; i++) {    // increment index i until space is reached
        
        if (credentials_buffer[i] == ' ' || credentials_buffer[i] == '\0') {
            break;
        }

        wifi_credentials->ssid[i] = credentials_buffer[i];
    }

    // terminate SSID and increment index beyond space
    wifi_credentials->ssid[i] = '\0';
    
    // handle no password
    if (credentials_buffer[i] == '\0') {
        wifi_credentials->password[0] = '\0';
        return;
    }
    
    i++;

    // password extracting
    for (;x < 64 && (x + i) < 96; x++) {
        
        // assign and access of pswrd from buffer must be offset by i
        if (credentials_buffer[x + i] == '\0') {
            break;
        }

        wifi_credentials->password[x] = credentials_buffer[x + i];
    }

    // terminate password
    wifi_credentials->password[x] = '\0';
}

void blink(int blink_length) {
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, 1);
    sleep_ms(blink_length);
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, 0);
    sleep_ms(blink_length);
}