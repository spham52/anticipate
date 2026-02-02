#ifndef BUTTON_HAL_H
#define BUTTON_HAL_H

#define BUTTON_PIN 15 // GPIO pin connected to button

// Inits the button hardware abstraction layer
//
// Configures GPIO pins for button input and onboard LED output.
void button_hal_init();

// Polls the button for press detection
//
// Returns true if button is pressed, false otherwise.
bool button_hal_pressed();


#endif // BUTTON_HAL_H