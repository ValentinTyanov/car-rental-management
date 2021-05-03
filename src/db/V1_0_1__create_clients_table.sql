CREATE TABLE Clients (
  client_id int NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  surname varchar(20) NOT NULL,
  sex varchar(6) NOT NULL,
  age int NOT NULL,
  salary decimal(6,2) NOT NULL,
  PRIMARY KEY (client_id)
);