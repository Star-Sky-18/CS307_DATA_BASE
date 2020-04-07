select line_1.station_id as a_station from (select stations.station_id from stations
    join line_detail on stations.station_id = line_detail.station_id
where line_id = 1) as line_1
    left join line_detail on line_1.station_id = line_detail.station_id and line_detail.line_id=2
where line_id is null
order by a_station;