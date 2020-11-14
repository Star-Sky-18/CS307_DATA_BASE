select line_id, ld.station_id, count(bl.bus_line)as cnt, rank() over(partition by line_id order by count(bl.bus_line) desc) as rank
       from stations
    join line_detail ld on stations.station_id = ld.station_id
    join bus_lines bl on stations.station_id = bl.station_id
    group by (line_id,ld.station_id)
    having count(bl.bus_line) >= 10
order by line_id,count(bl.bus_line),ld.station_id desc
offset 15 limit 10;