select coalesce(first_name,' ') || ' ' || surname as director
from (
         select c.peopleid, count( distinct country) as c_cnt
         from movies
                  join credits c on movies.movieid = c.movieid
                  join people p on c.peopleid = p.peopleid
         where c.credited_as = 'D'
         group by c.peopleid
     ) ds
         join people on ds.peopleid = people.peopleid
where c_cnt > 1;