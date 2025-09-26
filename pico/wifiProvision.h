#ifndef WIFIPROVISION_H
#define WIFIPROVISION_H

#include <iostream>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <signal.h>
#include "portalServer.h"

using namespace std;

class WifiProvisioner {
    public:
        WifiProvisioner(string wifiGatewayInfoPath) {
            
            PortalServer server;
            pid_t pid = fork();

            if (pid < 0) {          // error checking fork()
                cerr << "Fork failed." << endl;
                return;

            } else if (pid == 0) {  // child process code
                cout << "Child process started. PID: " << getpid() << endl;
            
                server.startServer();       // run server via child process
                
                _exit(0);                   // call exit() when sigterm is received

            } else {                // parent process code
                cout << "Parent process. Child PID: " << pid << endl;
                
                sleep(30);                  // allow 1 minute for user to provide wifi info

                cout << "Parent sending SIGTERM to child." << endl;
                server.closeServer();
                kill(pid, SIGTERM);         // Send SIGTERM

                int status;
                waitpid(pid, &status, 0);   // Wait for child to terminate
                cout << "Child process terminated with status: " << status << endl;
            }

            
        };

    private:

};

#endif // WIFIPROVISION_H