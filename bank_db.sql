drop database banktrackerdb;
drop user banktracker;
create user banktracker with password 'password';
create database banktrackerdb with template=template0 owner=banktracker;

\connect banktrackerdb;
alter default privileges grant all on tables to banktracker;
alter default privileges grant all on sequences to banktracker;

create table bt_employees(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
phone text not null,
password text not null,
role text,
is_deleted boolean default false,
created_at timestamp with time zone DEFAULT now(),
last_updated_at timestamp with time zone DEFAULT now()
);

create table bt_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
phone text not null,
created_by integer not null,
password text not null,
current_account_number integer default null,
saving_account_number integer default null,
loan_account_number integer default null,
salary_account_number integer default null,
kyc_status text ,
adhaar_number text,
is_deleted boolean DEFAULT false,
created_at timestamp with time zone DEFAULT now(),
last_updated_at timestamp with time zone DEFAULT now()
);

alter table bt_users add constraint emp_users_fk
foreign key (created_by) references bt_employees(user_id);

create table bt_accounts(
account_number integer primary key not null,
user_id integer not null,
type text not null,
current_balance numeric(11,3) not null,
is_deleted boolean DEFAULT false,
created_at timestamp with time zone DEFAULT now(),
last_updated_at timestamp with time zone DEFAULT now()
);
create sequence bt_employees_seq increment 1 start 1;
create sequence bt_users_seq increment 1 start 1;
create sequence bt_accounts_seq increment 1 start 1;

