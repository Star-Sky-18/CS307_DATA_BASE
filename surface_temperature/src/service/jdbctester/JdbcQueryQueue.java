package service.jdbctester;

import client.CallBack;
import service.QueryableQueue;
import service.TTCFilter;

import java.sql.*;
import java.util.List;

public class JdbcQueryQueue implements QueryableQueue {
    Connection con;
    String user, password;
    boolean autoClose;
    JdbcQueryHandler handler;

    public JdbcQueryQueue(String user, String password, boolean autoClose, JdbcQueryHandler handler) {
        this.user = user;
        this.password = password;
        this.autoClose = autoClose;
        this.handler = handler;
        connect();
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cs307", user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JdbcQueryQueue(String user, JdbcQueryHandler handler) {
        this(user, "123456", true, handler);
    }

    @Override
    public void queryTTCByTimeCity(int timeStart, int timeStop, String city, CallBack<List<String[]>> callBack) {
        JdbcQueryExecutor.getExecutor().execute(() -> {
            try {
                if (con.isClosed())
                    connect();
                handler.handleQueryTTCByTimeCity(con, timeStart, timeStop, city, callBack);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (autoClose)
                    close();
            }
        });
    }


    @Override
    public void queryCCLLByCity(String city, CallBack<String[]> callBack) {
        JdbcQueryExecutor.getExecutor().execute(() -> {
            try {
                if (con.isClosed())
                    connect();
                handler.handleQueryCCLLByCity(con, city, callBack);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (autoClose)
                    close();
            }
        });
    }

    @Override
    public void queryTCCByVagueCity(int timeStart, int timeStop, String contained, CallBack<List<String[]>> callBack) {
        JdbcQueryExecutor.getExecutor().execute(() -> {
            try {
                if (con.isClosed())
                    connect();
                handler.handleQueryTTCByVagueCity(con, timeStart, timeStop, contained, callBack);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (autoClose)
                    close();
            }
        });
    }


    @Override
    public void queryTTCByFilterWithoutIndex(TTCFilter filter, CallBack<List<String[]>> callBack) {
        JdbcQueryExecutor.getExecutor().execute(() -> {
            try {
                if (con.isClosed())
                    connect();
                handler.handleQueryTTCByFilterWithoutIndex(con, filter, callBack);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (autoClose)
                    close();
            }
        });
    }


    @Override
    public void addTimeTemperatureCity(CallBack<Boolean> callBack, String... content) throws Exception {
        callBack.callBack(false);
    }

    @Override
    public void addCityCountryLL(CallBack<Boolean> callBack, String... content) throws Exception {
        callBack.callBack(false);
    }
}
