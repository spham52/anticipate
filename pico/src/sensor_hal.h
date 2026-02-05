#ifndef SENSOR_HAL_H
#define SENSOR_HAL_H

#define PIR_PIN 21 // GPIO pin connected to PIR

// Counts the iterations that need to pass before the sensor can activate again.
static __uint8_t iterations_before_active;

// Inits the sensor hardware abstraction layer
//
// Configures GPIO pins for PIR sensor input and onboard LED output.
// Also initializes the active state variable and pulls up the PIR sensor pin to prevent floating.
void sensor_hal_init();

// Polls the PIR sensor for motion detection
//
// Returns true if motion is detected, false otherwise.
// This function handles it's own debouncing cooldown, allowing for 
// absolute minimal main loop code and minimal blocking of TCP polling.
//
// The logic if you need to know for some reason:
//      To prevent sticking activation, will return false 10 times for 500ms each time
//      to ensure sensor is ready to be active again (takes ~3s for PIR sensor to reset).
//      This is preffered to sleep_ms(3000) as TCP sockets need frequent polling.
bool sensor_hal_is_active();


#endif // SENSOR_HAL_H