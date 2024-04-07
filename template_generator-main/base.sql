\c template_spring;

create table poste(
     id serial primary key,
     nom varchar(20)
);

create table etudiant(
     id serial primary key,
     nom varchar(20),
     age int,
     idposte integer references poste(id)
);