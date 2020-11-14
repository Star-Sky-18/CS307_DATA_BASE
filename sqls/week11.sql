create or replace function get_city(car_num varchar)
    returns varchar
    language plpgsql
as
$$
declare
    citys       varchar[7] := ARRAY ['GUANG ZHOU','SHEN ZHEN'
        ,'ZHU HAI', 'SHAN TOU', 'FO SHAN','SHAO GUAN','ZHAN JIANG'];
    ASCII_BEGIN int        := 64;
    city_num    int;
begin
    if not ((length(car_num) = 8 and substr(car_num, 3, 1) similar to '[DF]')
        or length(car_num) = 7)
    then
        return 'Invalid plate Length';
    end if;

    city_num := ascii(substr(car_num, 2, 1)) - ASCII_BEGIN;
    if substr(car_num, 1, 1) = '粤'
    then
        if city_num <= 7
        then
            if (length(car_num) = 7
                and substr(car_num, 3, 5) similar to '[0-9A-Z]{5}')
                or (length(car_num) = 8 and substr(car_num, 3, 1) similar to '[DF]'
                    and substr(car_num, 4, 5) similar to '[0-9A-Z]{5}')
            then
                return citys[city_num];
            else
                return 'Invalid car number!';
            end if;
        else
            return 'Invalid city!';
        end if;
    else
        return 'Invalid province!';
    end if;
end
$$