select line_id, ld.station_id as station, count(bl.bus_line)as number, rank() over(partition by line_id order by count(bl.bus_line) desc)
       from stations
    join line_detail ld on stations.station_id = ld.station_id
    join bus_lines bl on stations.station_id = bl.station_id
    group by (line_id,ld.station_id)
    having count(bl.bus_line) >= 10
offset 15 limit 10;