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

-- SITES
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (1, '2ος όροφος', 'Στο μηχανοστάσιο', 1, 1);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (2, '4ος όροφος', 'Χρειάζονται διαφημίσεις για κασμάδες ξυσίματος', 1, 1);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (3, 'Κουζίνα', 'Δείχνουμε διαφημίσεις φαγητού', 1, 1);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (4, 'Κηφισίας', 'Υποκ/μα Κηφισίας 112', 1, 2);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (5, 'Νίκαια', 'Δυτική Όχθη του Ιορδάνη', 1, 2);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (6, 'Θεραπευτήριο', 'Διαφημίσεις για Κολυμβήθρα του Σιλωάμ', 1, 2);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (7, 'Headquarters New York', '1000 viewers per minute', 1, 3);
insert into SITE (ID, NAME, COMMENTS, ACTIVE, CUSTOMER_ID) values (8, 'Athens branch', 'Located downtown', 1, 3);

-- INSTALLATIONS
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (1, 'Αίθουσα 1', 'ADFG3245GTF23GHVS', 'Τη βλέπουν όλοι οι developers', 1, 0, 1);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (2, 'Αίθουσα 1', 'ADFG3FGTGTF2323VS', 'Managers and testers', 1, -1, 2);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (3, 'Αίθουσα 2', 'ASERWE45GTF23GHVS', 'Επισκέπτες', 1, 0, 2);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (4, 'Μαγέρικο', 'ADFG3245GTF231234', 'Μάγειρες κ λαντζέρηδες', 1, 0, 3);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (5, 'Εστιατόριο', 'ADFG3245GTF45678VS', 'Κανένα σχόλιο από εμάς', 1, -2, 3);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (6, '1ος όροφος', 'ADFG3245G0987HVS', 'Τμήμα επειγόντων περιστατικών', 1, 0, 4);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (7, '2ος όροφος', '33456245GTF23GHVS', 'Πολλά προβλήματα με ρεύμα', 1, -2, 4);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (8, '3ος όροφος', 'ADFG32666TF23GHVS', 'Μαιευτήριο', 1, 0, 4);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (9, 'Ισόγειο', 'ADFG32666VCXZHVS', 'Εξωτερικά ιατρεία', 1, 0, 5);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (10, 'Υπόγειο', 'AD0987666TF23GHVS', 'Πάρκιγκ κτιρίου', 1, 0, 5);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (11, 'Ισόγειο', 'ADFG32666TF239999', 'Εξωτερικά ιατρεία', 1, 0, 6);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (12, '1ος όροφος', 'ADFG32666T8888HVS', 'Θάλαμοι ασθενών', 1, -2, 6);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (13, 'Κύρια είσοδος', 'ADF555566TF23GHVS', 'Απέναντι από την κύρια είσοδο', 1, 0, 7);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (14, 'Ασανσέρ', 'A22222666TF23GHVS', 'Μπροστά από τα σανσέρ', 1, 0, 7);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (15, '1ος όροφος', 'ADFG329999F23GHVS', 'Πηγαίνοντας προς το λογιστήριο', 1, -1, 7);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (16, 'Ισόγειο', 'ADFG32666TF235656', 'Αίθουσα αναμονής', 1, 0, 8);
insert into INSTALLATION (ID, NAME, UUID, COMMENTS, ACTIVE, STATUS, SITE_ID) values (17, 'Κατάστημα', 'ADFG32666T2345HVS', 'Δίπλα στα ταμεία', 1, 0, 8);

-- GROUPS
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (1, 'Ένα γκρουπ', 'Δοκιμαστικό group', 1);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (2, 'Τυχαίοι players', 'Κάποιες εγκαταστάσεις στην τύχη', 1);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (3, 'Μητέρες', 'Μητέρες κ συγγενείς τους', 2);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (4, 'Νέοι', 'Μέσος όρος ηλικίας κάτω των 40', 2);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (5, 'Πελάτες', 'Διάφοροι πελάτες της εταιρείας', 3);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (6, 'Υπάλληλοι', 'Υπάλληλοι της εταιρείας', 3);
insert into INST_GROUP (ID, NAME, COMMENTS, CUSTOMER_ID) values (7, 'Επισκέπτες', 'Περαστικοί κ επισκέπτες', 3);

-- DEPLOYMENTS-to-GROUPS
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (1, 1);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (2, 1);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (3, 2);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (4, 2);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (8, 3);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (10, 4);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (11, 4);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (12, 4);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (13, 5);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (15, 6);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (16, 6);
insert into GRP_INSTALLATION (INSTALLATION_ID, GROUP_ID) values (17, 7);

-- CONTENT
insert into CONTENT (ID, URL, CRC, LOCAL_FILE) values (1, 'abcd', '123', 'test.wmv');
insert into CONTENT (ID, URL, CRC, LOCAL_FILE) values (2, 'efgh', '123', 'test.jpg');
insert into CONTENT (ID, URL, CRC, LOCAL_FILE) values (3, 'ijkl', '123', 'test.mpg');

