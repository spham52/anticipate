

---------- STARTUP LOGIC ----------

Pass the file directory path to the wifiProvisioner()

    wifiProvisioner(wifi info path):
        
        fork()

        parent process:
            wait 5 mins
            kill child

        child process runs portal server:

            portalServer():

                listens

                accepts connection

                client handler():

                    sends html form to client

                    waits for form input response

                    






            
        


