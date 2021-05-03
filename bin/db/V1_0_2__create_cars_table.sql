CREATE TABLE Cars (
  car_id int NOT NULL AUTO_INCREMENT,
  brand varchar(20) NOT NULL,
  model varchar(20) NOT NULL,
  color varchar(20) NOT NULL,
  price_per_day decimal(8,2) NOT NULL,
  PRIMARY KEY (car_id)
  );
