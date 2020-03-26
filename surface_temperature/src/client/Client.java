package client;

import client.callbacks.PrintCallBack;
import service.TTCFilter;
import service.rmiserver.ServiceQueue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.util.List;

public class Client {
    static {
        new MyTask();
    }

    public Client(){

    }

    static final String TTC = "TimeTemperatureCity";
    static final String CCLL = "CityCountryLongitudeLatitude";
    static String path = "out/production/surface_temperature/client/MyTask.class";

    public static void main(String[] args) throws Exception {
        var registry = LocateRegistry.getRegistry("localhost", 7718);
        var service = (ServiceQueue) registry.lookup("queue");
        var callBack = new PrintCallBack<List<String[]>>(true,true,"client");
//        var callBack2 = new PrintCallBack<List<String[]>>(true,true);
        //        var bytes = Files.readAllBytes(Paths.get(path));
//        service.enqueueTask(bytes, new MyCallBack());
//        service.queryCountryLLByCity("Århus",callBack);
        callBack.setMax(1);
//        for(var i=0;i<50;i++) {
//            service.queryTTCByTimeCity(180001+i*100,185001+i*100, "Beian", callBack);
            service.queryTTCByFilterWithoutIndex(new TTCFilter("Bei",0,10,0,5,188001,190001),callBack);
//        }

    }

    static void loadClass(ServiceQueue service) throws Exception {
        var path = "out/production/surface_temperature/dataCollection/CityTemperature.class";
        var bytes = Files.readAllBytes(Paths.get(path));
        service.loadSerializableClass(path, bytes);
    }
}



