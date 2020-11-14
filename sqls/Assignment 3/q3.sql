select district, count(distinct line_id) as number, rank() over (order by count(distinct line_id) desc ) as rank
from line_detail
         join stations s on line_detail.station_id = s.station_id
where district <> ''
and district is not null
group by district;
