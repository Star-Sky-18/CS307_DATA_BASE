select count(*) as count
from people p
         join credits c on p.peopleid = c.peopleid
         join movies m on c.movieid = m.movieid
where m.movieid in (
    select m.movieid
    from movies m
             join credits c on m.movieid = c.movieid
             join people p on c.peopleid = p.peopleid
    where p.surname = 'Liu'
      and p.first_name = 'Yifei'
)
and p.first_name <> 'Yifei'
and p.surname <> 'Liu'
and c.credited_as = 'A';