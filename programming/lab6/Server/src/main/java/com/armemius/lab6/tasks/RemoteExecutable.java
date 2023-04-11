package com.armemius.lab6.tasks;

import com.armemius.lab6.io.OutputHandler;

public interface RemoteExecutable {

    void validate(int id, Object[] data) throws IllegalArgumentException;
    void execute(int id, Object[] data, OutputHandler outputHandler);
}
