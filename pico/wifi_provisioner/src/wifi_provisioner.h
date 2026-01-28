#ifndef WIFI_PROVISIONER_H
#define WIFI_PROVISIONER_H

#pragma once

#define CREDENTIALS_PATH "wifi_credentials.txt"

#include <stdbool.h>

typedef enum {

    PICO_PROV_OK                = 0,

    PICO_PROV_ERR               = -1,
    PICO_PROV_ERR_INIT          = -2,
    PICO_PROV_ERR_FS_MOUNT      = -3,
    PICO_PROV_ERR_FS_READ       = -4,
    PICO_PROV_ERR_FS_UMOUNT     = -5,
    PICO_PROV_ERR_PORTAL_INIT   = -6,
    PICO_PROV_ERR_PORTAL_START  = -7,
    PICO_PROV_ERR_PORTAL_CONN   = -8,
    PICO_PROV_ERR_FS_WRITE      = -9
    
} pico_prov_err_t;

typedef struct {
    char ssid[33];          // up to 32 + terminating null
    char password[64];      // up to 63 + terminating null
    int ssid_state;
} pico_prov_credentials_t;

// Initialize all pico IO + credentials
//
// Initializing of stdio, the cyw34 wifi chip and the mounting of
// a pico flash file system is carried out. If init suceeds, 
// passed reference to credentials struct is used to assign extracted
// credentials from file (will leave password and/or ssid blank if needed).
pico_prov_err_t pico_prov_init(pico_prov_credentials_t *wifi_credentials);

// Begins wifi access point and the wifi login portal
//
// Activates the AP on the cyw34 chip, begins a dhcp server and the tcp http
// captive portal server. This tcp server will keep running, and modify the
// passed credentials object when recieved from the web portal. 
// To use, keep polling with cyw43_arch_poll() until your passed credentials buffer
// has data in it.
pico_prov_err_t pico_prov_begin(pico_prov_credentials_t *credentials);

// Stops all provisioning systems and writes credentials to pico
//
// Stops captive portal, dhcp server and ends pico AP. Finally, writes passed 
// credentials to flash storage.
pico_prov_err_t pico_prov_end(pico_prov_credentials_t *credentials);

// Connects to wifi with passed credentials
//
// Uses the cyw34 station mode to connect to wifi with the passed ssid
// and password. Returns error code on failure.
pico_prov_err_t pico_prov_connect_wifi(const char *ssid, const char *password);

// Sorts the credentails retrieved from the pico fs system
//
// Where the format of the extracted credentials is a single char array,
// we sort the credentails buffer into the passed reference for the 
// credentials struct. SSID and password are space delimited when stored.
// This function accounts for missing credentials and case where password
// is missing but SSID isn't. The credentails buffer for each will be left
// char[0] = '\0' where necessary.
void sort_credentials_buffer(pico_prov_credentials_t *wifi_credentials);

// Blinks the LED
//
// Using the cyw34 GPIO connection to led, execute a blink for int ms.
void blink(int);

#endif // WIFI_PROVISIONER_H