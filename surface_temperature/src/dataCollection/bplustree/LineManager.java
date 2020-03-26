package dataCollection.bplustree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LineManager {
    private String lraFileName;
    private int[] lineBytePoint;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;
    private String lines;

    private LineManager(String lraFilename, boolean preLoad){
        this.lraFileName = lraFilename;
        var path = Paths.get(lraFilename);
        this.lines = preLoad?getAll():null;
        try {
            fileChannel = FileChannel.open(path,StandardOpenOption.READ,StandardOpenOption.WRITE);
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,Files.size(path));
            try(var bf = new BufferedReader(new FileReader(lraFileName))){
                var line = bf.readLine();
                lineBytePoint = new int[9000000];
                lineBytePoint[0]= line.getBytes().length+1;
                var index = 1;
                while((line=bf.readLine())!=null){
                    lineBytePoint[index] = lineBytePoint[index++-1] + line.getBytes().length+1;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
//            var list = Files.readString(path).split("\n");
//            var lineBytePoint = new int[list.length];
//            lineBytePoint[0]= list[0].getBytes().length+1;
//            for(int i=1;i<list.length;i++){
//                lineBytePoint[i] = lineBytePoint[i-1] + list[i].getBytes().length+1;
//            }
//            for(int i=0;i<lineBytePoint.length;i++){
//                if(lineBytePoint[i]!=this.lineBytePoint[i]){
//                    System.out.println(lineBytePoint[i]+" "+this.lineBytePoint[i]);
//                }else{
//                    System.out.println(true);
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getInfoByLine(int line){
        var bytes = new byte[lineBytePoint[line + 1] - lineBytePoint[line]-1];
        if(lines==null) {
            synchronized (mappedByteBuffer) {
                mappedByteBuffer.position(lineBytePoint[line]);
                mappedByteBuffer.get(bytes);
            }
        }else{
            System.arraycopy(lines.getBytes(),lineBytePoint[line],bytes,0,bytes.length);
        }
        return new String(bytes).split(",");
    }

    public List<String[]> getInfosByLines(int... lines){
        return Arrays.stream(lines).mapToObj(this::getInfoByLine).collect(Collectors.toList());
    }

    public List<String[]> getInfosByLines(List<Integer> lines){
        return lines.stream().map(this::getInfoByLine).collect(Collectors.toList());
    }

    private String getAll(){
        synchronized (mappedByteBuffer) {
            mappedByteBuffer.position(0);
            try {
                var bytes = new byte[(int) fileChannel.size()];
                mappedByteBuffer.get(bytes);
                return new String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String[] getAllLines(){
        return getAll().split("\n");
    }

    public List<String[]> filterAllLines(Predicate<String[]> predicate) throws Exception{
        var re = new ArrayList<String[]>();
        try(var br = new BufferedReader(new FileReader(lraFileName))){
            var line = br.readLine();
            while((line=br.readLine())!=null){
                var con = line.split(",");
                if(predicate.test(con)){
                    re.add(con);
                }
            }
        }catch (Exception e){
            throw e;
        }
        return re;
    }

    public static LineManager getLineMapByLRAFileName(String lraFileName, boolean isPreLoad){
        return new LineManager(lraFileName,isPreLoad);
    }

    public int addLine(String line){
        try {
            synchronized (mappedByteBuffer) {
                fileChannel.position(lineBytePoint[lineBytePoint.length-1]);
                fileChannel.write(ByteBuffer.wrap((line+"\n").getBytes()));
                fileChannel.force(false);
                mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,fileChannel.size());
                var newLines = new int[lineBytePoint.length+1];
                System.arraycopy(lineBytePoint,0,newLines,0,lineBytePoint.length);
                newLines[newLines.length-1] = newLines[newLines.length-2]+line.getBytes().length+1;
                lineBytePoint = newLines;
                return lineBytePoint[lineBytePoint.length-2];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void close(){
        synchronized (mappedByteBuffer){
            try {
                this.fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush(){
        synchronized (mappedByteBuffer){
            try {
                this.fileChannel.force(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
