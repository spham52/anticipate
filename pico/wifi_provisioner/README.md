<h1>Wifi Provisioning for the Raspberry Pico Pi W</h1>

<h2>Usage Note</h2>

This is an educational project for me to learn the essentials of embedded C. Please refrain from
using this code for anything other than hobby projects.

This has only been tested on the Pico Pi W, not the Pico Pi W 2. Although they use the same wifi chip so no reason it wouldn't work.

<h2>How To Build</h2>

To build this project, you must have the pico-sdk installed properly.

IF: You're building from a raspberry Pi use the following setup script:
https://raw.githubusercontent.com/raspberrypi/pico-setup/master/pico_setup.sh

IF: You're building from a linux PC see page 32 "Manually Configure your Environment":
https://pip-assets.raspberrypi.com/categories/610-raspberry-pi-pico/documents/RP-008276-DS-1-getting-started-with-pico.pdf?disposition=inline

<h2>Psuedo Code</h2>

WIFI PROVISIONING:

    initialize littleFS instance
    read wifi credentials file
  
    If no wifi credentials saved to file, begin provisioning {
    
      start access point
      start dhcp server
      
      when user connects {
        begin captive http portal 
      }

      write provisioned credentials to pico board
      restart board
    }

CAPTIVE HTTP PORTAL:

    listen for localhost get request
    once recieved, handle client {
      send portal page html
      hold for reply
      POST-REDIRECT-GET transaction with data
    }