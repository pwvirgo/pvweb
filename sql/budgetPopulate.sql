--  populate the buget tables

delete from CAT;

insert into CAT (CAT, DESCRIP) values('food', 'Food');
insert into CAT (CAT, DESCRIP) values('grocery', 'Grocery');
insert into CAT (CAT, DESCRIP) values('eat out', 'Eat Out');


insert into CAT (CAT, DESCRIP) values('mortgage', 'Mortgage');
insert into CAT (CAT, DESCRIP) values('h maint', 'House Maintenance');
insert into CAT (CAT, DESCRIP) values('electric', 'Electric Bills');
insert into CAT (CAT, DESCRIP) values('gas', 'Gas Bills');
insert into CAT (CAT, DESCRIP) values('water', 'Water Bills');
insert into CAT (CAT, DESCRIP) values('phone', 'Smart Phone Bills');
insert into CAT (CAT, DESCRIP) values('internet', 'Internet');


insert into CAT (CAT, DESCRIP) values('clothes', 'Clothes');

insert into CAT (CAT, DESCRIP) values('med ins', 'Health Insurance');
insert into CAT (CAT, DESCRIP) values('med', 'Medical');
insert into CAT (CAT, DESCRIP) values('dental', 'Dental');
insert into CAT (CAT, DESCRIP) values('vision', 'Vision');


insert into CAT (CAT, DESCRIP) values('art sup', 'Art Supplies');
insert into CAT (CAT, DESCRIP) values('news', 'News');

insert into CAT (CAT, DESCRIP) values('music', 'music');
insert into CAT (CAT, DESCRIP) values('books', 'Books');

insert into CAT (CAT, DESCRIP) values('giving', 'Giving');
insert into CAT (CAT, DESCRIP) values('pension', 'Pension');

insert into CAT (CAT, DESCRIP) values('unknown', 'Unknown');
insert into CAT (CAT, DESCRIP) values('travel', 'Travel');

insert into ACCOUNT (ACCOUNT, DESCRIP) values ('cash', 'Cash' );
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('SFFCU', 'Signal Financial FCU' );
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('MAFCU', 'Mid-Atlanic FCU');
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('chase' ,'Chase Visa' );
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('discover' ,'Discover Card' );
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('amex' ,'American Express' );
insert into ACCOUNT (ACCOUNT, DESCRIP) values ('bofaV', 'Bank America Visa' );
