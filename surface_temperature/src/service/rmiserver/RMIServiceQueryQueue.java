package service.rmiserver;

import client.CallBack;
import dataCollection.bplustree.BPTree;
import dataCollection.bplustree.LineManager;
import service.TTCFilter;

import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RMIServiceQueryQueue extends UnicastRemoteObject implements ServiceQueue{

    private FileService fileService;
    private RMIQueryHandler handler;

    RMIServiceQueryQueue( FileService fileService,RMIQueryHandler handler) throws RemoteException {
        this.fileService = fileService;
        this.handler = handler;
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
            handler.handleQueryTTCByTimeCity(fileService, timeStart, timeStop, city, callBack);
        });
    }

    @Override
    public void queryCCLLByCity(String city, CallBack<String[]> callBack) throws RemoteException {
        fileService.execute(() -> {
            handler.handleQueryCCLLByCity(fileService, city, callBack);
        });
    }

    @Override
    public void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            handler.handleQueryTTCByFilterWithoutIndex(fileService, filter, callBack);
        });
    }

    @Override
    public void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws RemoteException {
        fileService.execute(() -> {
            handler.handleQueryTCCByVagueCity(fileService, timeStart, timeStop, contained, callBack);
        });
    }

    @Override
    public void addTimeTemperatureCity(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
            handler.handleAddTimeTemperatureCity(fileService,callBack,content);
        });
    }

    @Override
    public void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws RemoteException {
        fileService.execute(() -> {
            handler.handleAddCityCountryLL(fileService,callBack,content);
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

    public FileService getFileService() {
        return fileService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public RMIQueryHandler getHandler() {
        return handler;
    }

    public void setHandler(RMIQueryHandler handler) {
        this.handler = handler;
    }
}
