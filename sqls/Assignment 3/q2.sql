select district, count(ld.station_id) as number,rank() over(order by count(ld.station_id)) from stations
    join line_detail ld on stations.station_id = ld.station_id
                            and ld.line_id = 1
group by district;