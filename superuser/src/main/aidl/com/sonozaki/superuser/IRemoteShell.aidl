package com.sonozaki.superuser;
import com.sonozaki.superuser.ShellResult;

interface IRemoteShell {
    void destroy() = 16777114;

    void exit() = 1;

    ShellResult executeNow(String command) = 2;
    long execute(String command) = 3;

    ParcelFileDescriptor processOutput(long processId) = 4;
    ParcelFileDescriptor processError(long processId) = 5;
    ParcelFileDescriptor processInput(long processId) = 6;

    void destroyProcess(long processId) = 7;
}