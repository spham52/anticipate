#ifndef SENSOR_HAL_H
#define SENSOR_HAL_H

#define PIR_PIN 28 // GPIO pin connected to PIR
#define LED_PIN 25 // Pico W onboard LED

// Inits the sensor hardware abstraction layer
//
// Configures GPIO pins for PIR sensor input and onboard LED output
void sensor_hal_init();

void sensor_hal_poll();


#endif // SENSOR_HAL_H