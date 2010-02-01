create table CUSTOMER (ID bigint not null, NAME varchar(50) not null, UUID varchar(50) not null, COMMENTS varchar(512), ACTIVE smallint not null, ADMIN_LICENSES_PURCHASED integer not null, PLAYER_LICENSES_PURCHASED integer not null, SUPPORT_EXPIRY_DATE timestamp not null, ROLE varchar(40) default 'SUPPORTED_CLIENT', primary key (ID));
-- Schema version
create table SCHEMA_VERSION (VERSION_NUM varchar(10) not null);
insert into SCHEMA_version(VERSION_NUM) values ('1.0');