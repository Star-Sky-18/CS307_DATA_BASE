select  (case when country in ('kr','hk') then surname ||' '||coalesce(first_name,' ')
    else coalesce(first_name,'')||' '|| surname end) as director
from people p
join credits c on p.peopleid = c.peopleid
join movies m on c.movieid = m.movieid
where m.country in ('kr','hk','gb','ph')
and c.credited_as = 'D'
and m.year_released = 2016
order by director;