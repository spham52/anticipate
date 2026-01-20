#ifndef PICO_CAPTIVE_PORTAL_H
#define PICO_CAPTIVE_PORTAL_H

#define PORT 80
#define BUF_SIZE 2048

#include "wifi_provisioner.h"   // for err_t

typedef struct {
    struct tcp_pcb *tcp_pcb;
    ip_addr_t remote_addr;
    uint8_t buffer[BUF_SIZE];
    int buffer_len;
    int sent_len;
    bool complete;
    int run_count;
    bool connected;

} notify_client_t;

static int post_notification();

// Initializing the tcp structs.
//
// Heap allocates a new notify client instance.
// Error if new instance failed.
notify_client_t* pico_notify_client_init();

// Starting of the exchange with the anticipate server.
//
// Sends an initial post request to the anticipate server to notify it
// and waits for response to confirm notification was successful.
static err_t pico_notify_client_start(void *arg);

static err_t notify_client_connected(void *arg, struct tcp_pcb *tpcb, err_t err);

/*
// Call back function for accepted requests.
//
// Used by the lwip stack as a call back function when a connection is accepted 
// on a listening TCP socket.
err_t pico_captive_portal_accept(void *arg, struct tcp_pcb *client_pcb, err_t err);

// Sends data to an accepted connection/
//
// Arg set as client pcb via tcp_arg(). This will deliver a http response with html
// and css for the captive web portal.
err_t pico_captive_portal_send_data(void *arg, struct tcp_pcb *client_pcb);

// Callback function for sent data.
//
// When the tcp_send_data() function is done, this function is called
// as a callback to verify that data was sent successfully.
err_t pico_captive_portal_sent(void *arg, struct tcp_pcb *tpcb, u16_t len);

// Callback function for recieved data.
//
// Callback call when the client pcb sends an http response to the server pcb.
err_t pico_captive_portal_recv(void *arg, struct tcp_pcb *tpcb, struct pbuf *p, err_t err);

// Extract wifi details provided in http response
//
// Given an http response, extract the wifi credentials and store them in a credentials struct.
// Employs the get value funct with using relevant entry keys.
static void get_wifi_login(void *arg);

// Gets value of a field in a buffer given a key.
//
// Search for the key within a buffer. When the index of the key is found,
// this function skips past the index of the last character and equals sign
// and copies data to the provided output buffer. It stops copying data when
// met with a terminating null or an ampersand.
static void get_value(char *in_buffer, char *key, char *out_buffer);

// Checks if a passed http request has wifi credentials passed within it
//
// Using strstr for "wifi=" and "password=", this function will return 1 if they
// are both present and 0 if they are not.
static uint8_t has_ssid(char *http_request);

// Checks if a passed http request has POST it
//
// Using strstr, returns false/0 on NULL return and true/1 otherwise
static uint8_t is_post(char *http_request);

// Checks if a passed http request has GET it
//
// Using strstr, returns false/0 on NULL return and true/1 otherwise
static uint8_t is_get(char *http_request);

// Closes all pcb connections
//
// Sets all tcp callbacks to null and attemps to call tcp_close().
// Calls tcp_abort() on fail and returns abrt error code in this case.
// Returns ERR_OK otherwise. Also points the global credentials pointer
// back to null.
err_t pico_captive_portal_close();
*/

#endif // PICO_CAPTIVE_PORTAL_H