package service.jdbctester;

import client.CallBack;
import service.TTCFilter;
import service.TestableQueue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTester implements TestableQueue {
    Connection con;
    String user, password;
    boolean autoClose;

    public JdbcTester(String user, String password, boolean autoClose) {
        this.user = user;
        this.password = password;
        this.autoClose = autoClose;
        connect();
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs307", user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JdbcTester(String user) throws SQLException {
        this(user, "123456", true);
    }

    @Override
    public void queryTTCByTimeCity(int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) throws Exception {
        new Thread(() -> {
            try {
                var list = queryTTCByTimeCity(timeStart, timeStop, city);
                callBack.callBack(list);
            } catch (Exception e) {
                try {
                    callBack.err(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } finally {
                if (autoClose)
                    close();
            }
        }).start();
    }

    private List<String[]> queryTTCByTimeCity(int timeStart, int timeStop, String city) throws Exception {
        var start = (timeStart + "").substring(0, 4) + "-" + (timeStart + "").substring(4) + "-01";
        var stop = (timeStop + "").substring(0, 4) + "-" + (timeStop + "").substring(4) + "-01";
        var state = con.prepareStatement("select * from city_temperature " +
                "where city = ?" +
                "and date between ? and ?");
        state.setString(1, city);
        state.setDate(2, Date.valueOf(start));
        state.setDate(3, Date.valueOf(stop));
        return _xuan(state);
    }

    @Override
    public void queryCCLLByCity(String city, CallBack<String[]> callBack) throws Exception {
        new Thread(() -> {
            try {
                var re = queryCCLLByCity(city);
                callBack.callBack(re);
            } catch (Exception e) {
                try {
                    callBack.err(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } finally {
                if (autoClose)
                    close();
            }
        }).start();
    }

    private String[] queryCCLLByCity(String city) throws Exception {
        var state = con.prepareStatement("select * from city_country_ll " +
                "where city=?");
        state.setString(1, city);
        var result = state.executeQuery();
        if (result.next()) {
            return new String[]{result.getString(1), result.getString(2)
                    , result.getString(3), result.getString(4)};
        }
        return null;
    }

    @Override
    public void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) throws Exception {
        new Thread(() -> {
            try {
                var re = queryTCCByVagueCity(timeStart, timeStop, contained);
                callBack.callBack(re);
            } catch (Exception e) {
                try {
                    callBack.err(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } finally {
                if (autoClose)
                    close();
            }
        }).start();
    }

    private List<String[]> queryTCCByVagueCity(int timeStart, int timeStop, String contained) throws Exception {
        var start = (timeStart + "").substring(0, 4) + "-" + (timeStart + "").substring(4) + "-01";
        var stop = (timeStop + "").substring(0, 4) + "-" + (timeStop + "").substring(4) + "-01";
        var state = con.prepareStatement("select * from city_temperature " +
                "where city like ? " +
                "and date between ? and ?");
        state.setString(1, "%" + contained + "%");
        state.setDate(2, Date.valueOf(start));
        state.setDate(3, Date.valueOf(stop));
        return _xuan(state);
    }

    private List<String[]> _xuan(PreparedStatement state) throws SQLException {
        var result = state.executeQuery();
        var list = new ArrayList<String[]>();
        while (result.next()) {
            list.add(new String[]{String.valueOf(result.getDate(1)), result.getFloat(2) + ""
                    , result.getFloat(3) + "", result.getString(4)});
        }
        return list;
    }

    @Override
    public void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) throws Exception {
        new Thread(() -> {
            try {
                var re = queryTTCByFilterWithoutIndex(filter);
                callBack.callBack(re);
            } catch (Exception e) {
                try {
                    callBack.err(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } finally {
                if (autoClose)
                    close();
            }
        }).start();

    }

    private List<String[]> queryTTCByFilterWithoutIndex(TTCFilter filter) throws Exception {
        var state = con.prepareStatement("select * from city_temperature " +
                "where date between ? and ?" +
                "and city like ? " +
                "and average_temperature between ? and ? " +
                "and average_temperature_uncertainty between ? and ? ");
        var start = (filter.getTimeStart() + "").substring(0, 4) + "-" + (filter.getTimeStart() + "").substring(4) + "-01";
        var stop = (filter.getTimeStop() + "").substring(0, 4) + "-" + (filter.getTimeStop() + "").substring(4) + "-01";
        state.setDate(1, Date.valueOf(start));
        state.setDate(2, Date.valueOf(stop));
        state.setString(3, "%" + filter.getReg() + "%");
        state.setFloat(4, (float) filter.getTemStart());
        state.setFloat(5, (float) filter.getTemStop());
        state.setFloat(6, (float) filter.getUncStart());
        state.setFloat(7, (float) filter.getUncStop());
        return _xuan(state);
    }

    @Override
    public void addTimeTemperatureCity(CallBack<Boolean> callBack, String... content) throws Exception {

    }

    @Override
    public void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws Exception {

    }
}
