package service.rmiserver;

import client.CallBack;
import service.TTCFilter;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class RMIQueryHandler {

    protected void handleQueryTTCByTimeCity(FileService fileService,int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) {
        var keys = new ArrayList<Integer>();
        for (var i = timeStart; i <= timeStop; i++) {
            keys.add((i + city).hashCode());
            if (i % 100 == 12) {
                i += 88;
            }
        }
        try {
            var result = keys.stream().map(i -> fileService.indexMap.get("TimeTemperatureCity_Time_City").find(i, null))
                    .flatMap(Collection::stream).map(fileService.tableMap.get("TimeTemperatureCity")::getInfoByLine)
                    .collect(Collectors.toList());
            callBack.callBack(result);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void handleQueryCCLLByCity(FileService fileService, String city, CallBack<String[]> callBack){
        try {
            var result = fileService.indexMap.get("CityCountryLongitudeLatitude_City").find(city.hashCode(), city)
                    .stream()
                    .map(fileService.tableMap.get("CityCountryLongitudeLatitude")::getInfoByLine)
                    .collect(Collectors.toList());
            callBack.callBack(result.get(0));
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void handleQueryTTCByFilterWithoutIndex(FileService fileService,TTCFilter filter, CallBack<List<String[]>> callBack){
        try {
            var result = fileService.tableMap.get("TimeTemperatureCity").filterAllLines(filter);
            callBack.callBack(result);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void handleQueryTCCByVagueCity(FileService fileService,int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack){
        try {
            List<String> citys = Arrays.stream(fileService.tableMap.get("CityCountryLongitudeLatitude").getAllLines()).skip(1)
                    .map(s -> s.split(",")).filter(s -> s[0].contains(contained))
                    .map(s -> s[0]).collect(Collectors.toList());
            var keys = new ArrayList<Integer>();
            for (var i = timeStart; i <= timeStop; i++) {
                for (var j = 0; j < citys.size(); j++) {
                    keys.add((i + citys.get(j)).hashCode());
                }
                if (i % 100 == 12) {
                    i += 88;
                }
            }
            var result = keys.stream().map(i -> fileService.indexMap.get("TimeTemperatureCity_Time_City").find(i, null))
                    .flatMap(Collection::stream).map(fileService.tableMap.get("TimeTemperatureCity")::getInfoByLine)
                    .filter(s -> {
                        var t = Integer.parseInt(s[0]);
                        return timeStart <= t && t <= timeStop && s[3].contains(contained);
                    })
                    .collect(Collectors.toList());
            callBack.callBack(result);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void handleAddTimeTemperatureCity(FileService fileService,CallBack<Boolean> callBack, String... content){
        try {
            var flag = false;
            if (fileService.indexMap.get("TimeTemperatureCity_Time_City").find((content[0] + content[3]).hashCode(), content[0] + content[3]) == null) {
                var i = fileService.tableMap.get("TimeTemperatureCity").addLine(String.join(",", content));
                fileService.indexMap.get("TimeTemperatureCity_Time_City").insert((content[0] + content[3]).hashCode(), String.join(",", content), i);
                flag = true;
            }
            callBack.callBack(flag);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void handleAddCityCountryLL(FileService fileService,CallBack<Boolean> callBack, String... content){
        try {
            var flag = false;
            if (fileService.indexMap.get("CityCountryLongitudeLatitude_City").find(content[0].hashCode(), content[0]) == null) {
                var i = fileService.tableMap.get("CityCountryLongitudeLatitude").addLine(String.join(",", content));
                fileService.indexMap.get("CityCountryLongitudeLatitude_City").insert(content[0].hashCode(), String.join(",", content), i);
                flag = true;
            }
            callBack.callBack(flag);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }
}
