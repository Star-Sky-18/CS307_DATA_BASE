package client;

import client.callbacks.PrintCallBack;
import dataCollection.allinram.CityCountryLL;
import dataCollection.allinram.CityTemperature;
import dataCollection.allinram.DataCollection;
import service.FileService;
import service.MyServiceQueue;
import service.ServiceQueue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Client {
    static {
        new MyTask();
    }

    static final String TTC = "TimeTemperatureCity";
    static final String CCLL = "CityCountryLongitudeLatitude";
    static String path = "out/production/surface_temperature/client/MyTask.class";

    public static void main(String[] args) throws Exception {
        var registry = LocateRegistry.getRegistry("localhost", 7718);
        var service = (ServiceQueue) registry.lookup("queue");
        var callBack = new PrintCallBack<List<String[]>>(true,true);
        var callBack2 = new PrintCallBack<List<String[]>>(true,true);
        //        var bytes = Files.readAllBytes(Paths.get(path));
//        service.enqueueTask(bytes, new MyCallBack());
//        service.queryCountryLLByCity("Århus",callBack);
        service.queryTTCByTimeCity(199001,200012,"Beian",callBack);
        service.queryTTCByFilterWithoutIndex(new MyServiceQueue.TTC("Bei",0,20,0,20,188001,190001),callBack2);
//        service.queryTemperatureByTimeCity("197011Århus",callBack);

    }

    static void loadClass(ServiceQueue service) throws Exception {
        var path = "out/production/surface_temperature/dataCollection/CityTemperature.class";
        var bytes = Files.readAllBytes(Paths.get(path));
        service.loadSerializableClass(path, bytes);
    }
}

class MyTask implements Task<Set<Double>> {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Double> run(FileService data) {
        try {
//            var mapTable = (DataCollection.Table) data.getMap("city_temperature");
//        var setTable = (DataCollection.Table) data.getSet("city_country_ll");
//            var map = (Map<String, CityTemperature>) mapTable.getContent();
//        var set = (Set<CityCountryLL>) setTable.getContent();
//        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        var s = set.stream().filter(cityCountryLL -> cityCountryLL.getCountry().contains("A"))
//                .map(c->c.getCity()+" "+c.getCountry()+" "+c.getLongitude()+" "+c.getLatitude())
//                .collect(Collectors.joining("\n"));
//            var s = map.values().stream().filter(cityTemperature -> cityTemperature.getCityName().contains("ga"))
//                    .map(CityTemperature::getAvgTemperature).collect(Collectors.toUnmodifiableSet());
//            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(DataCollection data) {
        data.createMap("city_temperature", CityTemperature.class);
        data.createSet("city_country_ll", CityCountryLL.class);

        var mapTable = (DataCollection.Table) data.getMap("city_temperature");
        var setTable = (DataCollection.Table) data.getSet("city_country_ll");
        var map = (Map<String, CityTemperature>) mapTable.getContent();
        var set = (Set<CityCountryLL>) setTable.getContent();
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        var i = 0;
        try (var reader = new BufferedReader(new FileReader("txts/GlobalLandTemperaturesByCity.csv"))) {
            var line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                var content = line.split(",");
                var date = LocalDate.parse(content[0], format);
                var avg = content[1].length() > 0 ? Double.parseDouble(content[1]) : null;
                var avgUn = content[2].length() > 0 ? Double.parseDouble(content[2]) : null;
                var city = content[3];
                var country = content[4];
                var latitude = content[5];
                var longitude = content[6];
                map.put(date.format(format) + "|" + city, new CityTemperature(date, city, avg, avgUn));
                var cityCountryLL = new CityCountryLL(city, country, longitude, latitude);
                set.add(cityCountryLL);
                if (++i % 500000 == 0) {
                    System.out.println(i);
                    System.gc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
////        var df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        map.put("GZ|2000-00-00",new CityTemperature(LocalDate.parse("2000/01/01",df),"GZ",11,6));
}


