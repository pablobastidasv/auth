create table user_states(
  id varchar(10) not null,
  description varchar(100) not null,
  primary key (id)
);

alter table users
  add column state varchar(20) default 'CREATED';

insert into user_states values ('CREATED', 'User just created, not active yet.');
insert into user_states values ('ACTIVE', 'User ready to be loged in into the system.');
insert into user_states values ('BLOCKED', 'User has been removed from the system.');
insert into user_states values ('CHANGE_PWD', 'User has requested a password change.');

alter table users
  add constraint fk_user_state
  foreign key (state)
  references user_states(id);