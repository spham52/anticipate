// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "lwip/pbuf.h"
#include "lwip/tcp.h"

// project headers
#include "notify_client.h"

// http header defintions
static const char *POST_REQUEST =
    "POST /notification/notify HTTP/1.1\r\n"
    "Host: 192.168.1.109:8080\r\n"
    "Content-Type: application/json\r\n"
    "Content-Length: 24\r\n"
    "Connection: close\r\n"
    "\r\n"
    "{\n" 
    " \"sensorID\" : 123\n"
    "}\n";

// other definitions
#define TCP_PORT 8080
ip_addr_t server_ip = IPADDR4_INIT_BYTES(192, 168, 1, 119);

notify_client_t* notify_client_init() {

    // calloc call justified for setup process where time efficiency is low priority
    notify_client_t *notify_client = calloc(1, sizeof(notify_client_t));
    if (!notify_client) {
        return NULL;
    }

    notify_client->remote_addr = server_ip;

    return notify_client;
}

err_t notify_client_post_notification(notify_client_t *notify_client) {

    // sending of post request called by connected callback
    err_t err = notify_client_start(notify_client);
    if (err != ERR_OK) {
        printf("[notify_client] notify_client_start failed %d\n", err);
        return err;
    }

    while(1) {
        cyw43_arch_poll();
        sleep_ms(1);
    }

    notify_client_close(notify_client);

    return ERR_OK;
}

static err_t notify_client_start(notify_client_t *notify_client) {

    // connect to server port
    printf("[notify_client] Connecting to %s port %u\n", ip4addr_ntoa(&notify_client->remote_addr), TCP_PORT);

    notify_client->tcp_pcb = tcp_new_ip_type(IP_GET_TYPE(&notify_client->remote_addr));
    if (!notify_client->tcp_pcb) {
        printf("[notify_client] failed to create pcb\n");
        return ERR_MEM;
    }

    tcp_arg(notify_client->tcp_pcb, notify_client);
    /*
    tcp_poll(notify_client->tcp_pcb, tcp_client_poll, POLL_TIME_S * 2);
    tcp_sent(notify_client->tcp_pcb, tcp_client_sent);
    tcp_recv(notify_client->tcp_pcb, tcp_client_recv);
    tcp_err(notify_client->tcp_pcb, tcp_client_err);
    */

    notify_client->buffer_len = 0;

    // cyw43_arch_lwip_begin/end should be used around calls into lwIP to ensure correct locking.
    // You can omit them if you are in a callback from lwIP. Note that when using pico_cyw_arch_poll
    // these calls are a no-op and can be omitted, but it is a good practice to use them in
    // case you switch the cyw43_arch type later.
    cyw43_arch_lwip_begin();
    err_t err = tcp_connect(notify_client->tcp_pcb, &notify_client->remote_addr, TCP_PORT, notify_client_connected);
    cyw43_arch_lwip_end();

    return ERR_OK;
}

static err_t notify_client_connected(void *arg, struct tcp_pcb *tpcb, err_t err) {
    
    notify_client_t *state = (notify_client_t*)arg;
    
    if (err != ERR_OK) {
        printf("[notify_client] connect failed %d\n", err);
        return err;
    }

    state->connected = true;

    // send the POST request
    err = tcp_write(state->tcp_pcb, POST_REQUEST, strlen(POST_REQUEST), TCP_WRITE_FLAG_COPY);
    if (err != ERR_OK) {
        printf("[notify_client] notification post failed %d\n", err);
        return err;
    }

    err = tcp_output(tpcb);
    if (err != ERR_OK) {
        printf("[notify_client] tcp_output failed %d\n", err);
        return err;
    }

    printf("[notify_client] notification post sent successfully\n");
    state->complete = false;

    return ERR_OK;
}

static err_t notify_client_close(void *arg) {

    notify_client_t *state = (notify_client_t*)arg;
    err_t err = ERR_OK;

    // deallocating of tcp callback functions
    if (state->tcp_pcb) {
        tcp_arg(state->tcp_pcb, NULL);
    }

    // actual closing of the tcp connection
    err = tcp_close(state->tcp_pcb);
    if (err != ERR_OK) {
        printf("[notify_client] close failed %d, calling abort\n", err);
        tcp_abort(state->tcp_pcb);
        err = ERR_ABRT;
    }

    state->tcp_pcb = NULL;
    printf("[notify_client] Notification client closed successfully\n");

    return err;
}























/*
err_t pico_captive_portal_accept(void *arg, struct tcp_pcb *client_pcb, err_t err) {

    portal_server_t *captive_server = (portal_server_t*)arg;
    if (err != 0 || client_pcb == NULL) {
        printf("[pico_captive_portal] error accepting connection\n");
        return -1;
    }

    printf("[pico_captive_portal] connection accepted\n");

    captive_server->client_pcb = client_pcb;

    // setting callback functions and args for send and recv of http data
    tcp_arg(client_pcb, captive_server);
    tcp_sent(client_pcb, pico_captive_portal_sent);
    tcp_recv(client_pcb, pico_captive_portal_recv);
    //tcp_poll(client_pcb, tcp_server_poll, POLL_TIME_S * 2);
    //tcp_err(client_pcb, tcp_server_err);

    pico_captive_portal_send_data(arg, captive_server->client_pcb);

    return ERR_OK;
}

err_t pico_captive_portal_send_data(void *arg, struct tcp_pcb *client_pcb) {

    portal_server_t *captive_server = (portal_server_t*)arg;

    // handle varying request types
    if (is_get(captive_server->buffer_recv)) {

        // setting of credentials status (must precede setting of conent-length)
        const char *status;
        if (wifi_credentials->ssid[0] != '\0' && 
                    wifi_credentials->password[0] == '\0') {
            status = PORTAL_STATUS_SSID;
            wifi_credentials->ssid_state = 1;
        }
        else if (wifi_credentials->ssid[0] != '\0' &&
                    wifi_credentials->password[0] != '\0') {
            status = PORTAL_STATUS_SSIDPWSD;
            wifi_credentials->ssid_state = 1;
        }
        else {
            status = PORTAL_STATUS_NONE;
            wifi_credentials->ssid_state = 0;
        }

        // split body where the status placeholder is in two
        const char *status_placeholder = strstr(PORTAL_PAGE_BODY, "%STATUS%");
        if (status_placeholder == NULL) {
            return ERR_VAL;
        }

        size_t body_upper = status_placeholder - PORTAL_PAGE_BODY;
        size_t body_lower = strlen(status_placeholder + strlen("%STATUS%"));

        // setting of content-length
        char header[256];
        int body_len = strlen(PORTAL_PAGE_BODY) - strlen("%STATUS%") + strlen(status);
        int header_len = snprintf(header, sizeof header, HEADER_GET, body_len);

        printf("[pico_captive_portal] writing %d bytes to client\n", (header_len + strlen(status) + body_len));
        cyw43_arch_lwip_check();

        // clear received buffer and sent len
        captive_server->recv_len = 0;
        memset(captive_server->buffer_recv, 0, BUF_SIZE);
        captive_server->sent_len = 0;

        // writing of data to client (header -> upper body -> status -> lowerbody)
        tcp_write(client_pcb, header, header_len, TCP_WRITE_FLAG_COPY);
        tcp_write(client_pcb, PORTAL_PAGE_BODY, body_upper, TCP_WRITE_FLAG_COPY);
        tcp_write(client_pcb, status, strlen(status), TCP_WRITE_FLAG_COPY);
        tcp_write(client_pcb, status_placeholder + strlen("%STATUS%"), body_lower, TCP_WRITE_FLAG_COPY);
    }
    else if (is_post(captive_server->buffer_recv)) {

        const char *header = HEADER_REDIRECT;
        
        printf("[pico_captive_portal] writing %d bytes to client\n", strlen(header));
        cyw43_arch_lwip_check();

        // clear received buffer and sent len
        captive_server->recv_len = 0;
        memset(captive_server->buffer_recv, 0, BUF_SIZE);
        captive_server->sent_len = 0;
        
        tcp_write(client_pcb, header, strlen(header), TCP_WRITE_FLAG_COPY);
    }

    return ERR_OK;
}

err_t pico_captive_portal_sent(void *arg, struct tcp_pcb *tpcb, u16_t len) {

    printf("[pico_captive_portal] successfully sent http frame\n");

    return 0;
}

err_t pico_captive_portal_recv(void *arg, struct tcp_pcb *tpcb, struct pbuf *p, err_t err) {

    portal_server_t *captive_server = (portal_server_t*)arg;
    if (!p) {
        return -1;
    }
    
    cyw43_arch_lwip_check();
    if (p->tot_len > 0) {

        // Receive the buffer
        const uint16_t buffer_left = BUF_SIZE - captive_server->recv_len;
        captive_server->recv_len += pbuf_copy_partial(p, captive_server->buffer_recv + captive_server->recv_len,
                                             p->tot_len > buffer_left ? buffer_left : p->tot_len, 0);
        tcp_recved(tpcb, p->tot_len);

        // terminate string at end of recieved data
        captive_server->buffer_recv[captive_server->recv_len] = '\0';
    }
    pbuf_free(p);

    // process recieved buffer if credentials are present
    if (has_ssid(captive_server->buffer_recv)) {

        // clear credentials prior to further reading
        memset(wifi_credentials, 0, sizeof(pico_prov_credentials_t));

        printf("[pico_captive_portal] recieved bufer:\n%s\n", captive_server->buffer_recv);
        get_wifi_login(arg);
    }

    return pico_captive_portal_send_data(arg, captive_server->client_pcb);
}

static void get_wifi_login(void *arg) {

    portal_server_t *captive_server = (portal_server_t*)arg;

    // extracting wifi ssid
    get_value(captive_server->buffer_recv, "wifi=", wifi_credentials->ssid);
    if (wifi_credentials->ssid[0] == '\0') {
        printf("[pico_captive_portal] no ssid extracted\n");
        return;
    }

    printf("[pico_captive_portal] extracted ssid: \"%s\"\n", wifi_credentials->ssid);

    // extracting wifi password
    get_value(captive_server->buffer_recv, "password=", wifi_credentials->password);
    if (wifi_credentials->ssid == NULL) {
        printf("[pico_captive_portal] no password extracted\n");
        return;
    }

    printf("[pico_captive_portal] extracted password: \"%s\"\n", wifi_credentials->password);
}

static void get_value(char *in_buffer, char *key, char *out_buffer) {

    // initialize pointer to index of key in in_buffer
    char *chr_ptr;
    chr_ptr = strstr(in_buffer, key);
    if (chr_ptr == NULL) {
        printf("[pico_captive_portal] key \"%s\" not found\n", key);
        return;
    }

    // determine which index after key to start reading from
    uint8_t value_index = strlen(key);

    // copy to out_buffer
    uint8_t current_index = 0;
    while (chr_ptr[value_index + current_index] != '\0' &&
           chr_ptr[value_index + current_index] != '&' &&
           chr_ptr[value_index + current_index] != ' ' &&
           chr_ptr[value_index + current_index] != '\n') {

        out_buffer[current_index] = chr_ptr[value_index + current_index];
        current_index++;
    }
    out_buffer[current_index] = '\0';
}

static uint8_t has_ssid(char *http_request) {

    char *chr_ptr, *chr_ptr1;
    chr_ptr = strstr(http_request, "wifi=");

    if (chr_ptr == NULL) {
        return 0;
    }

    return 1;
}

static uint8_t is_post(char *http_request) {

    char *chr_ptr, *chr_ptr1;
    chr_ptr = strstr(http_request, "POST");

    if (chr_ptr == NULL) {
        return 0;
    }

    return 1;
}

static uint8_t is_get(char *http_request) {

    char *chr_ptr, *chr_ptr1;
    chr_ptr = strstr(http_request, "GET");

    if (chr_ptr == NULL) {
        return 0;
    }

    return 1;
}

err_t pico_captive_portal_close(portal_server_t *captive_server) {
    
    // ensuring global credentials pointer is pointing to NULL again
    wifi_credentials = NULL;

    // deallocating of tcp_() callbacks
    if (captive_server->server_pcb) {
        tcp_arg(captive_server->server_pcb, NULL);
        tcp_close(captive_server->server_pcb);
        captive_server->server_pcb = NULL;
    }

    if (captive_server->client_pcb != NULL) {
        tcp_arg(captive_server->client_pcb, NULL);
        tcp_sent(captive_server->client_pcb, NULL);
        tcp_recv(captive_server->client_pcb, NULL);

        captive_server->client_pcb = NULL;

        if (tcp_close(captive_server->client_pcb) != ERR_OK) {
            printf("[pico_captive_portal] Error closing captive portal. Calling abort()\n");
            tcp_abort(captive_server->client_pcb);

            return ERR_ABRT;
        }
    }

    printf("[pico_captive_portal] Captive portal closed successfully\n");
    
    return 0;
}
*/