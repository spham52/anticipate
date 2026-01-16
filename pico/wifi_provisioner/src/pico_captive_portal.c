// library headers
#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "lwip/pbuf.h"
#include "lwip/tcp.h"

// project headers
#include "pico_captive_portal.h"
#include "wifi_provisioner.h"   // for credentials struct

// http header defintions
static const char *HEADER_GET =
    "HTTP/1.1 200 OK\r\n"
    "Content-Type: text/html\r\n"
    "Content-Length: %d\r\n"
    "Connection: close\r\n"
    "\r\n";

static const char *HEADER_REDIRECT =
    "HTTP/1.1 303 See Other\r\n"
    "Location: /\r\n"
    "Cache-Control: no-store\r\n"
    "Content-Length: 0\r\n"
    "\r\n";

// credentials status definitions
static const char *PORTAL_STATUS_SSID =
    "<div id=\"status\">Saved SSID (no password)</div>";

static const char *PORTAL_STATUS_SSIDPWSD =
    "<div id=\"status\">Saved SSID and password</div>";

static const char *PORTAL_STATUS_NONE = 
    "";

// page body definition
const char *PORTAL_PAGE_BODY =
    "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Login</title>"
    "<style>"
    "body{margin:0;font-family:Arial;background:#e9eef3;display:flex;justify-content:center;align-items:center;height:100vh;}"
    ".box{background:#fff;padding:30px;border-radius:10px;box-shadow:0 4px 10px rgba(0,0,0,.1);width:280px;}"
    ".box h2{text-align:center;margin:0 0 20px;color:#333;font-size:20px;}"
    ".full{width:100%;box-sizing:border-box;}"
    ".box input{padding:10px;margin:8px 0;border:1px solid #ccc;border-radius:6px;font-size:14px;}"
    ".box button{padding:10px;border:none;border-radius:6px;background:#007bff;color:#fff;font-size:15px;font-weight:bold;cursor:pointer;}"
    ".box button:hover{background:#0056b3;}"
    "#status{margin-top:10px;text-align:center;font-size:13px;color:#c00;}"
    "</style></head>"
    "<body><div class=\"box\">"
    "<h2>Anticipate - Wi-Fi Login</h2>"
    "<form method=\"post\">"
    "<input class=\"full\" type=\"text\" name=\"wifi\" placeholder=\"Enter SSID\">"
    "<div style=\"position:relative\">"
    "<input class=\"full\" type=\"password\" name=\"password\" placeholder=\"Enter password\">"
    "<span style=\"position:absolute;right:5px;top:50%;transform:translateY(-50%);font-size:12px;color:#555\">*Optional</span>"
    "</div>"
    "%STATUS%"
    "<button class=\"full\" type=\"submit\">Login</button>"
    "</form>"
    "</div></body></html>";

    pico_prov_credentials_t *wifi_credentials;

portal_server_t* pico_captive_portal_init() {

    // calloc call justified for setup process where time efficiency is low priority
    portal_server_t *captive_server = calloc(1, sizeof(portal_server_t));

    if (!captive_server) {
        return NULL;
    }

    return captive_server;
}

int pico_captive_portal_start(portal_server_t *captive_server, pico_prov_credentials_t *credentials) {

    // declare new pcb instance for both ipv4 and ipv6
    struct tcp_pcb *pcb = tcp_new_ip_type(IPADDR_TYPE_ANY);
    if (!pcb) {
        printf("[pico_captive_portal] error allocating pcb memory\n");
        return -1;
    }

    // bind web port 80 to the pcb struct
    if (tcp_bind(pcb, IP_ANY_TYPE, PORT) < 0) {
        printf("[pico_captive_portal] failed to bind port 80 to socket\n");
        return -1;
    }

    // point server pcb to address of listening tcp_pcb
    captive_server->server_pcb =  tcp_listen_with_backlog(pcb, 1);
    if (!captive_server->server_pcb) {
        printf("[pico_captive_portal] failed to listen on tcp port\n");

        if (pcb) {
            tcp_close(pcb);
        }

        return -1;
    }

    wifi_credentials = credentials;

    printf("[pico_captive_portal] web portal server listening on port 80\n");

    // setting callback function args + call back function
    tcp_arg(captive_server->server_pcb, captive_server);
    tcp_accept(captive_server->server_pcb, pico_captive_portal_accept);

    return 0;
}

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