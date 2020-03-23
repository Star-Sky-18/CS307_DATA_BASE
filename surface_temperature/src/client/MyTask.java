package client;

import dataCollection.allinram.CityCountryLL;
import dataCollection.allinram.CityTemperature;
import dataCollection.allinram.DataCollection;
import service.rmiserver.FileService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

class MyTask implements Task<Set<Double>> {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Double> run(FileService data) {
        try {
//            var mapTable = (DataCollection.Table) data.getMap("city_temperature");
//        var setTable = (DataCollection.Table) data.getSet("city_country_ll");
//            var map = (Map<String, CityTemperature>) mapTable.getContent();
//        var set = (Set<CityCountryLL>) setTable.getContent();
//        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        var s = set.stream().filter(cityCountryLL -> cityCountryLL.getCountry().contains("A"))
//                .map(c->c.getCity()+" "+c.getCountry()+" "+c.getLongitude()+" "+c.getLatitude())
//                .collect(Collectors.joining("\n"));
//            var s = map.values().stream().filter(cityTemperature -> cityTemperature.getCityName().contains("ga"))
//                    .map(CityTemperature::getAvgTemperature).collect(Collectors.toUnmodifiableSet());
//            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(DataCollection data) {
        data.createMap("city_temperature", CityTemperature.class);
        data.createSet("city_country_ll", CityCountryLL.class);

        var mapTable = (DataCollection.Table) data.getMap("city_temperature");
        var setTable = (DataCollection.Table) data.getSet("city_country_ll");
        var map = (Map<String, CityTemperature>) mapTable.getContent();
        var set = (Set<CityCountryLL>) setTable.getContent();
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        var i = 0;
        try (var reader = new BufferedReader(new FileReader("txts/GlobalLandTemperaturesByCity.csv"))) {
            var line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                var content = line.split(",");
                var date = LocalDate.parse(content[0], format);
                var avg = content[1].length() > 0 ? Double.parseDouble(content[1]) : null;
                var avgUn = content[2].length() > 0 ? Double.parseDouble(content[2]) : null;
                var city = content[3];
                var country = content[4];
                var latitude = content[5];
                var longitude = content[6];
                map.put(date.format(format) + "|" + city, new CityTemperature(date, city, avg, avgUn));
                var cityCountryLL = new CityCountryLL(city, country, longitude, latitude);
                set.add(cityCountryLL);
                if (++i % 500000 == 0) {
                    System.out.println(i);
                    System.gc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
////        var df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        map.put("GZ|2000-00-00",new CityTemperature(LocalDate.parse("2000/01/01",df),"GZ",11,6));
}
