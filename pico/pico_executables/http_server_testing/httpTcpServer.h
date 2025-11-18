#ifndef HTTPTCPSERVER_H
#define HTTPTCPSERVER_H

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <cstring>
#include <cstdlib>
#include <arpa/inet.h>

using namespace std;

class HttpTcpServer {
    public:

        HttpTcpServer() {
            cout << "Initializing http server" << endl;
         
            // create TCP socket
            if ((tcp_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
                cerr << "Socket creation failed." << endl;
                return;
            }
            else{
                cout << "TCP Socket created" << endl;
            }
         
            // setup server address structure
            struct sockaddr_in server_addr;
            server_addr.sin_family = AF_INET;
            server_addr.sin_port = htons(8080);
            server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");   // setting address as local host

            // bind socket to the specified IP and port
            if (bind(tcp_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1) {
                cerr << "Socket bind failed" << endl;
                return;
            }
            else{
                cout << "Socket bind created" << endl;
            }
        }

        void startServer() {
            cout << "Starting HTTP Server" << endl;

            if (listen(tcp_socket, 5) == -1) {
                cout << "Listen failed" << endl;
                return;
            }
            else{
                cout << "Server listening on port 8080" << endl;
            }

            while (true) {
                struct sockaddr_in client_addr;
                socklen_t client_len = sizeof(client_addr);
                int client_socket = accept(tcp_socket, (struct sockaddr*)&client_addr, &client_len);
                if (client_socket == -1) {
                    cerr << "Accept failed" << endl;
                    continue;
                }
                else{
                    cout << "Client connected" << endl;
                }

                // handle client connection
                const char *http_response = "HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\nHello, World!";
                send(client_socket, http_response, strlen(http_response), 0);
                close(client_socket);
            }
        }

        void closeServer() {
            cout << "Closing HTTP Server" << endl;
            close(tcp_socket);
            exit(0);
        }
    

    private:

    int tcp_socket;

    void logI(const string &message) {
        cout << message << endl;
    }
    
    void logE(const string &errorMessage){
        logI("ERROR: " + errorMessage);
        exit(1);
    }

};

#endif // HTTPTCPSERVER_H