ALTER TABLE RESOURCE ADD ( size bigint, downloaded_bytes bigint, last_update timestamp );

update SCHEMA_version set VERSION_NUM = '1.1';