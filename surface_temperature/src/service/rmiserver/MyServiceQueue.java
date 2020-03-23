package service.rmiserver;

import client.CallBack;
import dataCollection.bplustree.BPTree;
import dataCollection.bplustree.LineManager;
import service.TTCFilter;

import java.io.Serializable;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class MyServiceQueue extends UnicastRemoteObject implements ServiceQueue {

    FileService fileService;

    MyServiceQueue(FileService fileService) throws RemoteException {
        super();
        this.fileService = fileService;
    }

    @Override
    public void loadSerializableClass(String path, byte[] b) throws RemoteException {
        fileService.loadSerializableClass(Paths.get(path), b);
    }

    public <T> void enqueueTask(byte[] b, CallBack<T> callBack) throws RemoteException {
        fileService.execute(b, callBack);
    }

    /**
     * should be sync
     *
     * @throws RemoteException
     */
    @Override
    public void serialize() throws RemoteException {
        fileService.indexMap.values().forEach(BPTree::serialize);
        fileService.tableMap.values().forEach(LineManager::flush);
    }

    @Override
    public void queryTTCByTimeCity(int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    public void queryCCLLByCity(String city, CallBack<String[]> callBack) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    public void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    public void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    public void addTimeTemperatureCity(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    public void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
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
        });
    }

    @Override
    @Deprecated
    public void queryByNamedIndexs(String tableName, String indexName, String[] indexs, Predicate<String[]> predicate
            , CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            var result = Arrays.stream(indexs).map(i -> fileService.indexMap.get(indexName).find(i.hashCode(), i))
                    .map(fileService.tableMap.get(tableName)::getInfosByLines).flatMap(Collection::stream).filter(predicate).collect(Collectors.toList());
            try {
                callBack.callBack(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

}
