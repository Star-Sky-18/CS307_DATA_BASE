select count(*) as cnt
from (select peopleid
      from credits
      where credited_as = 'A'
      group by peopleid
      having count(movieid) > 30) ps;
