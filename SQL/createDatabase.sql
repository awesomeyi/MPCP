CREATE DATABASE mpcp;

USE mpcp;

CREATE TABLE users
(
	userid int unsigned not null auto_increment primary key,
	username char(50) not null,
	password char(50) not null
);

CREATE TABLE cellphones
(
	cellid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	cellnumber char(10) not null,
	carrier char(50) not null
);

CREATE TABLE banks
(
	bankid int unsigned not null auto_increment primary key,
	bankname char(50) not null,
	routing char(9) not null
);

CREATE TABLE account
(
	accountid int unsigned not null auto_increment primary key,
	bankid int unsigned not null,
	userid int unsigned not null,
	balance int not null
);