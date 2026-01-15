#ifndef PICO_FS_H
#define PICO_FS_H

#include "wifi_provisioner.h"

// Mount pico file system
//
// Initially assumes flash fs is pre-formatted.
// Recalls mount with formatting enabled if first call fails.
// returns 0 on success, and fs read err enum
int pico_fs_init();

// Demount pico file system
//
// Demount call with layered value checking
int pico_fs_deinit();

// Read pico files
//
// Declares
// Returns bytes read.
int pico_fs_read_file(const char* file_name, char* buffer, int buff_len);

int pico_fs_write_file(const char* file_name, char* buffer, int buf_len);

#endif // PICO_FS_H