import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLExecutor {
    static Connection con;

    public static void main(String[] args) {
        var start = System.currentTimeMillis();
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs307", "checker", "123456");
            var statement = con.createStatement();
            var ts = new Thread[9];
            for (int i = 0; i < 9; i++) {
                ts[i] = new Thread(() -> {
                    try {
                        statement.executeQuery("select count(*) from city_temperature \n" +
                                "where city like '%ga%';");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                ts[i].run();
            }
            var result = statement.executeQuery("select count(*) from city_temperature \n" +
                    "where city like '%ga%';");
//            System.out.println(result.getFetchSize());
            var sb = new StringBuilder();
            while (result.next()) {
//                sb.append("Date:").append(result.getDate(1)).append(" City:")
//                        .append(result.getString(4)).append(" avgTemperature:")
//                        .append(result.getDouble(2)).append(" avgTemperatureUncertainty:")
//                        .append(result.getDouble(3)).append("\n");
            }
            System.out.println(sb);
            for(int i=0;i<9;i++){
                ts[i].join();
            }
            System.out.println(System.currentTimeMillis() - start + " ms");
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
