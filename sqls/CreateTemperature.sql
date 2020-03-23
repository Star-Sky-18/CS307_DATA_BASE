drop table if exists city_temperature;
drop table if exists city_country_ll;
create table if not exists city_country_ll
(
    city varchar(60) not null unique,
    country varchar(45) not null ,
    longitude varchar(10) not null ,
    latitude varchar(10) not null,
    primary key (city,longitude,latitude)
);
create table city_temperature
(
    date date not null,
    average_temperature real,
    average_temperature_uncertainty real,
    city varchar(60) not null,
    primary key (date,city),
    foreign key (city) references city_country_ll(city)
);
create index