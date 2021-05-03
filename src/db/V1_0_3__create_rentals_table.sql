CREATE TABLE Rentals (
  rental_id int NOT NULL AUTO_INCREMENT,
  car_id int NOT NULL,
  client_id int NOT NULL,
  rental_price decimal(6, 2) NOT NULL,
  duration int NOT NULL,
  PRIMARY KEY (rental_id),
  CONSTRAINT `FK_RENTAL_CAR` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_RENTAL_CLIENT` FOREIGN KEY (`client_id`) REFERENCES `clients` (`client_id`) ON DELETE CASCADE
);
