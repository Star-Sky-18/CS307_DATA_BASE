package service;

import client.CallBack;

import java.util.List;

public interface QueryableQueue {
    void queryTTCByTimeCity(int timeStart,int timeStop,String city, CallBack<List<String[]>> callBack) throws Exception;

    void queryCCLLByCity(String city, CallBack<String[]> callBack) throws Exception;

    void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws Exception;

    void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) throws Exception;

    void addTimeTemperatureCity(CallBack<Boolean> callBack,String... content) throws Exception;

    void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws Exception;
}
