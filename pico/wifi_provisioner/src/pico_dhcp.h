#ifndef PICO_DHCP_H
#define PICO_DHCP_H

// Starts a listening dhcp server
//
// Initializes a net interface struct with the typical pico AP
// network address and subnet mask. In this case, we provide the data from
// the pico wifi chip (given by cyw43_state.netif).
// details on the dhcp functions:
// https://github.com/raspberrypi/pico-examples/blob/master/pico_w/wifi/access_point/dhcpserver/dhcpserver.h
void pico_dhcp_start();

// Stops a listening dhcp server
//
// Using the global dhcp server struct address, stops and deinints the data in struct.
int pico_dhcp_stop();

#endif // PICO_DHCP_H