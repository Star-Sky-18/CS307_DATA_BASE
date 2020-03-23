-- 1
select *
from movies
where country in ('pt', 'br');

-- 2
select *
from movies
where year_released = 2000
  and country = 'us';

-- 3
select *
from movies
where (title like '%a%' or
       title like '%A%' or
       title like '%O%' or
       title like '%o%')
  and country = 'sp';

-- 4
select *
from movies
where country in ('cn', 'hk', 'tw', 'mo')
  and year_released between 1940 and 1949;

-- 5
select *
from people
where born <= 1920
  and died is null;

-- 6
select title
from alt_titles
where title like '%巴山夜雨%';

--7
select title
from alt_titles
where upper(title) like '%man%';

--8
select first_name, surname
from people as p
where p.died - p.born >= 100;

--9
select first_name, surname
from people as p
where p.died - p.born > 100
   or (p.died is null and p.born <= 1920);

--10
select *
from people
where surname like '%"%';

--11
select *
from countries
where continent = 'EUROPE'
  and country_code like 'c%';

--12
select *
from people as p
where p.surname like substr(p.first_name, 1, 1) || '%';

--13
select 2020 - max(born)
from people;

--14
select country, count(*)
from movies
where country like 'm%'
group by country;

--15
select count(distinct country)
from movies;

--16
select min(year_released)
from movies
where country in ('cn', 'tw', 'hk');

--17
select count(*)
from movies
where year_released = 2010;

--18
select year_released, count(*)
from movies
where year_released >= 1960
group by year_released;

--19
select count(*)
from movies
where country = 'gb'
  and year_released = 1949;

--20
select dn, count(movieid) films
from (select movieid, count(*) dn
      from credits
      where credited_as = 'D'
      group by movieid) d_cnt
group by dn;

--21
select count(*) as recorded, count(*) - count(died) as alive, count(died) as died
from people;

--22
select max(cnt)
from (select surname, count(*) as cnt
      from people
      group by surname) s_cnt;

--23
select count(*)
from (
         select peopleid
         from credits
         where credited_as in ('A', 'D')
         group by movieid, peopleid
         having count(*) = 2) pl;

--24
select round(100 * sum(case gender when 'F' then 1 else 0 end) / count(*), 1) p_w
from people;

--25
select country, count(*) from movies
where runtime >= 180
group by country;