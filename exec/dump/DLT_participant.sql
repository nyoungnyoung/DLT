-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: k8d209.p.ssafy.io    Database: DLT
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `participant`
--

DROP TABLE IF EXISTS `participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participant` (
  `participant_id` bigint NOT NULL AUTO_INCREMENT,
  `arrival_time` time DEFAULT NULL,
  `is_arrived` bit(1) DEFAULT b'0',
  `is_host` bit(1) DEFAULT NULL,
  `late_time` bigint DEFAULT '0',
  `transaction_money` int DEFAULT '0',
  `account_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `thyme` int DEFAULT '0',
  PRIMARY KEY (`participant_id`),
  KEY `FKptbndmpwose49o6u9d8x9aj9u` (`account_id`),
  KEY `FKjinj1dy7epqtgp6h64t42mn9j` (`plan_id`),
  CONSTRAINT `FKjinj1dy7epqtgp6h64t42mn9j` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`) ON DELETE CASCADE,
  CONSTRAINT `FKptbndmpwose49o6u9d8x9aj9u` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participant`
--

LOCK TABLES `participant` WRITE;
/*!40000 ALTER TABLE `participant` DISABLE KEYS */;
INSERT INTO `participant` VALUES (1,'18:20:10',_binary '',_binary '',-9,3000,1,1,0),(2,'18:40:20',_binary '',_binary '\0',10,-5000,2,1,0),(3,'16:23:50',_binary '',_binary '',-6,5000,3,2,0),(4,'16:30:00',_binary '',_binary '\0',0,3000,1,2,0),(5,'16:31:30',_binary '',_binary '\0',1,-3000,2,2,0),(6,NULL,_binary '\0',_binary '',NULL,0,1,3,0),(7,NULL,_binary '\0',_binary '\0',NULL,0,2,3,0),(9,'16:40:10',_binary '',_binary '\0',10,-5000,4,2,0),(10,NULL,_binary '\0',_binary '',NULL,0,3,4,0),(11,NULL,_binary '\0',_binary '\0',NULL,0,1,4,0),(12,NULL,_binary '\0',_binary '\0',NULL,0,2,4,0),(13,NULL,_binary '\0',_binary '\0',NULL,0,4,4,0),(14,'12:20:28',_binary '\0',_binary '',20,-4000,5,5,0),(15,'12:00:10',_binary '\0',_binary '\0',0,1000,4,5,0),(16,'12:05:12',_binary '\0',_binary '\0',5,-3000,2,5,0),(17,'11:49:24',_binary '\0',_binary '\0',-10,3000,3,5,0),(18,'11:54:57',_binary '\0',_binary '\0',-5,2000,1,5,0),(19,NULL,_binary '\0',_binary '\0',0,0,5,4,0),(20,'18:20:22',_binary '',_binary '',-9,3000,3,6,0),(21,'18:40:20',_binary '',_binary '\0',10,-3000,1,6,0),(22,'18:35:10',_binary '',_binary '\0',5,-2000,2,6,0),(23,'18:31:10',_binary '',_binary '\0',1,-1000,4,6,0),(24,'18:29:30',_binary '',_binary '\0',0,3000,5,6,0),(25,NULL,_binary '\0',_binary '',NULL,1900,4,7,0),(26,NULL,_binary '\0',_binary '\0',NULL,1300,5,7,0),(27,NULL,_binary '\0',_binary '\0',NULL,0,2,7,0),(28,NULL,_binary '\0',_binary '\0',NULL,0,1,7,0),(29,NULL,_binary '\0',_binary '\0',NULL,0,3,7,0),(30,'11:58:20',_binary '\0',_binary '\0',-1,1000,6,5,0),(31,NULL,_binary '\0',_binary '',NULL,-1000,5,8,0),(32,NULL,_binary '\0',_binary '\0',NULL,0,1,8,0),(33,NULL,_binary '\0',_binary '\0',NULL,-2000,6,8,0),(34,NULL,_binary '\0',_binary '\0',NULL,0,4,8,0),(35,NULL,_binary '\0',_binary '\0',NULL,0,2,8,0),(36,NULL,_binary '\0',_binary '\0',NULL,0,3,8,0),(37,NULL,_binary '\0',_binary '',NULL,-300,5,9,0),(38,NULL,_binary '\0',_binary '\0',NULL,0,2,9,0),(39,NULL,_binary '\0',_binary '\0',NULL,0,6,9,0),(40,NULL,_binary '\0',_binary '\0',NULL,0,4,9,0),(41,NULL,_binary '\0',_binary '',NULL,500,5,10,0),(42,NULL,_binary '\0',_binary '\0',NULL,0,2,10,0),(43,NULL,_binary '\0',_binary '\0',NULL,0,6,10,0),(44,NULL,_binary '\0',_binary '\0',NULL,100,4,10,0),(45,NULL,_binary '\0',_binary '',NULL,0,4,11,0),(46,NULL,_binary '\0',_binary '\0',NULL,0,2,11,0),(47,NULL,_binary '\0',_binary '\0',NULL,0,3,11,0),(48,NULL,_binary '\0',_binary '\0',NULL,0,5,11,0),(49,NULL,_binary '\0',_binary '\0',NULL,0,6,11,0),(50,NULL,_binary '\0',_binary '\0',NULL,0,1,11,0),(51,'21:08:07',_binary '\0',_binary '',8,-1000,2,12,0),(52,'20:56:22',_binary '\0',_binary '\0',-3,500,3,12,0),(53,'21:10:11',_binary '\0',_binary '\0',10,-1000,4,12,0),(54,'20:57:10',_binary '\0',_binary '\0',-2,200,5,12,0),(55,'20:58:20',_binary '\0',_binary '\0',-1,1300,1,12,0),(56,'18:00:00',_binary '\0',_binary '',0,7000,3,13,0),(57,'18:01:11',_binary '\0',_binary '\0',1,4000,1,13,0),(58,'18:02:12',_binary '\0',_binary '\0',2,-2000,2,13,0),(59,'18:03:56',_binary '\0',_binary '\0',3,-2000,4,13,0),(60,'18:04:01',_binary '\0',_binary '\0',4,-3000,5,13,0),(61,'18:05:30',_binary '\0',_binary '\0',5,-4000,6,13,0),(62,NULL,_binary '\0',_binary '',NULL,0,3,14,0),(63,NULL,_binary '\0',_binary '\0',NULL,0,2,14,0),(64,NULL,_binary '\0',_binary '\0',NULL,0,4,14,0),(65,NULL,_binary '\0',_binary '\0',NULL,0,5,14,0),(66,NULL,_binary '\0',_binary '\0',NULL,0,6,14,0),(67,NULL,_binary '\0',_binary '',NULL,0,1,15,0),(68,NULL,_binary '\0',_binary '\0',NULL,0,2,15,0),(69,NULL,_binary '\0',_binary '\0',NULL,0,3,15,0),(81,'18:25:00',_binary '',_binary '\0',-5,2000,3,1,0),(83,NULL,_binary '\0',_binary '',NULL,0,6,25,20),(84,NULL,_binary '\0',_binary '\0',NULL,-100,5,25,10),(85,NULL,_binary '\0',_binary '\0',NULL,0,1,25,10),(86,NULL,_binary '\0',_binary '',NULL,0,5,26,20),(87,NULL,_binary '\0',_binary '\0',NULL,0,4,26,10),(88,NULL,_binary '\0',_binary '',NULL,0,6,27,20),(89,NULL,_binary '\0',_binary '\0',NULL,0,4,27,10),(90,NULL,_binary '\0',_binary '\0',NULL,0,5,27,10),(91,NULL,_binary '\0',_binary '',NULL,0,6,28,20),(92,NULL,_binary '\0',_binary '\0',NULL,0,4,28,10),(93,NULL,_binary '\0',_binary '\0',NULL,0,5,28,10),(94,NULL,_binary '\0',_binary '',NULL,1700,6,29,20),(95,NULL,_binary '\0',_binary '\0',NULL,0,5,29,10),(96,NULL,_binary '\0',_binary '\0',NULL,0,4,29,10),(97,NULL,_binary '\0',_binary '',NULL,0,4,30,20),(98,NULL,_binary '\0',_binary '\0',NULL,0,1,30,10),(99,NULL,_binary '\0',_binary '\0',NULL,0,2,30,10),(100,NULL,_binary '\0',_binary '\0',NULL,0,3,30,10),(101,NULL,_binary '\0',_binary '\0',NULL,0,5,30,10),(102,NULL,_binary '\0',_binary '\0',NULL,0,6,30,10),(103,NULL,_binary '\0',_binary '',NULL,0,4,31,20),(104,NULL,_binary '\0',_binary '\0',NULL,0,1,31,10),(105,NULL,_binary '\0',_binary '\0',NULL,0,2,31,10),(106,NULL,_binary '\0',_binary '\0',NULL,0,3,31,10),(107,NULL,_binary '\0',_binary '\0',NULL,0,5,31,10),(108,NULL,_binary '\0',_binary '\0',NULL,0,6,31,10);
/*!40000 ALTER TABLE `participant` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-19 11:38:56
