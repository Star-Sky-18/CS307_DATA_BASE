package service.rmiserver;

import client.CallBack;
import client.Task;
import dataCollection.allinram.DataCollection;
import dataCollection.allinram.MyDataCollection;
import dataCollection.bplustree.BPTree;
import dataCollection.bplustree.LineManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.concurrent.*;

public class FileService {
    private static FileService service = new FileService();
    private static final int CORE_POOL_SIZE =4;
    private static final int MAX_POOL_SIZE =50;
    private static final int KEEP_ALIVE_TIME =60;
    private ThreadPoolExecutor poolExecutor;
    private ServiceQueue serviceQueue;
    protected DataCollection data;
    private DynamicClassLoader classLoader;
    protected HashMap<String, LineManager> tableMap;
    protected HashMap<String, BPTree<String,Integer>> indexMap;

    FileService(){
        classLoader = new DynamicClassLoader();
        poolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(5000));
        poolExecutor.prestartCoreThread();
        data = MyDataCollection.getDataCollection();
        try {
            serviceQueue = new RMIServiceQueryQueue(this, new RMIQueryHandler());
            tableMap = new HashMap<>();
            DataCollectionFactory.initTables(tableMap,"CityCountryLongitudeLatitude","TimeTemperatureCity");
            indexMap = new HashMap<>();
            DataCollectionFactory.initIndexs(indexMap,"TimeTemperatureCity_Time_City","CityCountryLongitudeLatitude_City");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try{
            var registry = LocateRegistry.createRegistry(7718);
            registry.bind("queue",this.serviceQueue);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.err.println("OK!");
    }

    public static void main(String[] args) {
        getService();
    }

    public static FileService getService(){
        return service;
    }

    @SuppressWarnings("unchecked")
    public <T> void execute(byte[] b, CallBack<T> callBack){
        this.poolExecutor.execute(() -> {
            var loader = new DynamicClassLoader();
            var newClass = loader.loadMyClass(b);
            Task<T> task = null;
            try {
                var constructor = newClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                task = (Task<T>) constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert task != null;
            T result = task.run(FileService.this);
            try {
                callBack.callBack(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private static class DynamicClassLoader extends ClassLoader{
//        public void loadSerializableClass(String path) {
//            try {
//                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//                method.setAccessible(true);
//                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//                method.invoke(classLoader, path);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        public Class<?> loadMyClass(byte[] b){
            return defineClass(null,b,0,b.length);
        }
    }

    protected void loadSerializableClass(Path path, byte[] b){
        try {
            if(!Files.exists(path))
                Files.createDirectories(path);
            Files.write(path,b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        classLoader.loadMyClass(b);
    }

    protected void execute(Runnable r){
        this.poolExecutor.execute(r);
    }
}
