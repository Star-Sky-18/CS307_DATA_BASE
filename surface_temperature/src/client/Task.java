package client;

import dataCollection.allinram.DataCollection;
import service.FileService;

import java.io.Serializable;

public interface Task<T> extends Serializable {
    public T run(FileService fileService);
}
