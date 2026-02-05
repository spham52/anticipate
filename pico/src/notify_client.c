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
ip_addr_t server_ip = IPADDR4_INIT_BYTES(192, 168, 1, 118);

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

    if (!notify_client) {
        printf("[notify client] NULL client struct passed to post notification\n");
        return ERR_MEM;
    }

    // connected flag must be handled by tcp callbacks
    if (notify_client->connected) {
        printf("[notify_client] client already connected, cannot post notification\n");
        return ERR_ISCONN;
    }

    notify_client->complete = false;

    // sending of post request called by connected callback
    err_t err = notify_client_start(notify_client);
    if (err != ERR_OK) {
        printf("[notify_client] notify_client_start failed %d\n", err);
        return err;
    }

    while(notify_client->complete == false) {
        cyw43_arch_poll();
    }

    if (!notify_client->connected) {
        printf("[notify_client] notify_client_post_notification failed to connect\n");
        return ERR_CONN;
    }
    
    sleep_ms(1000); // ensure ACK is received from server before closing
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
    tcp_sent(notify_client->tcp_pcb, notify_client_sent);
    tcp_err(notify_client->tcp_pcb, notify_client_err);

    // set sent_len for tracking ACK server response
    notify_client->buffer_len = 0;
    notify_client->sent_len = strlen(POST_REQUEST);
    notify_client->recv_len = 0;

    cyw43_arch_lwip_begin();
    err_t err = tcp_connect(notify_client->tcp_pcb, &notify_client->remote_addr, TCP_PORT, notify_client_connected);
    cyw43_arch_lwip_end();

    return err;
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
    state->complete = true;

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
    state->connected = false;
    printf("[notify_client] Notification client closed successfully\n");

    return err;
}

static err_t notify_client_sent(void *arg, struct tcp_pcb *tpcb, u16_t len) {
    
    notify_client_t *state = (notify_client_t*)arg;
    printf("[notify_client] notify_client sent %u bytes\n", len);
    state->recv_len += len;

    if (len >= state->sent_len) {
        state->complete = true;
        printf("[notify_client] server acknowledges all bytes sent\n");
    }

    return ERR_OK;
}

static void notify_client_err(void *arg, err_t err) {

    notify_client_t *state = (notify_client_t*)arg;

    if (err == ERR_ABRT) {
        printf("[notify_client] connection to server failed: %d\n", err);
    }
    else {
        printf("[notify_client] lwip/tcp error code: %d\n", err);
    }
    
    state->complete = true;
    notify_client_close(arg);
}