-- Hibernate schema
create table ACCESS_RIGHT (NAME varchar(50) not null, primary key (NAME));
create table ACC_CONTROL (ID bigint not null, ACC_TYPE smallint not null, USERNAME varchar(50) not null, INSTALLATION_ID bigint not null, primary key (ID));
create table ACTION (ID bigint not null, ACTION_ID varchar(50) not null, TYPE smallint not null, STATUS smallint not null, DATE_ADDED timestamp not null, MESSAGE varchar(3000), INSTALLATION_ID bigint, primary key (ID));
create table CONTENT (ID bigint not null, URL varchar(512) not null, CRC varchar(128), LOCAL_FILE varchar(512), primary key (ID));
create table CUSTOMER (ID bigint not null, NAME varchar(50) not null, COMMENTS varchar(512), ACTIVE smallint not null, primary key (ID));
create table DEPLOYMENT (ID bigint not null, URL varchar(512) not null, NAME varchar(50) not null, CRC varchar(128), LOCAL_FILE varchar(512), STATUS smallint not null, RETRIES integer not null, primary key (ID));
create table DEPL_CONTENT (CONTENT_ID bigint not null, DEPLOYMENT_ID bigint not null, primary key (DEPLOYMENT_ID, CONTENT_ID));
create table GRP_INSTALLATION (INSTALLATION_ID bigint not null, GROUP_ID bigint not null, primary key (GROUP_ID, INSTALLATION_ID));
create table INSTALLATION (ID bigint not null, NAME varchar(50) not null, UUID varchar(100) not null, SCR_TYPE varchar(50), COMMENTS varchar(512), ACTIVE smallint not null, STATUS smallint not null, SITE_ID bigint not null, primary key (ID));
create table INST_DEPLOYMENT (DEPLOYMENT_ID bigint not null, INSTALLATION_ID bigint not null, primary key (INSTALLATION_ID, DEPLOYMENT_ID));
create table INST_GROUP (ID bigint not null, NAME varchar(50) not null, COMMENTS varchar(512), CUSTOMER_ID bigint not null, primary key (ID));
create table PARAMETER (ID bigint not null, NAME varchar(512) not null, PARAM_VALUE varchar(512) not null, ACTION_ID bigint, primary key (ID));
create table SITE (ID bigint not null, NAME varchar(50) not null, COMMENTS varchar(512), ACTIVE smallint not null, CUSTOMER_ID bigint not null, primary key (ID));
create table STATUS_ENTRY (ID bigint not null, STATUS_DATE timestamp not null, STATUS smallint not null, INSTALLATION_ID bigint, primary key (ID));
create table USERS (USERNAME varchar(50) not null, PASSWORD varchar(50) not null, FIRST_NAME varchar(50) not null, LAST_NAME varchar(50) not null, CUSTOMER_ID bigint, primary key (USERNAME));
create table USER_RIGHTS (USERNAME varchar(50) not null, ROLE_NAME varchar(50) not null, primary key (USERNAME, ROLE_NAME));
alter table ACC_CONTROL add constraint FK7B45A35FECADAFAB foreign key (USERNAME) references USERS;
alter table ACC_CONTROL add constraint FK7B45A35F745A084 foreign key (INSTALLATION_ID) references INSTALLATION;
alter table ACTION add constraint FK72C27236745A084 foreign key (INSTALLATION_ID) references INSTALLATION;
alter table DEPL_CONTENT add constraint FK782AD057E7E563D0 foreign key (CONTENT_ID) references CONTENT;
alter table DEPL_CONTENT add constraint FK782AD05765DC0B24 foreign key (DEPLOYMENT_ID) references DEPLOYMENT;
alter table GRP_INSTALLATION add constraint FK73374374745A084 foreign key (INSTALLATION_ID) references INSTALLATION;
alter table GRP_INSTALLATION add constraint FK7337437468777876 foreign key (GROUP_ID) references INST_GROUP;
alter table INSTALLATION add constraint FK38C463A79AE9364 foreign key (SITE_ID) references SITE;
alter table INST_DEPLOYMENT add constraint FK1D4D165E745A084 foreign key (INSTALLATION_ID) references INSTALLATION;
alter table INST_DEPLOYMENT add constraint FK1D4D165E65DC0B24 foreign key (DEPLOYMENT_ID) references DEPLOYMENT;
alter table INST_GROUP add constraint FK85837326846A304 foreign key (CUSTOMER_ID) references CUSTOMER;
alter table PARAMETER add constraint FK1A96C389D4EBDE84 foreign key (ACTION_ID) references ACTION;
alter table SITE add constraint FK26D747846A304 foreign key (CUSTOMER_ID) references CUSTOMER;
alter table STATUS_ENTRY add constraint FK5133F9C5745A084 foreign key (INSTALLATION_ID) references INSTALLATION;
alter table USERS add constraint FK4D495E8846A304 foreign key (CUSTOMER_ID) references CUSTOMER;
alter table USER_RIGHTS add constraint FK11D4C4CBECADAFAB foreign key (USERNAME) references USERS;
alter table USER_RIGHTS add constraint FK11D4C4CBCCF58D82 foreign key (ROLE_NAME) references ACCESS_RIGHT;
-- Access rights
insert into ACCESS_RIGHT (NAME) values ('USER');
insert into ACCESS_RIGHT (NAME) values ('CONTENT_MGR');
insert into ACCESS_RIGHT (NAME) values ('ADMINISTRATOR');
-- Default user
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('admin', 'admin', 'System', 'Administrator', null);
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('admin', 'ADMINISTRATOR');
-- Schema version
create table SCHEMA_VERSION (VERSION_NUM varchar(10) not null);
insert into SCHEMA_version(VERSION_NUM) values ('1.0');

