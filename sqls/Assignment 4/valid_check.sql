create or replace function valid_check()
    returns trigger
    language plpgsql
as
$$
declare
    pro_num   varchar(2);
    city_num  varchar(2);
    block_num varchar(2);
    check_num int := 0 ;
    i         int := 1;
begin
    if new.id not similar to '[0-9]{17}[0-9X]'
    then
        raise EXCEPTION 'invalid id char sequence';
    end if;
    pro_num := substr(new.id, 1, 2);
    city_num := substr(new.id, 3, 2);
    block_num := substr(new.id, 5, 2);
    new.address := string_agg(x.name, ',')
                   from (
                            select name
                            from district
                            where code in
                                  (pro_num || '0000', pro_num || city_num || '00', pro_num || city_num || block_num)
                            order by code
                        ) x;
    if new.address is null
    then
        raise EXCEPTION 'invalid address code';
    end if;
    if substr(new.id, 7, 8)::date < '19000101'::date
    then
        raise EXCEPTION 'invalid birthday';
    end if;
    new.birthday :=  substr(new.id, 7, 8);
    while i < 18
        loop
            check_num := check_num + (((1 << (18 - i)) % 11) * cast(substr(new.id, i, 1) as integer));
            i := i + 1;
        end loop;
    check_num := (12 - (check_num % 11)) % 11;
    if (check_num = 10 and substr(new.id, 18, 1) = 'X')
    then
        return new;
    elseif check_num = cast(substr(new.id, 18, 1) as integer)
    then
        return new;
    else
        raise EXCEPTION 'check fail, should be %',cast(check_num as varchar);
    end if;
end;
$$;
