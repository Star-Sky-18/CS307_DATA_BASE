package client.callbacks;

import client.CallBack;

import java.io.PrintWriter;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BlockReturnCallBack<T> extends PrintCallBack<T> implements CallBack<T> {
    T t;

    public BlockReturnCallBack(boolean showTime, boolean autoUnexport, String str) throws RemoteException {
        super(showTime, autoUnexport, str);
        this.t = null;
    }

    @Override
    public void callBack(T t) throws RemoteException {
        this.t = t;
        synchronized (BlockReturnCallBack.this) {
            count++;
        }
        if (timeFlag)
            System.out.print(" ," + (System.currentTimeMillis() - this.start));
    }

    public T block() throws InterruptedException {
        while (this.t == null) {
            Thread.sleep(50);
        }
        var re = t;
        this.t = null;
        return re;
    }

    public void blockForAll() throws InterruptedException {
        while (count < max)
            Thread.sleep(50);
        if (autoUnexport) {
            try {
                UnicastRemoteObject.unexportObject(this, false);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
