create table categories (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    name varchar(160) not null unique
);

create table pricing (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    price decimal(12, 2) not null,
    constraint chk_pricing_price_non_negative check (price >= 0)
);

create table inventory (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    quantity integer not null,
    constraint chk_inventory_quantity_non_negative check (quantity >= 0)
);

create table products (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    name varchar(220) not null,
    category_id uuid not null,
    pricing_id uuid not null,
    inventory_id uuid not null,
    constraint fk_products_category foreign key (category_id) references categories(id),
    constraint fk_products_pricing foreign key (pricing_id) references pricing(id),
    constraint fk_products_inventory foreign key (inventory_id) references inventory(id)
);

create index idx_products_category_id on products(category_id);

create table customer_orders (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    total_price decimal(12, 2) not null,
    user_id uuid not null,
    purchased_at timestamp not null
);

create index idx_customer_orders_purchased_at on customer_orders(purchased_at);

create table order_items (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    order_id uuid not null,
    product_id uuid not null,
    price decimal(12, 2) not null,
    quantity integer not null,
    constraint chk_order_items_quantity_positive check (quantity > 0),
    constraint fk_order_items_order foreign key (order_id) references customer_orders(id) on delete cascade,
    constraint fk_order_items_product foreign key (product_id) references products(id) on delete cascade
);

create index idx_order_items_order_id on order_items(order_id);
create index idx_order_items_product_id on order_items(product_id);

create table product_clicks (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    product_id uuid not null,
    user_id uuid,
    clicked_at timestamp not null,
    constraint fk_product_clicks_product foreign key (product_id) references products(id) on delete cascade
);

create index idx_product_clicks_product_id on product_clicks(product_id);
create index idx_product_clicks_clicked_at on product_clicks(clicked_at);

create table scores (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    product_id uuid not null,
    window_days integer not null,
    score integer not null,
    constraint chk_scores_window_positive check (window_days > 0),
    constraint fk_scores_product foreign key (product_id) references products(id) on delete cascade,
    constraint uk_scores_product_window unique (product_id, window_days)
);

create index idx_scores_score on scores(score desc);
