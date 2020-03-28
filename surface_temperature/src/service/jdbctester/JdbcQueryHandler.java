package service.jdbctester;

import client.CallBack;
import service.TTCFilter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcQueryHandler {
    private List<String[]> queryTTCByFilterWithoutIndex(Connection con, TTCFilter filter) throws Exception {
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

    protected void handleQueryTTCByFilterWithoutIndex(Connection con, TTCFilter filter, CallBack<List<String[]>> callBack) {
        try {
            var re = queryTTCByFilterWithoutIndex(con, filter);
            callBack.callBack(re);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private List<String[]> $(Connection con, int time, String city) throws SQLException {
        var stop = (time + "").substring(0, 4) + "-" + (time + "").substring(4) + "-01";
        var state = con.prepareStatement("select * from city_temperature " +
                "where city = ?" +
                "and date =? ");
        state.setString(1, city);
        state.setDate(2, Date.valueOf(stop));
//        state.executeQuery();
//        return null;
        return _xuan(state);
    }

    protected void handleQueryTTCByTimeCity(Connection con, int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) {
        try {
            var list = queryTTCByTimeCity(con, timeStart, timeStop, city);
//            var list = $(con,timeStart,city);
            callBack.callBack(list);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
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

    private List<String[]> queryTTCByVagueCity(Connection con, int timeStart, int timeStop, String contained) throws Exception {
        var start = (timeStart + "").substring(0, 4) + "-" + (timeStart + "").substring(4) + "-01";
        var stop = (timeStop + "").substring(0, 4) + "-" + (timeStop + "").substring(4) + "-01";
        var state = con.prepareStatement("select * from city_temperature " +
                "where city in (select city from city_country_ll where city like ?) " +
                "and date between ? and ?");
        state.setString(1, "%" + contained + "%");
        state.setDate(2, Date.valueOf(start));
        state.setDate(3, Date.valueOf(stop));
        return _xuan(state);
    }

    protected void handleQueryCCLLByCity(Connection con, String city, CallBack<String[]> callBack) {
        try {
            var re = queryCCLLByCity(con, city);
            callBack.callBack(re);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private String[] queryCCLLByCity(Connection con, String city) throws Exception {
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

    protected void handleQueryTTCByVagueCity(Connection con, int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) {
        try {
            var re = queryTTCByVagueCity(con, timeStart, timeStop, contained);
            callBack.callBack(re);
        } catch (Exception e) {
            try {
                callBack.err(e);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private List<String[]> queryTTCByTimeCity(Connection con, int timeStart, int timeStop, String city) throws Exception {
        var start = (timeStart + "").substring(0, 4) + "-" + (timeStart + "").substring(4) + "-01";
        var stop = (timeStop + "").substring(0, 4) + "-" + (timeStop + "").substring(4) + "-01";
        var state = con.prepareStatement("select * from city_temperature " +
                "where city = ?" +
                "and date between ? and ?");
        state.setString(1, city);
        state.setDate(2, Date.valueOf(start));
        state.setDate(3, Date.valueOf(stop));
//        return null;
        return _xuan(state);
    }
}
