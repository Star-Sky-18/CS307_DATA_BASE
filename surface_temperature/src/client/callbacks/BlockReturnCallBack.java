package client.callbacks;

import client.CallBack;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BlockReturnCallBack<T> extends PrintCallBack<T> implements CallBack<T> {
    T t;

    public BlockReturnCallBack(boolean showTime,boolean autoUnexport) throws RemoteException {
        super(showTime,autoUnexport);
        this.t = null;
    }

    @Override
    public void callBack(T t) throws RemoteException {
        new Thread(() -> {
            this.t = t;
            if(timeFlag)
                System.out.println("time: " + (System.currentTimeMillis() - this.start));
            if(autoUnexport) {
                try {
                    UnicastRemoteObject.unexportObject(this, true);
                } catch (NoSuchObjectException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public T block() throws InterruptedException {
        while (this.t == null) {
            Thread.sleep(10);
        }
        var re = t;
        this.t = null;
        return re;
    }
}
