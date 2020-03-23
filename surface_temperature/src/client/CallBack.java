package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack<T> extends Remote, Serializable {
    public void callBack(T t) throws RemoteException;
}
