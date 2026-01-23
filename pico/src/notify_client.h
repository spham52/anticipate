#ifndef NOTIFY_CLIENT_H
#define NOTIFY_CLIENT_H

#define PORT 80
#define BUF_SIZE 2048
#define TEST_ITERATIONS 5

#include "wifi_provisioner.h"   // for err_t

typedef struct {
    struct tcp_pcb *tcp_pcb;
    ip_addr_t remote_addr;
    uint8_t buffer[BUF_SIZE];
    int buffer_len;
    int sent_len;
    int recv_len;
    bool complete;
    bool connected;

} notify_client_t;

// Initializing the tcp structs.
//
// Heap allocates a new notify client instance.
// Error if new instance failed.
notify_client_t* notify_client_init();

// Handles notification posting logic.
//
// Invokes the notify client start function to begin, which connects to
// the server. Upon connection, the tcp_connected callback posts to the server.
// The polling of the wifi stack continues until server ACK response 
// acknowledges all bytes sent.
err_t notify_client_post_notification(notify_client_t *notify_client);

// Starting of the exchange with the anticipate server.
//
// Sets up the tcp pcb and connects to the server ip and port. This in turn
// will invoke the notify_client_connected callback upon success, which posts
// the a notifcation to the server.
static err_t notify_client_start(notify_client_t *notify_client);

// Callback function for successful connection to server.
//
// Upon succcessful tcp connection to the server, this callback function sends 
// the post notification to the server.
static err_t notify_client_connected(void *arg, struct tcp_pcb *tpcb, err_t err);

// Closing of the notify client connection.
//
// Deallocates tcp callback functions and closes the tcp connection.
static err_t notify_client_close(void *arg);

// Callback function for sent data.
//
// This function is used to verify that data has been sent successfully from the 
// TCP stack to the server. The "len" parameter indicates how many bytes the
// servers TCP layer has acknowledged as received. This is compared with how many
// were sent to decide if notify_client can be considered complete.
static err_t notify_client_sent(void *arg, struct tcp_pcb *tpcb, u16_t len);



#endif // NOTIFY_CLIENT_H