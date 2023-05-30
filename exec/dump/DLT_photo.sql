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
-- Table structure for table `photo`
--

DROP TABLE IF EXISTS `photo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo` (
  `photo_id` bigint NOT NULL AUTO_INCREMENT,
  `photo_url` varchar(2500) DEFAULT NULL,
  `register_dt` datetime DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  PRIMARY KEY (`photo_id`),
  KEY `FKq74lv9gtamyu1xhfpq8m1tc56` (`plan_id`),
  CONSTRAINT `FKq74lv9gtamyu1xhfpq8m1tc56` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `photo`
--

LOCK TABLES `photo` WRITE;
/*!40000 ALTER TABLE `photo` DISABLE KEYS */;
INSERT INTO `photo` VALUES (1,'https://kr.object.ncloudstorage.com/dlt/photo/18d6aeb3-9d0c-4c15-b090-4796572c560f타코야키.jpg','2023-05-18 14:20:30',1),(2,'https://kr.object.ncloudstorage.com/dlt/photo/bfa0ba9e-7bde-4435-a152-a0d31ee512ea인터불고.jpg','2023-05-18 14:23:40',2),(3,'https://kr.object.ncloudstorage.com/dlt/photo/12a8b928-e090-4089-86be-b57038badb9f구미역.jpg','2023-05-18 14:26:59',6),(4,'https://kr.object.ncloudstorage.com/dlt/photo/302c1f85-923c-4348-b21d-8abfed116ad1compressed_image874491296920689114.jpg','2023-05-18 17:17:50',7),(5,'https://kr.object.ncloudstorage.com/dlt/photo/dbac8092-cfc5-4124-904f-07995578de58compressed_image1582325941613499540.jpg','2023-05-19 02:48:42',14),(6,'https://kr.object.ncloudstorage.com/dlt/photo/12f8bb40-42db-45ab-a596-8c762f10d521compressed_image4722851194623425012.jpg','2023-05-19 07:40:34',8);
/*!40000 ALTER TABLE `photo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-19 11:38:59
