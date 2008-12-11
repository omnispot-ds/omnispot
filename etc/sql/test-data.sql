-- CUSTOMERS
insert into CUSTOMER (ID, NAME, COMMENTS, ACTIVE) values (1, 'Trasys', 'Πολύ απαιτητικοί zhe Germans', 1);
insert into CUSTOMER (ID, NAME, COMMENTS, ACTIVE) values (2, 'Euromedica', 'Πολλά κτίρια δεξιά-αριστερά', 1);
insert into CUSTOMER (ID, NAME, COMMENTS, ACTIVE) values (3, 'David Karamanolis', 'Τεράστια πολυεθνική, πολύ χρήμα', 1);
insert into CUSTOMER (ID, NAME, COMMENTS, ACTIVE) values (4, 'Carefour', 'Γαμώτο, τους χάσαμε από πελάτες!', 0);

-- USERS
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('pftakas', 'pftakas', 'Παυσανίας', 'Φτάκας', null);
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('sgerogia', 'sgerogia', 'Στέλιος', 'Γερογιαννάκης', null);
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('gorilas', 'gorilas', 'Στέλιος', 'Γκορίλας', 1);
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('pkattoul', 'pkattoul', 'Παύλος', 'Κάττουλας', 2);
insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CUSTOMER_ID) values ('giamour', 'giamour', 'Νίκος', 'Γιαμούρης', 3);

-- USERS_RIGHTS
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('sgerogia', 'ADMINISTRATOR');
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('gorilas', 'CONTENT_MGR');
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('pftakas', 'USER');
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('pkattoul', 'USER');
insert into USER_RIGHTS (USERNAME, ROLE_NAME) values ('giamour', 'CONTENT_MGR');

