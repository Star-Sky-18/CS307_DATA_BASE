package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class OffsetTest {
    public static void main(String[] args) throws Exception{
        var input = new BufferedReader(new FileReader("txts/GlobalLandTemperaturesByCity.csv"));
        var s = input.readLine();
        var random = new RandomAccessFile(new File("txts/GlobalLandTemperaturesByCity.csv"),"r");
        random.seek(s.length()+2);
        System.out.println(random.readLine());
    }
}
