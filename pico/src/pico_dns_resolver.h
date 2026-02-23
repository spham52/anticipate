#include "lwip/ip_addr.h"

// Callback function
//
// Called when hostname is resolved by lwip/DNS. 
static void dns_found_cb(const char *name, const ip_addr_t *ipaddr, void *callback_arg);

// Resolves a hostname to an IP address.
//
// This function initiates the hostname resolution process using lwip's DNS API.
err_t pico_resolve_hostname(const char *hostname, ip_addr_t *addr);