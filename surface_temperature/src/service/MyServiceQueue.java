package service;

import client.CallBack;
import dataCollection.bplustree.BPTree;
import dataCollection.bplustree.LineMap;

import java.io.Serializable;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class MyServiceQueue extends UnicastRemoteObject implements ServiceQueue {
    public static class TTC implements Predicate<String[]>, Serializable {
        String reg;
        double temStart, temStop;
        double uncStart, uncStop;
        int timeStart, timeStop;

        public TTC(String reg, double temStart, double temStop, double uncStart, double uncStop, int timeStart, int timeStop) {
            this.reg = reg;
            this.temStart = temStart;
            this.temStop = temStop;
            this.uncStart = uncStart;
            this.uncStop = uncStop;
            this.timeStart = timeStart;
            this.timeStop = timeStop;
        }

        @Override
        public boolean test(String[] strings) {
            if (strings[1].length() == 0 || strings[2].length() == 0) return false;
            int time = Integer.parseInt(strings[0]);
            double tem = Double.parseDouble(strings[1]);
            double unc = Double.parseDouble(strings[2]);
            return timeStart <= time && time <= timeStop
                    && strings[3].contains(reg) && temStart <= tem && tem <= temStop && uncStart <= unc && unc <= uncStop;
        }
    }

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
        fileService.tableMap.values().forEach(LineMap::flush);
    }

    @Override
    public void queryTTCByTimeCity(int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            var keys = new ArrayList<Integer>();
            for (var i = timeStart; i <= timeStop; i++) {
                keys.add((timeStart + city).hashCode());
                if (i % 100 == 12) {
                    i += 88;
                }
            }
            var result = keys.stream().map(i -> fileService.indexMap.get("TimeTemperatureCity_Time_City").find(i, null))
                    .flatMap(Collection::stream).map(fileService.tableMap.get("TimeTemperatureCity")::getInfoByLine).collect(Collectors.toList());
            try {
                callBack.callBack(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void queryCountryLLByCity(String city, CallBack<String[]> callBack) throws RemoteException {
        fileService.execute(() -> {
            var result = fileService.indexMap.get("CityCountryLongitudeLatitude_City").find(city.hashCode(), city)
                    .stream().map(fileService.tableMap.get("CityCountryLongitudeLatitude")::getInfoByLine)
                    .collect(Collectors.toList());
            try {
                callBack.callBack(result.get(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void queryTTCByFilterWithoutIndex(Predicate<String[]> filter, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            var result = Arrays.stream(fileService.tableMap.get("TimeTemperatureCity").getAllLines())
                    .skip(1).map(a -> a.split(",")).filter(filter).collect(Collectors.toList());
            try {
                callBack.callBack(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            List<String> citys = Arrays.stream(fileService.tableMap.get("CityCountryLongitudeLatitude").getAllLines()).skip(1)
                    .map(s -> s.split(",")).filter(s -> s[0].contains(contained))
                    .map(s -> s[0]).collect(Collectors.toList());
            var keys = new ArrayList<Integer>();
            for (var i = timeStart; i <= timeStop; i++) {
                for (var j = 0; j < citys.size(); j++) {
                    keys.add((timeStart + citys.get(j)).hashCode());
                    if (i % 100 == 12) {
                        i += 88;
                    }
                }
            }
            var result = keys.stream().map(i -> fileService.indexMap.get("TimeTemperatureCity_Time_City").find(i, null))
                    .flatMap(Collection::stream).map(fileService.tableMap.get("TimeTemperatureCity")::getInfoByLine).collect(Collectors.toList());
            try {
                callBack.callBack(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addTimeTemperatureCity(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
            var flag = false;
            if (fileService.indexMap.get("TimeTemperatureCity_Time_City").find((content[0] + content[3]).hashCode(), content[0] + content[3]) == null) {
                var i = fileService.tableMap.get("TimeTemperatureCity").addLine(String.join(",", content));
                fileService.indexMap.get("TimeTemperatureCity_Time_City").insert((content[0] + content[3]).hashCode(), String.join(",", content), i);
                flag = true;
            }
            try {
                callBack.callBack(flag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
            var flag = false;
            if (fileService.indexMap.get("CityCountryLongitudeLatitude_City").find(content[0].hashCode(), content[0]) == null) {
                var i = fileService.tableMap.get("CityCountryLongitudeLatitude").addLine(String.join(",", content));
                fileService.indexMap.get("CityCountryLongitudeLatitude_City").insert(content[0].hashCode(), String.join(",", content), i);
                flag = true;
            }
            try {
                callBack.callBack(flag);
            } catch (RemoteException e) {
                e.printStackTrace();
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
