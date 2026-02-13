<div align="center">
  <h1>anticipate</h1>
</div>

Anticipate is an End-To-End IoT reminder service made for the forgetful. Powered by a Spring-Boot backend, 
Anticipate delivers deployment-ready, modular software, for linking any end-user device to the Pi Pico firmware that awaits your move.
By employing an "as-code" doctrine, this project promotes repteatable builds and a highly automatable deployment process.

<h2>Server End</h2>

The backbone of Anticipate is run by a Spring-Boot server.

![Anticipate demo](misc/gif.gif)

![](https://raw.githubusercontent.com/spham52/anticipate/main/misc/gif.gif)

<div align="center">
    <img src="misc/gif" alt="Gif of the login and dashboard of Anticipate" width="250"/>
</div>

<h2>Pico End</h2>

With a Raspberry Pi Pico W board in combination with a motion sensor, Anticipate sends users notifications to their device 
from anywhere over the internet when motion is sensed. To tackle the provisioning of wifi, the firmware in this project integrates 
a DHCP server with a web HTTP server in order to create a completely microcontroller hosted, captive web portal.

Simply provision the device to home Wi-Fi, register it in the web app, and Anticipate anything.

<div align="center">
    <img src="misc/pico_pi" alt="Photo of the Pico Pi W. Be sure to replace with finished product later!" width="250"/>
</div>

