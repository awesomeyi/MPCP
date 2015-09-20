CREATE DATABASE mpcp;

USE mpcp;

CREATE TABLE users
(
	userid int unsigned not null auto_increment primary key,
	username char(50) not null,
	password char(64) not null,
	authcode char(64)
);

CREATE TABLE auth
(
	sid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	authcode char(64),
	expire DATETIME 
);

CREATE TABLE cellphones
(
	cellid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	cellnumber char(10) not null
);

CREATE TABLE banks
(
	bankid int unsigned not null auto_increment primary key,
	bankname char(50) not null,
	routing char(9) not null
);

CREATE TABLE accounts
(
	accountid int unsigned not null auto_increment primary key,
	bankid int unsigned not null,
	userid int unsigned not null,
	name char(50) not null,
	balance int not null
);

CREATE TABLE transfers
(
	transferid int unsigned not null auto_increment primary key,
	fromid int unsigned not null,
	toid int unsigned not null,
	amount int not null,
	fromcheck BOOL not null,
	tocheck BOOL not null,
	complete BOOL not null,
	starttime DATETIME not null,
	endtime DATETIME
);

CREATE TABLE session
(
	sessionid int unsigned not null auto_increment primary key,
	algid int unsigned not null,
	step int unsigned not null,
	symkey char(24),
	start BOOL not null,
	tempdata varchar(256),
	expire DATETIME
	terminate BOOL not null
);

CREATE TABLE kap
(
	algid int unsigned not null auto_increment primary key,
	name char(50) not null,
	length int unsigned not null
);