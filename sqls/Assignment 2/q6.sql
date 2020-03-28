select count(distinct p.peopleid) as count
from people p
         join credits c on p.peopleid = c.peopleid
where c.movieid in (
    select c.movieid
    from credits c
             join people p on c.peopleid = p.peopleid
    where p.surname = 'Liu'
      and p.first_name = 'Yifei'
    and c.credited_as = 'A'
)
  and (p.first_name <> 'Yifei'
    or p.surname <> 'Liu')
  and c.credited_as = 'A';