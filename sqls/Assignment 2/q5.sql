with ac_cnt_2000_movie as
         (select c.movieid, count(c.peopleid) cnt
          from movies m
                   join credits c on m.movieid = c.movieid and c.credited_as = 'A'
                   join people p on c.peopleid = p.peopleid
          where p.born >= 2000
          and year_released >= 2000
          and c.credited_as='A'
          group by c.movieid)
select movieid, cnt
from ac_cnt_2000_movie
where cnt = (select max(cnt)
             from ac_cnt_2000_movie);