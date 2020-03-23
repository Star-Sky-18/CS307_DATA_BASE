package service.rmiserver;

import client.CallBack;
import service.TTCFilter;
import service.TestableQueue;

import java.rmi.Remote;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface ServiceQueue extends Remote, TestableQueue {
    void loadSerializableClass(String path, byte[] b) throws Exception;

    <T> void enqueueTask(byte[] b, CallBack<T> callBack) throws Exception;

    void serialize() throws Exception;

    void queryTTCByTimeCity(int timeStart,int timeStop,String city, CallBack<List<String[]>> callBack) throws Exception;

    void queryCCLLByCity(String city, CallBack<String[]> callBack) throws Exception;

    void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws Exception;

    void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) throws Exception;

    void addTimeTemperatureCity(CallBack<Boolean> callBack,String... content) throws Exception;

    void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws Exception;

    @Deprecated
    void queryByNamedIndexs(String tableName, String indexName, String[] indexs, Predicate<String[]> predicate
            , CallBack<List<String[]>> callBack) throws Exception;
}
