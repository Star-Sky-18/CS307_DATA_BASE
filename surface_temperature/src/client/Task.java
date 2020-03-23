package client;

import service.rmiserver.FileService;

import java.io.Serializable;

public interface Task<T> extends Serializable {
    public T run(FileService fileService);
}
