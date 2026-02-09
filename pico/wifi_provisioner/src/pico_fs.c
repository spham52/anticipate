#include "pico_fs.h"
#include "pico_hal.h"            // pico file system lib
#include "wl_log.h"

int pico_fs_init() {

    // attempt initial mount w/o formatting - expect mount error on first startup (format req.)
    if (pico_mount(false) < 0) {
        WL_LOGW("pico_fs", "failed to mount file system - commencing formatting...");

        
        // reattempt with formatting on first fail
        if (pico_mount(true) < 0) {
            WL_LOGE("pico_fs", "failed to mount file system even after formatting");
            return -1;
        }
    }

    WL_LOGI("pico_fs", "file system mounted successfully");

    return 0;
}

int pico_fs_deinit() {

    if (pico_unmount() < 0) {
        return -1;
    }

    WL_LOGI("pico_fs", "unmounted successfully");

    return 0;
}

int pico_fs_read_file(const char* file_name, char* buffer, int buff_len) {

    // to handle first time startups, set flag create if not exists
    int fp = pico_open(file_name, LFS_O_RDWR | LFS_O_CREAT);
    
    if (fp < 0) {
        WL_LOGE("pico_fs", "error opening file");
        return -1;
    }

    // read to buffer (-1 buff len to preserve terminating null)
    int bytes_read = pico_read(fp, buffer, buff_len-1);

    if (bytes_read < 0) {
        return -1;
    }

    buffer[bytes_read] = '\0';

    if (pico_close(fp) < 0) {
        WL_LOGE("pico_fs", "error closing file");
        return -1;
    }

    return 0;
}

int pico_fs_write_file(const char* file_name, char* buffer, int buf_len) {

    // truncate file data
    int fp = pico_open(file_name, LFS_O_RDWR | LFS_O_CREAT | LFS_O_TRUNC);
    
    if (fp < 0) {
        WL_LOGE("pico_fs", "error opening file");
        return -1;
    }

    WL_LOGI("pico_fs", "file opened successfully");
    
    // write from buffer
    int bytes_written = pico_write(fp, buffer, strnlen(buffer, buf_len));

    if (bytes_written < 0) {
        WL_LOGE("pico_fs", "error writing to file");
        return -1;
    }

    WL_LOGI("pico_fs", "%d bytes written to file", bytes_written);

    if (pico_close(fp) < 0) {
        WL_LOGE("pico_fs", "error closing file");
        return -1;
    }

    WL_LOGI("pico_fs", "file closed successfully");

    return 0;
}