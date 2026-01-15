#include <stdio.h>
#include "pico/stdlib.h"        // sleep_ms, stdio...
#include "dhcpserver.h"         // pico DHCP server functionality (targeteted in CMakeLists.txt)
#include "lwip/ip4_addr.h"      // for ip4_addr_t
#include "pico/cyw43_arch.h"    // wifi chip library

#include "pico_dhcp.h"

dhcp_server_t dhcp_server;

void pico_dhcp_start() {

    // initialize gateway and subnet address
    ip4_addr_t gateway_addr;
    ip4_addr_t sub_mask;

    // gateway address reflected by ip 1
    IP4_ADDR(&gateway_addr, 192,168,4,1);
    IP4_ADDR(&sub_mask, 255,255,255,0);

    // begin arch
    cyw43_arch_lwip_begin();

    // initialize network interface for dhcp access point
    struct netif *ap_interface = &cyw43_state.netif[CYW43_ITF_AP];
    netif_set_addr(ap_interface, &gateway_addr, &sub_mask, &gateway_addr);
    netif_set_default(ap_interface);
    netif_set_up(ap_interface);

    // initialize the dhcp server
    dhcp_server_init(&dhcp_server, &gateway_addr, &sub_mask);

    // end arch
    cyw43_arch_lwip_end();

    printf("[pico_dhcp] initialized\n");
}

int pico_dhcp_stop() {

    dhcp_server_deinit(&dhcp_server);

    return 0;
}