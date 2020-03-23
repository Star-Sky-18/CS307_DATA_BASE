package client.callbacks;

import client.CallBack;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class PrintCallBack<T> extends UnicastRemoteObject implements CallBack<T> {
    boolean timeFlag,autoUnexport;
    long start;
    public PrintCallBack(boolean showTime,boolean autoUnexport) throws RemoteException {
        start = System.currentTimeMillis();
        timeFlag = showTime;
        this.autoUnexport = autoUnexport;
    }

    @Override
    public void callBack(T t) throws RemoteException {
        if(timeFlag)
            System.out.println("time: "+(System.currentTimeMillis()-start));
        System.out.println(getString(t));
        if(autoUnexport)
            UnicastRemoteObject.unexportObject(this, true);
    }

    private String getString(Object o){
        if(o.getClass().isArray()){
            return Arrays.toString((Object[])o);
        }
        if(o instanceof Collection){
            return (String) ((Collection)o).stream().map(this::getString).collect(Collectors.joining(","));
        }
        return o.toString();
    }
}
