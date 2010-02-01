create table CUSTOMER (ID bigint not null, NAME varchar(50) not null, UUID varchar(50) not null, COMMENTS varchar(512), ACTIVE smallint not null, ADMIN_LICENSES_PURCHASED integer not null, PLAYER_LICENSES_PURCHASED integer not null, SUPPORT_EXPIRY_DATE timestamp not null, primary key (ID));
create table CUSTOMER_ROLE (UUID varchar(50) not null, ROLE varchar(40) default 'SUPPORTED_CLIENT');
