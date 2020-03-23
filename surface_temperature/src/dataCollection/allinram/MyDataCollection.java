package dataCollection.allinram;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyDataCollection implements DataCollection {
    private static MyDataCollection myDataCollection;

    Map<String, List<? extends Serializable>> lists = new HashMap<>();
    Map<String, ConcurrentHashMap<String,? extends Serializable>> maps = new HashMap<>();
    Map<String, Set<? extends Serializable>> sets = new HashMap<>();

    Map<String, Class<? extends Serializable>> classInfos = new ConcurrentHashMap<>();

    private MyDataCollection(){

    }

    @Override
    public Table<List<? extends Serializable>> getList(String name) {
        return new Table<>(lists.get(name),classInfos.get(name));
    }


    @Override
    public Table<Map<String,? extends Serializable>> getMap(String name) {
        return new Table<>(maps.get(name),classInfos.get(name));
    }

    @Override
    public Table<Set<? extends Serializable>> getSet(String name){
        return new Table<>(sets.get(name),classInfos.get(name));
    }

    @Override
    public <C extends Serializable> void createList(String name, Class<C> classInfo){
        var list = Collections.synchronizedList(new ArrayList<C>());
        lists.put(name,list);
        classInfos.put(name,classInfo);
    }

    @Override
    public <C extends Serializable> void createMap(String name, Class<C> classInfo){
        var map = new ConcurrentHashMap<String,C>();
        maps.put(name,map);
        classInfos.put(name, classInfo);
    }

    @Override
    public <C extends Serializable> void createSet(String name, Class<C> classInfo){
        var set = Collections.synchronizedSet(new HashSet<C>());
        sets.put(name,set);
        classInfos.put(name, classInfo);
    }

    public static DataCollection getDataCollection() {
        if(myDataCollection == null){
            var file = new File("serializations/myDataCollection.slt");
            if(file.exists()) {
                try (var ois = new ObjectInputStream(new FileInputStream(file))) {
                    myDataCollection = (MyDataCollection) ois.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                myDataCollection = new MyDataCollection();
            }
        }
        return myDataCollection;
    }

    @Override
    public void serialize() {
        var file = new File("serializations/myDataCollection.slt");
        try(var oos = new ObjectOutputStream(new FileOutputStream(file))){
            oos.writeObject(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
