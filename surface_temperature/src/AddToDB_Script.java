import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import org.postgresql.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

public class AddToDB_Script {
    static Connection con;
    static{
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs307","checker","123456");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        var path = Paths.get("txts/GlobalLandTemperaturesByCity.csv");
        Arrays.stream(Files.readString(path).split("\n"))
                .skip(1).parallel().forEach(AddToDB_Script::add);
    }

    static void add(String str){
        addCityLL(str);
        addTemperature(str);
    }

    static void addTemperature(String string){
        var content = string.split(",");
        var sql = "INSERT INTO city_temperature (date, average_temperature, average_temperature_uncertainty, city) VALUES (?,?,?,?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = con.prepareStatement(sql);

            preparedStatement.setDate(1,Date.valueOf(content[0]));
            if(content[1].length()>0)
                preparedStatement.setDouble(2,Double.parseDouble(content[1]));
            else
                preparedStatement.setNull(2,Types.DOUBLE);
            if(content[2].length()>0)
                preparedStatement.setDouble(3,Double.parseDouble(content[2]));
            else
                preparedStatement.setNull(3,Types.DOUBLE);
            preparedStatement.setString(4,content[3]);

            preparedStatement.execute();

        }catch (Exception e){
            if(!e.getMessage().contains("错误: 重复键违反唯一约束\"city_temperature_pkey\"")) {
                e.printStackTrace();
                System.out.println(preparedStatement.toString());
                new Scanner(System.in).nextInt();
            }
        }
    }

    static void addCityLL(String string){
        var content = string.split(",");
        var sql = "INSERT INTO city_country_ll (city, country, longitude, latitude) VALUES (?,?,?,?)";
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,content[3]);
            preparedStatement.setString(2,content[4]);
            preparedStatement.setString(3,content[6]);
            preparedStatement.setString(4,content[5]);
            preparedStatement.execute();
            preparedStatement.getResultSet();

        }catch (SQLException e){
            if(!e.getMessage().contains("错误: 重复键违反唯一约束\"city_country_ll_pkey\""))
                e.printStackTrace();
        }
    }
}
