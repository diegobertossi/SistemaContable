-- MySQL dump 10.13  Distrib 8.4.8, for Win64 (x86_64)
--
-- Host: localhost    Database: facturacion_db_brc
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'CUIT',
  `nro_documento` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `razon_social` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `condicion_iva` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `domicilio` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `origen` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'manual',
  `els_referencia` int DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `tipo_persona` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'empresa',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_documento` (`tipo_documento`,`nro_documento`),
  KEY `idx_razon_social` (`razon_social`),
  KEY `idx_nro_documento` (`nro_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'DNI','147','Abel Sarac','Consumidor Final','','12345678','','reparsoft',147,1,'particular','2026-06-21 15:48:29'),(2,'DNI','22','Abuelo Ian','Consumidor Final','','654654654','juan@gmail.com','reparsoft',22,1,'particular','2026-06-21 15:48:29'),(3,'DNI','70','Adriana (Placa clasera)','Consumidor Final','','12345678','','reparsoft',70,1,'particular','2026-06-21 15:48:29'),(4,'DNI','27','Aguirre (Inversor Enertik)','Consumidor Final','pedro','1234','','reparsoft',27,1,'particular','2026-06-21 15:48:29'),(5,'DNI','146','Alberto ( Inversor Daza )','Consumidor Final','pepito','1234','','reparsoft',146,1,'particular','2026-06-21 15:48:30'),(6,'DNI','139','Alejandro','Consumidor Final','','123','','reparsoft',139,1,'particular','2026-06-21 15:48:30'),(7,'DNI','101','Alejandro Azocar','Consumidor Final','','1135878598','','reparsoft',101,1,'particular','2026-06-21 15:48:30'),(8,'Otro','155','Alejandro Perret','IVA Responsable Inscripto','','','','reparsoft',155,1,'empresa','2026-06-21 15:48:30'),(9,'CUIT','30711621942','Alma Del Lago (Almasur S.A.)','IVA Responsable Inscripto','Av. Bustillo km 1,151','','abastecimiento@almasuites.com.ar','reparsoft',19,1,'empresa','2026-06-21 15:48:30'),(10,'DNI','50','Andrés Barresi','Consumidor Final','','123456; 789789; 96589','','reparsoft',50,1,'particular','2026-06-21 15:48:30'),(11,'Otro','12','Andrés Gallardo (X28)','IVA Responsable Inscripto','','','','reparsoft',12,1,'empresa','2026-06-21 15:48:30'),(12,'Otro','34','Ariel (Placa lavarropas)','IVA Responsable Inscripto','','','','reparsoft',34,1,'empresa','2026-06-21 15:48:30'),(13,'Otro','84','Ariel (Soldadoras)','IVA Responsable Inscripto','','123123','juan@pepe.com; pepito@gmai.com; pedesa','reparsoft',84,1,'empresa','2026-06-21 15:48:30'),(14,'Otro','18','Ariel Lotito','IVA Responsable Inscripto','','','','reparsoft',18,1,'empresa','2026-06-21 15:48:30'),(15,'Otro','121','Ariel Silva','IVA Responsable Inscripto','','','','reparsoft',121,1,'empresa','2026-06-21 15:48:30'),(16,'Otro','45','Arturo Ruiz (Comallo)','IVA Responsable Inscripto','','','','reparsoft',45,1,'empresa','2026-06-21 15:48:30'),(17,'Otro','11','Ascensores Bariloche','IVA Responsable Inscripto','','','barilocheascensores@gmail.com','reparsoft',11,1,'empresa','2026-06-21 15:48:30'),(18,'CUIT','20263643810','Ascensores Basaldua','IVA Responsable Inscripto','','','elevatec@yahoo.com.ar','reparsoft',143,1,'empresa','2026-06-21 15:48:30'),(19,'CUIT','30706433585','Ascensores Lucero','IVA Responsable Inscripto','Las Violetas 1215, San Carlos de Bariloche, RÃ­o Negro','542944425706','luceroasc@speedy.com.ar','reparsoft',1,1,'empresa','2026-06-21 15:48:30'),(20,'Otro','77','Ascensores Patagonia','IVA Responsable Inscripto','','','','reparsoft',77,1,'empresa','2026-06-21 15:48:30'),(21,'CUIT','30632773354','Asociación Club Los Pehuenes','IVA Responsable Inscripto','Pintores Argentinos 250','','','reparsoft',13,1,'empresa','2026-06-21 15:48:30'),(22,'Otro','112','Bernardo Moreno','IVA Responsable Inscripto','','','','reparsoft',112,1,'empresa','2026-06-21 15:48:30'),(23,'Otro','141','Boletti Ana','IVA Responsable Inscripto','','','','reparsoft',141,1,'empresa','2026-06-21 15:48:30'),(24,'Otro','36','C.U.B.A Sede Cerro Catedral','IVA Responsable Inscripto','Del Alto 811, San Carlos de Bariloche, Río Negro','','','reparsoft',36,1,'empresa','2026-06-21 15:48:30'),(25,'Otro','75','Camila Frabre León','IVA Responsable Inscripto','','','','reparsoft',75,1,'empresa','2026-06-21 15:48:30'),(26,'Otro','161','Capitalinas Bariloche','IVA Responsable Inscripto','','','','reparsoft',161,1,'empresa','2026-06-21 15:48:30'),(27,'Otro','162','Carlos Cubau','IVA Responsable Inscripto','','','','reparsoft',162,1,'empresa','2026-06-21 15:48:30'),(28,'Otro','134','Carlos Lamuniere','IVA Responsable Inscripto','','','','reparsoft',134,1,'empresa','2026-06-21 15:48:30'),(29,'Otro','60','Carlos Zabala','IVA Responsable Inscripto','','','','reparsoft',60,1,'empresa','2026-06-21 15:48:30'),(30,'CUIT','30712195890','Cerveceria Kunstmann','IVA Responsable Inscripto','','','','reparsoft',153,1,'empresa','2026-06-21 15:48:30'),(31,'Otro','104','Cesar (Inversor Daza)','IVA Responsable Inscripto','','','','reparsoft',104,1,'empresa','2026-06-21 15:48:30'),(32,'Otro','44','Christian Steverlynck','IVA Responsable Inscripto','','','','reparsoft',44,1,'empresa','2026-06-21 15:48:30'),(33,'CUIT','30672972961','Colegio San Esteban Bariloche','IVA Responsable Inscripto','','','','reparsoft',43,1,'empresa','2026-06-21 15:48:30'),(34,'Otro','31','Concejo Municipal de Bariloche','IVA Responsable Inscripto','','','','reparsoft',31,1,'empresa','2026-06-21 15:48:30'),(35,'Otro','38','Connie Santana','IVA Responsable Inscripto','','','','reparsoft',38,1,'empresa','2026-06-21 15:48:30'),(36,'Otro','39','Convertec-Energías Renovables Bariloche','IVA Responsable Inscripto','Rivadavia 571. San Carlos de Bariloche','','info@convertec.com.ar','reparsoft',39,1,'empresa','2026-06-21 15:48:30'),(37,'Otro','157','Daniel ( Soldadora Lusqtoff)','IVA Responsable Inscripto','','','','reparsoft',157,1,'empresa','2026-06-21 15:48:30'),(38,'Otro','6','Daniel (Bari Clima)','IVA Responsable Inscripto','','','','reparsoft',6,1,'empresa','2026-06-21 15:48:30'),(39,'Otro','49','Daniel (Placa Ariston)','IVA Responsable Inscripto','','','','reparsoft',49,1,'empresa','2026-06-21 15:48:30'),(40,'Otro','28','Daniel Calderísta','IVA Responsable Inscripto','','','','reparsoft',28,1,'empresa','2026-06-21 15:48:30'),(41,'Otro','94','Daniel Venturino','IVA Responsable Inscripto','','','','reparsoft',94,1,'empresa','2026-06-21 15:48:30'),(42,'Otro','58','Daniela (TV Hisense)','IVA Responsable Inscripto','','','','reparsoft',58,1,'empresa','2026-06-21 15:48:30'),(43,'Otro','131','Darío (Inversor Epever)','IVA Responsable Inscripto','','','','reparsoft',131,1,'empresa','2026-06-21 15:48:30'),(44,'CUIT','30545031112','DEL TURISTA','IVA Responsable Inscripto','','','','reparsoft',74,1,'empresa','2026-06-21 15:48:30'),(45,'Otro','169','Diego','IVA Responsable Inscripto','','21545215','','reparsoft',169,1,'empresa','2026-06-21 15:48:30'),(46,'Otro','68','Diego (Cargador Motocaddy)','IVA Responsable Inscripto','','','','reparsoft',68,1,'empresa','2026-06-21 15:48:30'),(47,'Otro','95','Diego Robin','IVA Responsable Inscripto','','','','reparsoft',95,1,'empresa','2026-06-21 15:48:30'),(48,'Otro','14','Distribuidora Patagonia','IVA Responsable Inscripto','','','lzeiss@yahoo.com.ar','reparsoft',14,1,'empresa','2026-06-21 15:48:30'),(49,'Otro','163','Eduardo Taglia','IVA Responsable Inscripto','','','','reparsoft',163,1,'empresa','2026-06-21 15:48:30'),(50,'CUIT','20214888956','ELS','IVA Responsable Inscripto','','','els@elsweb.com.ar','reparsoft',9,1,'empresa','2026-06-21 15:48:30'),(51,'Otro','35','Elvis','IVA Responsable Inscripto','','','','reparsoft',35,1,'empresa','2026-06-21 15:48:30'),(52,'Otro','73','Emiliano','IVA Responsable Inscripto','','','','reparsoft',73,1,'empresa','2026-06-21 15:48:30'),(53,'Otro','164','Enrique Clausen','IVA Responsable Inscripto','','','','reparsoft',164,1,'empresa','2026-06-21 15:48:30'),(54,'CUIT','30708767693','ERDESA SRL (Lavadero Bariloche)','IVA Responsable Inscripto','La Paz 730, Bariloche','','','reparsoft',66,1,'empresa','2026-06-21 15:48:30'),(55,'Otro','47','Esteban (Smart 32)','IVA Responsable Inscripto','','','','reparsoft',47,1,'empresa','2026-06-21 15:48:30'),(56,'Otro','53','Eugenio (Grupo Electrógeno)','IVA Responsable Inscripto','','','','reparsoft',53,1,'empresa','2026-06-21 15:48:30'),(57,'CUIT','30711711798','Exequiel Benjamín','IVA Responsable Inscripto','','','operaciones@baguales-patagonia.com','reparsoft',135,1,'empresa','2026-06-21 15:48:30'),(58,'Otro','54','Ezequiel (Grupo Electrógeno)','IVA Responsable Inscripto','','','','reparsoft',54,1,'empresa','2026-06-21 15:48:30'),(59,'Otro','32','Facundo','IVA Responsable Inscripto','','','','reparsoft',32,1,'empresa','2026-06-21 15:48:30'),(60,'Otro','51','Federíco (Soldadora)','IVA Responsable Inscripto','','','','reparsoft',51,1,'empresa','2026-06-21 15:48:30'),(61,'CUIT','33715914889','Federico Herrera','IVA Responsable Inscripto','','','','reparsoft',65,1,'empresa','2026-06-21 15:48:30'),(62,'Otro','2','Felipe','IVA Responsable Inscripto','Personal','0','','reparsoft',2,1,'empresa','2026-06-21 15:48:30'),(63,'Otro','111','Fernando Simoneta','IVA Responsable Inscripto','','','','reparsoft',111,1,'empresa','2026-06-21 15:48:30'),(64,'Otro','145','Gastón (A3 Unión Gráfica)','IVA Responsable Inscripto','','','','reparsoft',145,1,'empresa','2026-06-21 15:48:30'),(65,'Otro','105','Gastón (Ascensores Lucero)','IVA Responsable Inscripto','','','','reparsoft',105,1,'empresa','2026-06-21 15:48:30'),(66,'Otro','160','Gastón (Placa Ariston)','IVA Responsable Inscripto','','','','reparsoft',160,1,'empresa','2026-06-21 15:48:30'),(67,'Otro','30','Gerardo (Caldera Pina)','IVA Responsable Inscripto','','','','reparsoft',30,1,'empresa','2026-06-21 15:48:30'),(68,'Otro','98','Germán Montero','IVA Responsable Inscripto','','','','reparsoft',98,1,'empresa','2026-06-21 15:48:30'),(69,'Otro','122','Guillermo','IVA Responsable Inscripto','','','','reparsoft',122,1,'empresa','2026-06-21 15:48:30'),(70,'Otro','87','Guillermo Calderas','IVA Responsable Inscripto','','','','reparsoft',87,1,'empresa','2026-06-21 15:48:30'),(71,'Otro','72','Gustavo Calderas','IVA Responsable Inscripto','','','','reparsoft',72,1,'empresa','2026-06-21 15:48:30'),(72,'Otro','80','Héctor Vázquez','IVA Responsable Inscripto','','','','reparsoft',80,1,'empresa','2026-06-21 15:48:30'),(73,'Otro','56','Hernán Calderas','IVA Responsable Inscripto','','','','reparsoft',56,1,'empresa','2026-06-21 15:48:30'),(74,'Otro','89','Hotel Cacique Inacayal','IVA Responsable Inscripto','','','','reparsoft',89,1,'empresa','2026-06-21 15:48:30'),(75,'Otro','150','Hotel Huinid','IVA Responsable Inscripto','','','','reparsoft',150,1,'empresa','2026-06-21 15:48:30'),(76,'Otro','96','Hotel Kilton SA','IVA Responsable Inscripto','','','','reparsoft',96,1,'empresa','2026-06-21 15:48:30'),(77,'Otro','113','Hotel Montana','IVA Responsable Inscripto','','','','reparsoft',113,1,'empresa','2026-06-21 15:48:30'),(78,'Otro','140','Hotel Tunquelén','IVA Responsable Inscripto','','','','reparsoft',140,1,'empresa','2026-06-21 15:48:30'),(79,'Otro','16','Hugo','IVA Responsable Inscripto','','','','reparsoft',16,1,'empresa','2026-06-21 15:48:30'),(80,'Otro','85','Hugo (Inversor Belttt)','IVA Responsable Inscripto','','','','reparsoft',85,1,'empresa','2026-06-21 15:48:30'),(81,'Otro','46','Hugo (Soldadora Inverter)','IVA Responsable Inscripto','','','','reparsoft',46,1,'empresa','2026-06-21 15:48:30'),(82,'Otro','92','Hugo Rega','IVA Responsable Inscripto','','','','reparsoft',92,1,'empresa','2026-06-21 15:48:30'),(83,'CUIT','30660795568','Huilque SRL','IVA Responsable Inscripto','AVENIDA CARLOS BUSTOS 329 / PARAJE: CERRO CATEDRAL Código postal: 8400 RIO NEGRO','','santiagol@huilque.com','reparsoft',10,1,'empresa','2026-06-21 15:48:30'),(84,'Otro','116','INTA Bariloche','IVA Responsable Inscripto','','','','reparsoft',116,1,'empresa','2026-06-21 15:48:30'),(85,'CUIT','30715328433','IPATEC','IVA Responsable Inscripto','Av De Los Pioneros 2350, (CP: 8400), San Carlos de Bariloche, Río Negro, Argentina.','','contacto.ipatec@comahue-conicet.gob.ar','reparsoft',15,1,'empresa','2026-06-21 15:48:30'),(86,'Otro','33','Javier','IVA Responsable Inscripto','','','','reparsoft',33,1,'empresa','2026-06-21 15:48:30'),(87,'Otro','62','Joaquín','IVA Responsable Inscripto','','','','reparsoft',62,1,'empresa','2026-06-21 15:48:30'),(88,'Otro','79','John','IVA Responsable Inscripto','','','','reparsoft',79,1,'empresa','2026-06-21 15:48:30'),(89,'Otro','29','Jonathan Chacón','IVA Responsable Inscripto','','','','reparsoft',29,1,'empresa','2026-06-21 15:48:30'),(90,'Otro','71','Jorge (Frigobar)','IVA Responsable Inscripto','','','','reparsoft',71,1,'empresa','2026-06-21 15:48:30'),(91,'Otro','149','Jorge Pastrana','IVA Responsable Inscripto','','','','reparsoft',149,1,'empresa','2026-06-21 15:48:30'),(92,'Otro','123','Jorge Roca','IVA Responsable Inscripto','','','','reparsoft',123,1,'empresa','2026-06-21 15:48:30'),(93,'Otro','133','José','IVA Responsable Inscripto','','','','reparsoft',133,1,'empresa','2026-06-21 15:48:30'),(94,'Otro','118','José (Total Clima)','IVA Responsable Inscripto','','','','reparsoft',118,1,'empresa','2026-06-21 15:48:30'),(95,'Otro','42','José Groznik','IVA Responsable Inscripto','','','','reparsoft',42,1,'empresa','2026-06-21 15:48:30'),(96,'Otro','17','Jose Luis (Privatel)','IVA Responsable Inscripto','','','','reparsoft',17,1,'empresa','2026-06-21 15:48:30'),(97,'Otro','168','Juan','IVA Responsable Inscripto','','1234567','','reparsoft',168,1,'empresa','2026-06-21 15:48:30'),(98,'Otro','137','Juan ( Horno Ariston)','IVA Responsable Inscripto','','','','reparsoft',137,1,'empresa','2026-06-21 15:48:30'),(99,'Otro','154','Julian Grossi','IVA Responsable Inscripto','','','','reparsoft',154,1,'empresa','2026-06-21 15:48:30'),(100,'Otro','5','Julio Piacentini','IVA Responsable Inscripto','','','','reparsoft',5,1,'empresa','2026-06-21 15:48:30'),(101,'Otro','90','Leandro ( Placas lavarropas )','IVA Responsable Inscripto','','','','reparsoft',90,1,'empresa','2026-06-21 15:48:30'),(102,'Otro','114','Leandro (Los Pehuenes)','IVA Responsable Inscripto','','','','reparsoft',114,1,'empresa','2026-06-21 15:48:30'),(103,'Otro','20','Leandro Slosel','IVA Responsable Inscripto','','','','reparsoft',20,1,'empresa','2026-06-21 15:48:30'),(104,'Otro','55','Leandro Suaréz','IVA Responsable Inscripto','','','','reparsoft',55,1,'empresa','2026-06-21 15:48:30'),(105,'Otro','76','Leonardo (Gimnasios Neuquén)','IVA Responsable Inscripto','','','','reparsoft',76,1,'empresa','2026-06-21 15:48:30'),(106,'Otro','102','Leonel Valdez (Calderas)','IVA Responsable Inscripto','','','','reparsoft',102,1,'empresa','2026-06-21 15:48:30'),(107,'Otro','167','Liliana Muñoz','IVA Responsable Inscripto','','','','reparsoft',167,1,'empresa','2026-06-21 15:48:30'),(108,'Otro','61','Lionel (Alicura)','IVA Responsable Inscripto','','','','reparsoft',61,1,'empresa','2026-06-21 15:48:30'),(109,'Otro','99','Lisandro (amplificador guitarra)','IVA Responsable Inscripto','','','','reparsoft',99,1,'empresa','2026-06-21 15:48:30'),(110,'Otro','69','Lisandro (Patagonia Bags)','IVA Responsable Inscripto','','','','reparsoft',69,1,'empresa','2026-06-21 15:48:31'),(111,'Otro','63','Lisandro Pastrana','IVA Responsable Inscripto','','','','reparsoft',63,1,'empresa','2026-06-21 15:48:31'),(112,'Otro','120','Lucas (Inversor Voltronic)','IVA Responsable Inscripto','','','','reparsoft',120,1,'empresa','2026-06-21 15:48:31'),(113,'Otro','152','Luciano Zeiss','IVA Responsable Inscripto','','','','reparsoft',152,1,'empresa','2026-06-21 15:48:31'),(114,'Otro','67','Malvina Gallardo','IVA Responsable Inscripto','','','','reparsoft',67,1,'empresa','2026-06-21 15:48:31'),(115,'CUIT','30708107758','Mamuschka SRL','IVA Responsable Inscripto','Mitre 298, San Carlos De Bariloche (8400), Rio Negro, Argentina','2944426585','proveedores@mamuschka.com','reparsoft',21,1,'empresa','2026-06-21 15:48:31'),(116,'Otro','83','Marcelo (Aguas Claras)','IVA Responsable Inscripto','','','','reparsoft',83,1,'empresa','2026-06-21 15:48:31'),(117,'Otro','100','Marcos (Inversor Giandel)','IVA Responsable Inscripto','','','','reparsoft',100,1,'empresa','2026-06-21 15:48:31'),(118,'Otro','82','María Fernanda Bari','IVA Responsable Inscripto','','','','reparsoft',82,1,'empresa','2026-06-21 15:48:31'),(119,'Otro','4','Mariel Celio','IVA Responsable Inscripto','','','','reparsoft',4,1,'empresa','2026-06-21 15:48:31'),(120,'Otro','117','Martín (Cargador Baterías)','IVA Responsable Inscripto','','','','reparsoft',117,1,'empresa','2026-06-21 15:48:31'),(121,'Otro','59','Martín Federico Sosa','IVA Responsable Inscripto','','','','reparsoft',59,1,'empresa','2026-06-21 15:48:31'),(122,'Otro','142','Martín Korten','IVA Responsable Inscripto','','','','reparsoft',142,1,'empresa','2026-06-21 15:48:31'),(123,'Otro','136','Matías (Cinta BH Fitnes)','IVA Responsable Inscripto','','','','reparsoft',136,1,'empresa','2026-06-21 15:48:31'),(124,'Otro','106','Matías Korten','IVA Responsable Inscripto','','','','reparsoft',106,1,'empresa','2026-06-21 15:48:31'),(125,'Otro','93','Matías Menteguiaga','IVA Responsable Inscripto','','','','reparsoft',93,1,'empresa','2026-06-21 15:48:31'),(126,'Otro','144','Matías Sebastián Regina','IVA Responsable Inscripto','','','','reparsoft',144,1,'empresa','2026-06-21 15:48:31'),(127,'Otro','86','Mauro (Calderas)','IVA Responsable Inscripto','','','','reparsoft',86,1,'empresa','2026-06-21 15:48:31'),(128,'Otro','158','Maxi (Inversor Voltronic)','IVA Responsable Inscripto','','','','reparsoft',158,1,'empresa','2026-06-21 15:48:31'),(129,'Otro','41','Maxi (Servicios Carlitos)','IVA Responsable Inscripto','','','','reparsoft',41,1,'empresa','2026-06-21 15:48:31'),(130,'Otro','48','Mery (soldadora Dogo)','IVA Responsable Inscripto','','','','reparsoft',48,1,'empresa','2026-06-21 15:48:31'),(131,'Otro','88','Miguel Díaz','IVA Responsable Inscripto','','','','reparsoft',88,1,'empresa','2026-06-21 15:48:31'),(132,'Otro','119','Miguel Sanchez','IVA Responsable Inscripto','','','','reparsoft',119,1,'empresa','2026-06-21 15:48:31'),(133,'Otro','57','Namor (Soldadora inverter)','IVA Responsable Inscripto','','','','reparsoft',57,1,'empresa','2026-06-21 15:48:31'),(134,'Otro','23','Nicolás (Placas Lav_Sec ropas)','IVA Responsable Inscripto','','','','reparsoft',23,1,'empresa','2026-06-21 15:48:31'),(135,'Otro','130','Nora Di Capua','IVA Responsable Inscripto','','','','reparsoft',130,1,'empresa','2026-06-21 15:48:31'),(136,'Otro','107','Octavio Roca','IVA Responsable Inscripto','','','','reparsoft',107,1,'empresa','2026-06-21 15:48:31'),(137,'Otro','81','Osvaldo','IVA Responsable Inscripto','','','','reparsoft',81,1,'empresa','2026-06-21 15:48:31'),(138,'Otro','110','Pablo (Caldera Peisa DS30f)','IVA Responsable Inscripto','','','','reparsoft',110,1,'empresa','2026-06-21 15:48:31'),(139,'CUIT','30716840243','Pablo Biggeri','IVA Responsable Inscripto','','','','reparsoft',151,1,'empresa','2026-06-21 15:48:31'),(140,'CUIT','33716584459','Patagonia Clima SAS','IVA Responsable Inscripto','Catamarca 150 - San Carlos de Bariloche','','patagoniaclimabariloche@gmail.com','reparsoft',40,1,'empresa','2026-06-21 15:48:31'),(141,'Otro','166','Patricia Icare','IVA Responsable Inscripto','','','','reparsoft',166,1,'empresa','2026-06-21 15:48:31'),(142,'CUIT','30615757078','Privatel','IVA Responsable Inscripto','Rivadavia 571, San Carlos De Bariloche (8400), Rio Negro, Argentina','','','reparsoft',7,1,'empresa','2026-06-21 15:48:31'),(143,'CUIT','30710358938','QUASAR INFORMATICA SA','IVA Responsable Inscripto','Emilio Frey 568, San Carlos de Bariloche','','','reparsoft',24,1,'empresa','2026-06-21 15:48:31'),(144,'Otro','159','Quimey','IVA Responsable Inscripto','','','','reparsoft',159,1,'empresa','2026-06-21 15:48:31'),(145,'Otro','25','Rolando Soldadora MIG-MAG','IVA Responsable Inscripto','','','','reparsoft',25,1,'empresa','2026-06-21 15:48:31'),(146,'Otro','109','Rubén (Caldera Ideal Clima)','IVA Responsable Inscripto','','','','reparsoft',109,1,'empresa','2026-06-21 15:48:31'),(147,'Otro','132','Santiago Nazar','IVA Responsable Inscripto','','','','reparsoft',132,1,'empresa','2026-06-21 15:48:31'),(148,'Otro','103','Sebastián (TV LG)','IVA Responsable Inscripto','','','','reparsoft',103,1,'empresa','2026-06-21 15:48:31'),(149,'Otro','156','Sergio (Cerro Lopez)','IVA Responsable Inscripto','','','','reparsoft',156,1,'empresa','2026-06-21 15:48:31'),(150,'Otro','52','Servytec','IVA Responsable Inscripto','','','','reparsoft',52,1,'empresa','2026-06-21 15:48:31'),(151,'Otro','165','Silvana TV Samsung 40','IVA Responsable Inscripto','','','','reparsoft',165,1,'empresa','2026-06-21 15:48:31'),(152,'Otro','97','Soluciones Técnicas Bariloche','IVA Responsable Inscripto','','','','reparsoft',97,1,'empresa','2026-06-21 15:48:31'),(153,'Otro','8','Stec Climatización','IVA Responsable Inscripto','','','stec.climatizacion@gmail.com','reparsoft',8,1,'empresa','2026-06-21 15:48:31'),(154,'Otro','148','Tecpetrol','IVA Responsable Inscripto','','','','reparsoft',148,1,'empresa','2026-06-21 15:48:31'),(155,'Otro','64','Torres (2do 1)','IVA Responsable Inscripto','','','','reparsoft',64,1,'empresa','2026-06-21 15:48:31'),(156,'CUIT','30708448202','Total Clima','IVA Responsable Inscripto','Elordi 368 (8400), San Carlos de Bariloche. Rio Negro, Argentina','2944431070','info@totalclimaonline.com.ar','reparsoft',3,1,'empresa','2026-06-21 15:48:31'),(157,'CUIT','30522581506','Tronador SAC (Rapanui)','IVA Responsable Inscripto','Mitre 202 Bariloche','','','reparsoft',108,1,'empresa','2026-06-21 15:48:31'),(158,'Otro','91','Vanesa ( Reflector LED)','IVA Responsable Inscripto','','','','reparsoft',91,1,'empresa','2026-06-21 15:48:31'),(159,'Otro','138','Victor (Soldadora Volt)','IVA Responsable Inscripto','','','','reparsoft',138,1,'empresa','2026-06-21 15:48:31'),(160,'Otro','115','Viviana Escobar','IVA Responsable Inscripto','','','','reparsoft',115,1,'empresa','2026-06-21 15:48:31');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobantes`
--

DROP TABLE IF EXISTS `comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobantes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cuit_emisor` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_comprobante` int NOT NULL,
  `punto_venta` int NOT NULL,
  `numero` bigint NOT NULL,
  `cuit_receptor` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `razon_social_rec` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `importe_neto` decimal(12,2) DEFAULT NULL,
  `importe_iva` decimal(12,2) DEFAULT NULL,
  `importe_total` decimal(12,2) DEFAULT NULL,
  `cae` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vencimiento_cae` date DEFAULT NULL,
  `els_asociado` int DEFAULT NULL,
  `ruta_pdf` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_enviado` tinyint(1) DEFAULT '0',
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `concepto` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `periodo_desde` date DEFAULT NULL,
  `periodo_hasta` date DEFAULT NULL,
  `periodo_vto` date DEFAULT NULL,
  `condicion_iva_receptor` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_documento` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nro_documento` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `domicilio_receptor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_receptor` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condiciones_venta` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comprobante_asociado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_pago` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'pendiente',
  `otros_impuestos` decimal(12,2) DEFAULT '0.00',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comprobante` (`cuit_emisor`,`tipo_comprobante`,`punto_venta`,`numero`),
  KEY `idx_cuit_emisor` (`cuit_emisor`),
  KEY `idx_fecha_emision` (`fecha_emision`),
  KEY `idx_cae` (`cae`),
  KEY `idx_estado_pago` (`estado_pago`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobantes`
--

LOCK TABLES `comprobantes` WRITE;
/*!40000 ALTER TABLE `comprobantes` DISABLE KEYS */;
INSERT INTO `comprobantes` VALUES (1,'20309255039',11,1,4,'70','Adriana (Placa clasera)','2026-06-21',159500.00,0.00,159500.00,'86250460585084','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000004.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','DNI','70','','','Contado','','pagada_total',0.00,'2026-06-21 17:07:17'),(2,'20309255039',11,1,5,'167','Liliana Muñoz','2026-06-21',198000.00,0.00,198000.00,'86250460655189','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000005.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','','167','','','Contado','','pagada_total',0.00,'2026-06-21 18:03:48'),(3,'20309255039',11,1,6,'166','Patricia Icare','2026-06-21',187000.00,0.00,187000.00,'86250460756103','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000006.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','DNI','166','','','Contado','','pagada_total',0.00,'2026-06-21 19:27:11'),(4,'20309255039',11,1,7,'30711621942','Alma Del Lago (Almasur S.A.)','2026-06-21',50000.00,0.00,50000.00,'86250460774356','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000007.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','IVA Responsable Inscripto','CUIT','30711621942','Av. Bustillo km 1,151','abastecimiento@almasuites.com.ar','Contado','','pagada_total',0.00,'2026-06-21 19:41:53'),(5,'20309255039',11,1,8,'30708767693','ERDESA SRL (Lavadero Bariloche)','2026-06-21',120000.00,0.00,120000.00,'86250460801430','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000008.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','IVA Responsable Inscripto','CUIT','30708767693','La Paz 730, Bariloche','','Contado','','pagada_total',0.00,'2026-06-21 20:02:09'),(6,'20309255039',11,1,9,'87','Guillermo Calderas','2026-06-21',210000.00,0.00,210000.00,'86250460825561','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000009.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','DNI','87','','','Contado','','pagada_total',0.00,'2026-06-21 20:17:17'),(7,'20309255039',11,1,10,'112','Bernardo Moreno','2026-06-21',213000.00,0.00,213000.00,'86250461057229','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000010.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','','112','','','Contado','','pendiente',0.00,'2026-06-21 22:46:00'),(8,'20309255039',11,1,11,'162','Carlos Cubau','2026-06-21',297000.00,0.00,297000.00,'86250461059292','2026-07-01',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000011.pdf',0,NULL,NULL,'2026-06-21','2026-06-21','2026-06-21','Consumidor Final','','162','','','Contado','','pagada_total',0.00,'2026-06-21 22:46:38'),(9,'20309255039',11,1,12,'30706433585','Ascensores Lucero','2026-06-22',415000.00,0.00,415000.00,'86250464465789','2026-07-02',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000012.pdf',0,NULL,NULL,'2026-06-22','2026-06-22','2026-06-22','IVA Responsable Inscripto','CUIT','30706433585','Las Violetas 1215, San Carlos de Bariloche, RÃ­o Negro','luceroasc@speedy.com.ar','Contado','','pendiente',0.00,'2026-06-23 01:43:10'),(10,'20309255039',11,1,13,'86','Mauro (Calderas)','2026-06-22',460000.00,0.00,460000.00,'86250464508341','2026-07-02',0,'F:\\els\\Bariloche\\Administracion\\Sistema\\Facturas PDF\\20309255039_001_00011_00000013.pdf',0,NULL,NULL,'2026-06-22','2026-06-22','2026-06-22','Consumidor Final','DNI','86','','','Contado','','pendiente',0.00,'2026-06-23 01:52:38');
/*!40000 ALTER TABLE `comprobantes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuraciones`
--

DROP TABLE IF EXISTS `configuraciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuraciones` (
  `clave` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuraciones`
--

LOCK TABLES `configuraciones` WRITE;
/*!40000 ALTER TABLE `configuraciones` DISABLE KEYS */;
INSERT INTO `configuraciones` VALUES ('arca.entorno','homo','Entorno ARCA: homo=homologación, prod=producción'),('iva.alicuota.0','0.00','Alicuota IVA 0%'),('iva.alicuota.1','10.50','Alicuota IVA 10.5%'),('iva.alicuota.2','21.00','Alicuota IVA 21%'),('iva.alicuota.3','27.00','Alicuota IVA 27%'),('reparsoft.host','localhost','Host de ReparSoft'),('reparsoft.port','3306','Puerto de ReparSoft'),('smtp.host','','Host del servidor SMTP'),('smtp.pass','','Contraseña SMTP'),('smtp.port','587','Puerto del servidor SMTP'),('smtp.user','','Usuario SMTP');
/*!40000 ALTER TABLE `configuraciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuit_certificados`
--

DROP TABLE IF EXISTS `cuit_certificados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuit_certificados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cuit` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `razon_social` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condicion_iva` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `punto_venta` int NOT NULL,
  `ruta_certificado` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_cert` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `domicilio` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ingresos_brutos` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_inicio_actividades` date DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cuit` (`cuit`),
  KEY `idx_cuit` (`cuit`),
  KEY `idx_activo` (`activo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuit_certificados`
--

LOCK TABLES `cuit_certificados` WRITE;
/*!40000 ALTER TABLE `cuit_certificados` DISABLE KEYS */;
INSERT INTO `cuit_certificados` VALUES (1,'30678901234','FACTURASOFT TEST S.A.','IVA Responsable Inscripto',1,'src/main/resources/certificados/Certificado.p12','123456','','',NULL,0),(2,'27123456789','FACTURASOFT MONOTRIBUTO','Responsable Monotributo',2,'src/main/resources/certificados/Certificado.p12','123456','','',NULL,0),(3,'20309255039','BERTOSSI DIEGO HERNAN','Responsable Monotributo',1,'F:\\Users\\Diego\\git\\SistemaContable_ramas\\src\\main\\resources\\certificados\\certificado.p12','30925503','Cedro 13455 - San Carlos De Bariloche, Río Negro','RS RIO NEGRO','2017-11-01',1);
/*!40000 ALTER TABLE `cuit_certificados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura_item_pagos`
--

DROP TABLE IF EXISTS `factura_item_pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_item_pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `factura_item_id` int NOT NULL,
  `comprobante_id` int NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `fecha_pago` date NOT NULL,
  `recibo_id` int DEFAULT NULL,
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pendiente',
  PRIMARY KEY (`id`),
  KEY `comprobante_id` (`comprobante_id`),
  KEY `idx_item` (`factura_item_id`),
  KEY `idx_recibo` (`recibo_id`),
  CONSTRAINT `factura_item_pagos_ibfk_1` FOREIGN KEY (`factura_item_id`) REFERENCES `factura_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `factura_item_pagos_ibfk_2` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura_item_pagos`
--

LOCK TABLES `factura_item_pagos` WRITE;
/*!40000 ALTER TABLE `factura_item_pagos` DISABLE KEYS */;
INSERT INTO `factura_item_pagos` VALUES (1,5,5,120000.00,'2026-06-21',NULL,'pagado');
/*!40000 ALTER TABLE `factura_item_pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura_items`
--

DROP TABLE IF EXISTS `factura_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `comprobante_id` int NOT NULL,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` decimal(12,2) NOT NULL,
  `unidad_medida` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'Unidad',
  `precio_unitario` decimal(12,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `alicuota_iva` decimal(5,2) DEFAULT '21.00',
  `estado_pago` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pendiente',
  `els_referencia` int DEFAULT NULL,
  `orden` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_comprobante` (`comprobante_id`),
  KEY `idx_els` (`els_referencia`),
  CONSTRAINT `factura_items_ibfk_1` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura_items`
--

LOCK TABLES `factura_items` WRITE;
/*!40000 ALTER TABLE `factura_items` DISABLE KEYS */;
INSERT INTO `factura_items` VALUES (1,1,'1149','Reparación de Placa de Caldera  Peisa BGL 188 s/n: 6YKG8',1.00,'Unidad',159500.00,159500.00,0.00,'pagado',1149,0),(2,2,'1497','Reparación de Smart TV 50\" BGH B5021UH6A s/n: 271JJ20334',1.00,'Unidad',198000.00,198000.00,0.00,'pagado',1497,0),(3,3,'1496','Reparación de Smart TV 43 Philco PLD43FS9A s/n: 059630000020483',1.00,'Unidad',187000.00,187000.00,0.00,'pagado',1496,0),(4,4,'1495','Reparación de Teléfono Digital Panasonic KX-DT346 s/n: 07896067293997',1.00,'Unidad',50000.00,50000.00,0.00,'pendiente',1495,0),(5,5,'1429','Reparación de Placa Controladora de Motor Graseby Controls 1268P CONTROLLER s/n: 147579',1.00,'Unidad',120000.00,120000.00,0.00,'pagado',1429,0),(6,6,'1494','Reparación de Placa de caldera Caldaia E312264 (TOP digital Sa26F) s/n: 1Q3FO',1.00,'Unidad',210000.00,210000.00,0.00,'pagado',1494,0),(7,7,'1493','Reparación de Placa Fuente Horno  Wictory LTedesco (INOVA) INV-6803-01/1 s/n: 103022',1.00,'Unidad',213000.00,213000.00,0.00,'pendiente',1493,0),(8,8,'1481','Reparación de UPS 1,1 kVA APC BX1100C-AR s/n: 3B1226X33717',1.00,'Unidad',225000.00,225000.00,0.00,'pagado',1481,0),(9,8,'1482','Reparación de UPS 350VA APC BK350EI s/n: 4B1145P15035',1.00,'Unidad',72000.00,72000.00,0.00,'pagado',1482,1),(10,9,'1478','Reparación de Operador de Puerta Fermator VF5+ s/n: 10/650624',1.00,'Unidad',195000.00,195000.00,0.00,'pendiente',1478,0),(11,9,'1479','Reparación de Control de Maniobra Automac A6300 s/n: U3JCT',1.00,'Unidad',220000.00,220000.00,0.00,'pendiente',1479,1),(12,10,'1420','Reparación de Placa de caldera Peisa BGL 188 s/n: FBTOP',1.00,'Unidad',230000.00,230000.00,0.00,'pendiente',1420,0),(13,10,'1421','Reparación de Placa de caldera Peisa BGL 188 s/n: TNSMB',1.00,'Unidad',230000.00,230000.00,0.00,'pendiente',1421,1);
/*!40000 ALTER TABLE `factura_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura_pagos`
--

DROP TABLE IF EXISTS `factura_pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `comprobante_id` int NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `fecha_pago` date NOT NULL,
  `forma_pago` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `recibo_id` int DEFAULT NULL,
  `observaciones` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_comprobante` (`comprobante_id`),
  KEY `idx_recibo` (`recibo_id`),
  CONSTRAINT `factura_pagos_ibfk_1` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura_pagos`
--

LOCK TABLES `factura_pagos` WRITE;
/*!40000 ALTER TABLE `factura_pagos` DISABLE KEYS */;
INSERT INTO `factura_pagos` VALUES (1,6,210000.00,'2026-06-21','Efectivo',3,NULL,'2026-06-21 20:33:37'),(2,5,120000.00,'2026-06-21','Efectivo',2,NULL,'2026-06-21 20:33:50'),(3,4,50000.00,'2026-06-21','Efectivo',1,NULL,'2026-06-21 20:33:58'),(4,3,187000.00,'2026-06-21','Efectivo',4,NULL,'2026-06-21 22:06:55'),(5,2,198000.00,'2026-06-21','Efectivo',5,NULL,'2026-06-21 22:18:41'),(6,1,159500.00,'2026-06-21','Efectivo',6,NULL,'2026-06-21 22:42:37'),(7,8,297000.00,'2026-06-21','Efectivo',7,NULL,'2026-06-21 22:47:24');
/*!40000 ALTER TABLE `factura_pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recibo_facturas`
--

DROP TABLE IF EXISTS `recibo_facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibo_facturas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `recibo_id` int NOT NULL,
  `comprobante_id` int NOT NULL,
  `monto_aplicado` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recibo_factura` (`recibo_id`,`comprobante_id`),
  KEY `idx_comprobante` (`comprobante_id`),
  CONSTRAINT `recibo_facturas_ibfk_1` FOREIGN KEY (`recibo_id`) REFERENCES `recibos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recibo_facturas_ibfk_2` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recibo_facturas`
--

LOCK TABLES `recibo_facturas` WRITE;
/*!40000 ALTER TABLE `recibo_facturas` DISABLE KEYS */;
INSERT INTO `recibo_facturas` VALUES (1,1,4,50000.00),(2,2,5,120000.00),(3,3,6,210000.00),(4,4,3,187000.00),(5,5,2,198000.00),(6,6,1,159500.00),(7,7,8,297000.00);
/*!40000 ALTER TABLE `recibo_facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recibo_pagos`
--

DROP TABLE IF EXISTS `recibo_pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibo_pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `recibo_id` int NOT NULL,
  `forma_pago` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `referencia` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `datos_adicionales` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `idx_recibo` (`recibo_id`),
  CONSTRAINT `recibo_pagos_ibfk_1` FOREIGN KEY (`recibo_id`) REFERENCES `recibos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recibo_pagos`
--

LOCK TABLES `recibo_pagos` WRITE;
/*!40000 ALTER TABLE `recibo_pagos` DISABLE KEYS */;
INSERT INTO `recibo_pagos` VALUES (1,1,'Efectivo',50000.00,'',NULL),(2,2,'Efectivo',120000.00,'',NULL),(3,3,'Efectivo',210000.00,'',NULL),(4,4,'Efectivo',187000.00,'',NULL),(5,5,'Efectivo',198000.00,'',NULL),(6,6,'Efectivo',159500.00,'',NULL),(7,7,'Efectivo',297000.00,'',NULL);
/*!40000 ALTER TABLE `recibo_pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recibos`
--

DROP TABLE IF EXISTS `recibos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_recibo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_cobro` date NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `cuit_cliente` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `razon_social_cliente` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `monto_total` decimal(12,2) NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_recibo` (`numero_recibo`),
  UNIQUE KEY `uk_numero` (`numero_recibo`),
  KEY `idx_cliente` (`cliente_id`),
  KEY `idx_fecha` (`fecha_cobro`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recibos`
--

LOCK TABLES `recibos` WRITE;
/*!40000 ALTER TABLE `recibos` DISABLE KEYS */;
INSERT INTO `recibos` VALUES (1,'RE 0001-00000001','2026-06-21',NULL,'30711621942','Alma Del Lago (Almasur S.A.)',50000.00,'Recibo generado desde 1 pago(s)','2026-06-21 20:40:45'),(2,'RE 0001-00000002','2026-06-21',NULL,'30708767693','ERDESA SRL (Lavadero Bariloche)',120000.00,'Recibo generado desde 1 pago(s)','2026-06-21 20:40:58'),(3,'RE 0001-00000003','2026-06-21',NULL,'87','Guillermo Calderas',210000.00,'Recibo generado desde 1 pago(s)','2026-06-21 21:38:21'),(4,'RE 0001-00000004','2026-06-21',NULL,'166','Patricia Icare',187000.00,'Recibo generado desde 1 pago(s)','2026-06-21 22:07:21'),(5,'RE 0001-00000005','2026-06-21',NULL,'167','Liliana Muñoz',198000.00,'Recibo generado desde 1 pago(s)','2026-06-21 22:19:13'),(6,'RE 0001-00000006','2026-06-21',NULL,'70','Adriana (Placa clasera)',159500.00,'Recibo generado desde 1 pago(s)','2026-06-21 22:42:51'),(7,'RE 0001-00000007','2026-06-21',NULL,'162','Carlos Cubau',297000.00,'Recibo generado desde 1 pago(s)','2026-06-21 22:48:25');
/*!40000 ALTER TABLE `recibos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remito_items`
--

DROP TABLE IF EXISTS `remito_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `remito_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `remito_id` int NOT NULL,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` decimal(12,2) NOT NULL,
  `unidad_medida` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'Unidad',
  `els_referencia` int DEFAULT NULL,
  `orden` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_remito` (`remito_id`),
  CONSTRAINT `remito_items_ibfk_1` FOREIGN KEY (`remito_id`) REFERENCES `remitos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remito_items`
--

LOCK TABLES `remito_items` WRITE;
/*!40000 ALTER TABLE `remito_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `remito_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remitos`
--

DROP TABLE IF EXISTS `remitos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `remitos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_remito` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega` date DEFAULT NULL,
  `cuit_emisor` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `razon_social_emisor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `domicilio_emisor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cuit_receptor` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `razon_social_receptor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `domicilio_receptor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comprobante_id` int DEFAULT NULL,
  `reparsoft_remito_id` int DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'pendiente',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_remito` (`numero_remito`),
  KEY `idx_emisor` (`cuit_emisor`),
  KEY `idx_receptor` (`cuit_receptor`),
  KEY `idx_estado` (`estado`),
  KEY `idx_comprobante` (`comprobante_id`),
  KEY `idx_reparsoft_id` (`reparsoft_remito_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remitos`
--

LOCK TABLES `remitos` WRITE;
/*!40000 ALTER TABLE `remitos` DISABLE KEYS */;
/*!40000 ALTER TABLE `remitos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_cache`
--

DROP TABLE IF EXISTS `token_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_cache` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cuit` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `sign` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `expiracion` datetime NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_cuit_expiracion` (`cuit`,`expiracion`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_cache`
--

LOCK TABLES `token_cache` WRITE;
/*!40000 ALTER TABLE `token_cache` DISABLE KEYS */;
INSERT INTO `token_cache` VALUES (1,'20309255039','PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8c3NvIHZlcnNpb249IjIuMCI+CiAgICA8aWQgc3JjPSJDTj13c2FhaG9tbywgTz1BRklQLCBDPUFSLCBTRVJJQUxOVU1CRVI9Q1VJVCAzMzY5MzQ1MDIzOSIgZHN0PSJDTj13c2ZlLCBPPUFGSVAsIEM9QVIiIHVuaXF1ZV9pZD0iMzg2MTA3ODUzMyIgZ2VuX3RpbWU9IjE3ODIwNTcxMTEiIGV4cF90aW1lPSIxNzgyMTAwMzcxIi8+CiAgICA8b3BlcmF0aW9uIHR5cGU9ImxvZ2luIiB2YWx1ZT0iZ3JhbnRlZCI+CiAgICAgICAgPGxvZ2luIGVudGl0eT0iMzM2OTM0NTAyMzkiIHNlcnZpY2U9IndzZmUiIHVpZD0iU0VSSUFMTlVNQkVSPUNVSVQgMjAzMDkyNTUwMzksIENOPXRlc3RmYWN0dXJhc29mdCIgYXV0aG1ldGhvZD0iY21zIiByZWdtZXRob2Q9IjIyIj4KICAgICAgICAgICAgPHJlbGF0aW9ucz4KICAgICAgICAgICAgICAgIDxyZWxhdGlvbiBrZXk9IjIwMzA5MjU1MDM5IiByZWx0eXBlPSI0Ii8+CiAgICAgICAgICAgIDwvcmVsYXRpb25zPgogICAgICAgIDwvbG9naW4+CiAgICA8L29wZXJhdGlvbj4KPC9zc28+Cg==','kAZ8K/6D5dw0iy5geshmTIboXPbqhwbPGB/x1OmqV0X/UYqfk9haFSPekmlhx9y9lxaFMVAN2K6TCiEKExkk92lxFFvy6or549vadBgoaWw96q99rLg2P3uLJi0MXH6KMmzHXCXAqKp9CqXlFqEooqIjqF5+AWti/bogtaS3e5I=','2026-06-22 00:52:52','2026-06-21 15:52:52'),(2,'20309255039','PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8c3NvIHZlcnNpb249IjIuMCI+CiAgICA8aWQgc3JjPSJDTj13c2FhaG9tbywgTz1BRklQLCBDPUFSLCBTRVJJQUxOVU1CRVI9Q1VJVCAzMzY5MzQ1MDIzOSIgZHN0PSJDTj13c2ZlLCBPPUFGSVAsIEM9QVIiIHVuaXF1ZV9pZD0iMTQ0ODEzMDMwNiIgZ2VuX3RpbWU9IjE3ODIxNzg5MjYiIGV4cF90aW1lPSIxNzgyMjIyMTg2Ii8+CiAgICA8b3BlcmF0aW9uIHR5cGU9ImxvZ2luIiB2YWx1ZT0iZ3JhbnRlZCI+CiAgICAgICAgPGxvZ2luIGVudGl0eT0iMzM2OTM0NTAyMzkiIHNlcnZpY2U9IndzZmUiIHVpZD0iU0VSSUFMTlVNQkVSPUNVSVQgMjAzMDkyNTUwMzksIENOPXRlc3RmYWN0dXJhc29mdCIgYXV0aG1ldGhvZD0iY21zIiByZWdtZXRob2Q9IjIyIj4KICAgICAgICAgICAgPHJlbGF0aW9ucz4KICAgICAgICAgICAgICAgIDxyZWxhdGlvbiBrZXk9IjIwMzA5MjU1MDM5IiByZWx0eXBlPSI0Ii8+CiAgICAgICAgICAgIDwvcmVsYXRpb25zPgogICAgICAgIDwvbG9naW4+CiAgICA8L29wZXJhdGlvbj4KPC9zc28+Cg==','ImTIyceBqqQ5cozvky7zzDQOmfpYs8/lI33xcngr1x740Ev+Egvbfw1EKDrrLksP49I25qMgtTxlOwtjK1mROXvzATQdaNTpFWVyJwP4YKh0xib+vIMh3UcKyC8Z/vk6eBq+HHYMPenIpa5RC642ofmrpTyWUNenAQUPreMh3Ds=','2026-06-23 10:43:05','2026-06-23 01:43:05'),(3,'20309255039','PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8c3NvIHZlcnNpb249IjIuMCI+CiAgICA8aWQgc3JjPSJDTj13c2FhaG9tbywgTz1BRklQLCBDPUFSLCBTRVJJQUxOVU1CRVI9Q1VJVCAzMzY5MzQ1MDIzOSIgZHN0PSJDTj13c2ZlLCBPPUFGSVAsIEM9QVIiIHVuaXF1ZV9pZD0iMjIxNzI2NzE1MCIgZ2VuX3RpbWU9IjE3ODIxNzk0OTQiIGV4cF90aW1lPSIxNzgyMjIyNzU0Ii8+CiAgICA8b3BlcmF0aW9uIHR5cGU9ImxvZ2luIiB2YWx1ZT0iZ3JhbnRlZCI+CiAgICAgICAgPGxvZ2luIGVudGl0eT0iMzM2OTM0NTAyMzkiIHNlcnZpY2U9IndzZmUiIHVpZD0iU0VSSUFMTlVNQkVSPUNVSVQgMjAzMDkyNTUwMzksIENOPXRlc3RmYWN0dXJhc29mdCIgYXV0aG1ldGhvZD0iY21zIiByZWdtZXRob2Q9IjIyIj4KICAgICAgICAgICAgPHJlbGF0aW9ucz4KICAgICAgICAgICAgICAgIDxyZWxhdGlvbiBrZXk9IjIwMzA5MjU1MDM5IiByZWx0eXBlPSI0Ii8+CiAgICAgICAgICAgIDwvcmVsYXRpb25zPgogICAgICAgIDwvbG9naW4+CiAgICA8L29wZXJhdGlvbj4KPC9zc28+Cg==','coY7Ybv/paKma4gMlSRZq+1VPadk8LF02UeheVgWcCNQ1kjTkHCN8tBnmOh8uaiLQVQDejiy9NY7nPJ1jubcsa7uTlHda2PcL+HcTxUCV+A01u+QDRK+EgzhTSbAOjS5J9+D+GjfB/G66hw79gtYVgX8Zg0xFJqTje99mXHdmbE=','2026-06-23 10:52:33','2026-06-23 01:52:33');
/*!40000 ALTER TABLE `token_cache` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22 23:30:11
