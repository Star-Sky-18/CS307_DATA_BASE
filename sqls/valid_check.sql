create or replace function valid_check()
    returns trigger language plpgsql
as
$$
declare
    citys varchar[7] := ARRAY ['GUANG ZHOU','SHEN ZHEN'
        ,'ZHU HAI', 'SHAN TOU', 'FO SHAN','SHAO GUAN','ZHAN JIANG'];
begin
    if new.car_num similar to '粤[A-G][DF]?[A-Z0-9]{5}'
    then
        new.city := citys[ascii(substr(new.car_num, 2, 1)) - 64];
        if length(new.car_num) = 8
        then
            new.color := 'GREEN';
        else
            new.color := 'BLUE';
        end if;
        if new.year_no similar to '201[789]'
        then
            return new;
        else
            return null;
        end if;
    else
        return null;
    end if;
end
$$