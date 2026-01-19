#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "pico/cyw43_arch.h"    // wifi chip library
#include "pico_fs.h"

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

        // restart pico to connect with newly obtained credentials
    }
    else {
        printf("[main] attempting wifi connection with credentials\n");
        printf("    ssid: \"%s\"\n", wifi_credentials.ssid);
        fflush(stdout);
        printf("    password: \"%s\"\n", wifi_credentials.password);

        cyw43_arch_enable_sta_mode();
        
        if (cyw43_arch_wifi_connect_timeout_ms(wifi_credentials.ssid, wifi_credentials.password, CYW43_AUTH_WPA2_AES_PSK, 30000)) {
            printf("failed to connect.\n");
            exit(1);
        }

        printf("Connected to WiFi!\n");

        // ATTEMPT AT SENDING PACKET TO SERVER


        // DECLARE NEW TCP PCB
        struct tcp_pcb *client_pcb = tcp_new_ip_type(IPADDR_TYPE_ANY);
        if (!pcb) {
            printf("[pico_captive_portal] error allocating pcb memory\n");
            return -1;
        }

        // bind web port 80 to the pcb struct
        int port = 80; // TODO: change port as needed (not necessary as client)
        if (tcp_bind(pcb, IP_ANY_TYPE, port) < 0) {
            printf("[pico_captive_portal] failed to bind port 80 to socket\n");
            return -1;
        }

        ip_addr_t server_ip;
        IP4_ADDR(&server_ip, 192, 168, 1, 100); // TODO: change to server's IP address
        u16_t server_port = 8080;               

        err_t err = tcp_connect(client_pcb, &server_ip, server_port, tcp_client_connected);
        if (err != ERR_OK) {
            // Handle error
        }

        // HANDLE TCP EVENTS IN CALLBACK FUNCTIONS AS FOLLOWS:
        /**
         * TCP connected callback: tcp_client_connected()
         * TCP sent callback: tcp_client_sent()
         * TCP recv callback: tcp_client_recv()
         * TCP error callback: tcp_client_err()
         * TCP poll callback: tcp_client_poll()
         * TCP closed callback: tcp_client_close()
         */

        // send data via tcp_write() in tcp_client_connected() callback
        

    return 0;
}