ALTER TABLE RESOURCE ADD COLUMN size bigint; 
ALTER TABLE RESOURCE ADD COLUMN downloaded_bytes bigint; 
ALTER TABLE RESOURCE ADD COLUMN last_update timestamp;

update SCHEMA_version set VERSION_NUM = '1.1';