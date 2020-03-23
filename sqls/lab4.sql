select year_released as year, count(*) as count
from (
     select year_released, movieid from movies
        where country = 'cn' and year_released>=1960
     ) chinese_movie group by year_released;
