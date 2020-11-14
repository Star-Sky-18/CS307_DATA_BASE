create trigger people_id_check
    before insert or update
    on people
    for each row
execute procedure valid_check();