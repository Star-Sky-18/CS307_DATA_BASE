package service;

import client.CallBack;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface ServiceQueue extends Remote {
    void loadSerializableClass(String path, byte[] b) throws RemoteException;

    <T> void enqueueTask(byte[] b, CallBack<T> callBack) throws RemoteException;

    void serialize() throws RemoteException;

    void queryTTCByTimeCity(int timeStart,int timeStop,String city, CallBack<List<String[]>> callBack) throws RemoteException;

    void queryCountryLLByCity(String city, CallBack<String[]> callBack) throws RemoteException;

    void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws RemoteException;

    void queryTTCByFilterWithoutIndex(Predicate<String[]> filter, CallBack<List<String[]>> callBack) throws RemoteException;

    void addTimeTemperatureCity(CallBack<Boolean> callBack,String... content) throws RemoteException;

    void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws RemoteException;

    @Deprecated
    void queryByNamedIndexs(String tableName, String indexName, String[] indexs, Predicate<String[]> predicate
            , CallBack<List<String[]>> callBack) throws RemoteException;
}
