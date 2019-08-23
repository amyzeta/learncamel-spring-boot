create table items (
    item_i serial,
    sku text not null,
    item_description text default null,
    price numeric(5,2),
    create_ts timestamptz null default current_timestamp
);

select * from items;

delete from items;