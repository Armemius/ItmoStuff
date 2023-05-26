package com.armemius.lab7.tasks;

import com.armemius.lab7.io.OutputHandler;

public interface RemoteExecutable {

    void validate(int id, Object[] data) throws IllegalArgumentException;
    void execute(int id, Object[] data, OutputHandler outputHandler, String login);
}
