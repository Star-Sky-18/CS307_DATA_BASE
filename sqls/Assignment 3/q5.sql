with chrs as(
         select district, substr(chinese_name,1,1) as chr, count(*) as cnt from stations
         where district is not null
           and district <> ''
         group by (district, substr(chinese_name,1,1))
         )
select district, chr, cnt from chrs
where (district,cnt) in (
select district ,max(cnt) from chrs
    group by district
)
order by district, chr;