ALTER TABLE CONTENT ADD ( size bigint );

update SCHEMA_version set VERSION_NUM = '1.1';