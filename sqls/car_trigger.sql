create trigger car_trigger
    before insert on cars
    for each row
    execute procedure valid_check();