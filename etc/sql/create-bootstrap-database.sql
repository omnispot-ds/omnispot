CREATE TABLE DEPLOYMENT (ID bigint not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), URL varchar(512) not null, FILENAME varchar(512) not null, CRC varchar(128) not null, DEPLOY_DATE timestamp not null, FAILED_RESOURCE char not null, RETRIES int not null, primary key (ID));
CREATE TABLE RESOURCE (ID bigint not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), URL varchar(512) not null, CRC varchar(128), FILENAME varchar(512) not null, RETRIES int not null, primary key (ID)); 
CREATE TABLE PENDING (DEPLOYMENT_ID bigint not null, RESOURCE_ID bigint not null); 
CREATE TABLE ACTION (ID bigint not null, ACTION_ID varchar(50) not null, TYPE smallint not null, STATUS smallint not null, DATE_ADDED timestamp not null, MESSAGE varchar(3000), INSTALLATION_ID bigint, primary key (ID));
CREATE TABLE PARAMETER (ID bigint not null, NAME varchar(50) not null, PARAM_VALUE varchar(512) not null, ACTION_ID bigint, primary key (ID));
ALTER TABLE PENDING ADD CONSTRAINT FK_PENDING_DEPLOYMENT FOREIGN KEY (DEPLOYMENT_ID) REFERENCES DEPLOYMENT;
ALTER TABLE PENDING ADD CONSTRAINT FK_PENDING_RESOURCE FOREIGN KEY (RESOURCE_ID) REFERENCES RESOURCE;
ALTER TABLE PARAMETER add constraint FK1A96C389D4EBDE84 foreign key (ACTION_ID) references ACTION;