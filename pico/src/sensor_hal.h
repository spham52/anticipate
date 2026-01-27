#ifndef SENSOR_HAL_H
#define SENSOR_HAL_H

#define PIR_PIN 21 // GPIO pin connected to PIR

// Inits the sensor hardware abstraction layer
//
// Configures GPIO pins for PIR sensor input and onboard LED output.
// Further more registers the provided callback to be called on motion detection.
void sensor_hal_init();

void sensor_hal_poll();


#endif // SENSOR_HAL_H