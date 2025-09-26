#ifndef PORTALSERVER_H
#define PORTALSERVER_H

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <cstring>
#include <cstdlib>
#include <arpa/inet.h>

using namespace std;

class PortalServer {
    public:

        PortalServer() {
            cout << "Initializing http server" << endl;
         
            // create TCP socket
            if ((tcp_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
                cerr << "Socket creation failed." << endl;
                return;
            }
            cout << "TCP Socket created" << endl;

            // Set SO_REUSEADDR
            int opt = 1;
            if (setsockopt(tcp_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) == -1) {
                cerr << "setsockopt(SO_REUSEADDR) failed." << endl;
                close(tcp_socket);
                return;
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

            initialized = true;
        }

        int startServer() {

            if (!initialized) {
                cerr << "Server Sartup Failed - Server not properly initialized" << endl;
                return -1;
            }

            cout << "Starting HTTP Server" << endl;

            if (listen(tcp_socket, 5) == -1) {
                cout << "Listen failed" << endl;
                return -1;
            }
        
            cout << "Server listening on port 8080" << endl;

            while (true) {
                struct sockaddr_in client_addr;
                socklen_t client_len = sizeof(client_addr);

                if (int client_socket = accept(tcp_socket, (struct sockaddr*)&client_addr, &client_len) == -1) {
                    cerr << "Accept failed" << endl;
                    continue;
                }
                else{
                    cout << "Client connected" << endl;
                    handleClient(client_socket);
                }

                // handle client connection
                //const char *http_response = "HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\nHello, World!";
                //send(client_socket, http_response, strlen(http_response), 0);
                //close(client_socket);
            }

            return 0;
        }

        void closeServer() {
            cout << "Closing HTTP Server" << endl;
            
            if (close(tcp_socket) == -1) {
                cerr << "Socket close failed" << endl;
            } 

            cout << "Socket closed successfully" << endl;
        }
    

    private:
        bool initialized = false;
        int tcp_socket;

        void handleClient(int client_socket) {
            const char html[] = "HTTP/1.1 200 OK\r\n"
            "Connection: close\r\n"
            "Content-type: text/html\r\n"
            "\r\n"
            "<html>\r\n"
            "<head>\r\n"
            "<title>Hello, world!</title>\r\n"
            "</head>\r\n"
            "<body>\r\n"
            "<h1>Hello, world!</h1>\r\n"
            "<form action=\"/submit_form.php\" method=\"post\">"
            "<label for=\"username\">Username:</label><br>"
            "<input type=\"text\" id=\"username\" name=\"username\" placeholder=\"Enter your username\"><br><br>"
            "<label for=\"password\">Password:</label><br>"
            "<input type=\"password\" id=\"password\" name=\"password\" required><br><br>"
            "<input type=\"submit\" value=\"Login\">"
            "</form>"
            "</body>\r\n"
            "</html>\r\n\r\n";

            send(client_socket, html, strlen(html), 0);
            close(client_socket);
        }

};

#endif // PORTALSERVER_H