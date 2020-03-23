package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack<T> extends Remote, Serializable {
    void callBack(T t) throws RemoteException;

    void err(Exception e) throws RemoteException;
}
