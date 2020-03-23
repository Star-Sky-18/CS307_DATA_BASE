package dataCollection.allinram;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public interface DataCollection{
    class Table<Type>{
        Type content;
        Class<? extends Serializable> classInfo;
        Table(Type content, Class<? extends Serializable> classInfo){
            this.content = content;
            this.classInfo = classInfo;
        }

        public Type getContent(){
            return content;
        }

        public Class<? extends Serializable> getClassInfo(){
            return classInfo;
        }
    }

    public Table<List<? extends Serializable>> getList(String name);

    public Table<Map<String,? extends Serializable>> getMap(String name);

    public Table<Set<? extends Serializable>> getSet(String name);

    public <C extends Serializable> void createList(String name, Class<C> classInfo);

    public <C extends Serializable> void createMap(String name, Class<C> classInfo);

    public <C extends Serializable> void createSet(String name, Class<C> classInfo);

    public void serialize();
}
