package service;

import com.esotericsoftware.kryo.io.Input;
import dataCollection.bplustree.BPTree;
import dataCollection.bplustree.LineMap;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class DataCollectionFactory {
    static File lraDir = new File("lras");
    static File treeDir = new File("trees");
    static HashMap<String,String[]> columnMap;

    static{
        columnMap = initLras();
    }

    public static void main(String[] args) throws IOException {
//        parseMyCsvTo_TTC();
//        parseMyCsvTo_CCLL();
        getTreeByTableName_ColumnName("TimeTemperatureCity","Time","City");
        getTreeByTableName_ColumnName("CityCountryLongitudeLatitude","City");
    }

    public static BPTree<String,Integer> getTreeByTableName_ColumnName(String tableName, String... columnName){
        var name = tableName+"_"+ String.join("_", columnName);
        var path = Paths.get(treeDir.getPath()+File.separator+name+".tree");
        if(!Files.exists(path.getParent())){
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(Files.exists(path)){
            try {
                var bytes = Files.readAllBytes(path);
                var kryo = BPTree.getKryo();
                return kryo.readObject(new Input(bytes), BPTree.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            System.out.println(name);
            var bPTree = new BPTree<String,Integer>(195,name);
            var columnLine = Arrays.asList(columnMap.get(tableName));
            var indexs = new int[columnName.length];
            for(int i=0;i<indexs.length;i++){
                indexs[i] = columnLine.indexOf(columnName[i]);
            }
            System.out.println(Arrays.toString(indexs));
            var tablePath = Paths.get(lraDir+File.separator+tableName+".lra");
            var lines = Files.lines(tablePath)
                    .skip(1).map(s-> {
                       var con =  s.split(",");
                       StringBuilder re = new StringBuilder();
                        for (int index : indexs) {
                            re.append(con[index]);
                        }
                       return re.toString();
                    }).collect(Collectors.toList());
            for(int i=0;i<lines.size();i++){
                bPTree.insert(lines.get(i).hashCode(),lines.get(i),i);
            }
            bPTree.serialize();
            return bPTree;
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

    private static HashMap<String,String[]> initLras(){
        try {
            columnMap = new HashMap<>();
            for(var file: Objects.requireNonNull(lraDir.listFiles())){
                var line = new Scanner(file).nextLine().split(",");
                columnMap.put(file.getName().split("\\.")[0],line);
            }
            return columnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static void parseMyCsvTo_TTC() throws IOException {
        var list = Files.readAllLines(Paths.get("txts/GlobalLandTemperaturesByCity.csv"));
        var newList = new ArrayList<>(list.stream().skip(1).map(s -> {
            var r = s.split(",");
            var date = r[0].split("-");
            r[0] = date[0] + date[1];
            if (r[1].length() > 0)
                r[1] = String.format("%.3f", Double.parseDouble(r[1]));
            if (r[2].length() > 0)
                r[2] = String.format("%.3f", Double.parseDouble(r[2]));
            return r[0] + "," + r[1] + "," + r[2] + "," + r[3];
        }).collect(Collectors.toMap(s->s.split(",")[0]+s.split(",")[3],s->s,(o1,o2)->o1)).values());
        newList.add(0,"Time,AverageTemperature,AverageTemperatureUncertainty,City");
        FileChannel channel = FileChannel.open(Paths.get("lras/TimeTemperatureCity.lra"),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        channel.write(ByteBuffer.wrap(String.join("\n", newList).getBytes()));
        channel.close();
    }

    protected static void parseMyCsvTo_CCLL() throws IOException {
        var list = Files.readAllLines(Paths.get("txts/GlobalLandTemperaturesByCity.csv"));
        var newList = new ArrayList<>(list.stream().skip(1).map(s -> {
            var r = s.split(",");
            return r[3] + "," + r[4] + "," + r[6] + "," + r[5];
        }).collect(Collectors.toMap(s->s.split(",")[0],s->s,(o1,o2)->o1)).values());
        newList.add(0,"City,Country,Latitude,Longitude");
        var path = Paths.get("lras/CityCountryLongitudeLatitude.lra");
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        FileChannel channel = FileChannel.open(path,
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        channel.write(ByteBuffer.wrap(String.join("\n", newList).getBytes()));
        channel.close();
    }

    protected static void initTables(HashMap<String,LineMap> map, String... tableNames){
        try{
            for(var tableName:tableNames){
                map.put(tableName,LineMap.getLineMapByLRAFileName(lraDir+File.separator+tableName+".lra",false));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static void initIndexs(HashMap<String, BPTree<String,Integer>> map, String... indexNames){
        for(var indexName:indexNames) {
            var names = indexName.split("_");
            var a = Arrays.copyOfRange(names,1,names.length);
            map.put(indexName, getTreeByTableName_ColumnName(names[0],a));
        }
    }
}
