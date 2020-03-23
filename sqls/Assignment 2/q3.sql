select round(100.0 * sum(case when country = 'us' then 1 else 0 end) / count(*), 2) as us_percent
from movies
where year_released between 1970 and 1979;