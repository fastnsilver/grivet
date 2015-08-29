CREATE TABLE users (
	id INT ${autoInc},
    name VARCHAR(255) ${varcharQualifier} NOT NULL,
    login VARCHAR(75) ${varcharQualifier} NOT NULL UNIQUE,
    password VARCHAR(75) ${varcharQualifier} NOT NULL,
    PRIMARY KEY (id)
) ${createTableSuffix};

CREATE TABLE role (
	id INT ${autoInc},
    name VARCHAR(50) ${varcharQualifier} NOT NULL,
    PRIMARY KEY (id)
) ${createTableSuffix};

CREATE TABLE user_role (
	user_id INT NOT NULL,
	role_id INT NOT NULL,
	PRIMARY KEY (user_id, role_id)
) ${createTableSuffix};

insert into users(id, name, login, password) values (1,'Administrator','admin','$2a$10$2KoEwaAWjjqWjwlj/zDgCOAKPtjbvbRkxow0nW3nbv0AxRfV2SRiu');
insert into users(id, name, login, password) values (2,'User','user','$2a$10$3ufGTyMkvhgaflIgiTZ0uO7pNGsx2/MDFOWU8fS5P3GVcRnMv9V2S');

insert into role(id, name) values (1,'ROLE_USER');
insert into role(id, name) values (2,'ROLE_ADMIN');

insert into user_role(user_id, role_id) values (1,1);
insert into user_role(user_id, role_id) values (1,2);
insert into user_role(user_id, role_id) values (2,1);
