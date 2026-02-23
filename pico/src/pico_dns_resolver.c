#include "lwip/dns.h"
#include "lwip/ip_addr.h"
#include "pico/stdlib.h"
#include "pico/cyw43_arch.h"

#include "wl_log.h"

static void dns_found_cb(const char *name, const ip_addr_t *ipaddr, void *callback_arg) {
    ip_addr_t *passed_ip = (ip_addr_t *)callback_arg;

    if (ipaddr && passed_ip) {
        WL_LOGI("DNS", "Hostname '%s' resolved to IP: %s", name, ipaddr_ntoa(ipaddr));
        *passed_ip = *ipaddr;
    } else {
        WL_LOGE("DNS", "Failed to resolve hostname '%s'", name);
    }
}

err_t pico_resolve_hostname(const char *hostname, ip_addr_t *addr) {

    cyw43_arch_lwip_begin();
    err_t err = dns_gethostbyname(hostname, addr, dns_found_cb, addr);
    cyw43_arch_lwip_end();

    if (err == ERR_OK) {
        WL_LOGI("DNS", "Hostname '%s' resolved immediately", hostname);
        return ERR_OK;
    }
    else if (err == ERR_INPROGRESS) {
        WL_LOGI("DNS", "Hostname resolution in progress for %s...", hostname);
    }
    else {
         WL_LOGE("DNS", "dns_gethostbyname('%s') failed: %d (%s)",
                hostname, (int)err, lwip_strerr(err));
        return ERR_VAL;
    }

    return ERR_OK;
}