
create database testBank;
use testBank;

create table accounts
(
    accountid int primary key auto_increment,
    balance   numeric(10,2)
);

create table oprecord
(
    id int primary key auto_increment,
    accountid int references accounts(accountid),
    opmoney numeric(10,2),
    optime datetime,
    optype enum('deposite','withraw','transfer') not null,
    transferid varbinary(50)
);

select * from accounts;
