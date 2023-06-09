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
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `register_dt` datetime DEFAULT NULL,
  `update_dt` datetime DEFAULT NULL,
  `participant_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FKje0p0wgldmvw9rtv8jk0y79d5` (`participant_id`),
  KEY `FKp18l0cr84nk867f6s6mjhnosr` (`plan_id`),
  CONSTRAINT `FKje0p0wgldmvw9rtv8jk0y79d5` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`participant_id`) ON DELETE CASCADE,
  CONSTRAINT `FKp18l0cr84nk867f6s6mjhnosr` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'맛있겠다!','2023-05-18 14:58:16','2023-05-18 14:58:16',2,1),(2,'맛팄','2023-05-18 17:09:59','2023-05-18 17:09:59',2,1),(3,'ㅏㅅ','2023-05-18 17:10:03','2023-05-18 17:10:03',2,1),(4,'지각비 마시따','2023-05-18 17:10:09','2023-05-18 17:10:09',25,7),(5,'ㅋㅋㅋ','2023-05-18 17:10:22','2023-05-18 17:10:22',22,6),(6,'주현코치 qa 제대로 하세요','2023-05-18 17:10:29','2023-05-18 17:10:29',23,6),(7,'ㅋㅋㅋㅋ','2023-05-18 17:10:35','2023-05-18 17:10:35',22,6),(8,'ㅋㅋㅋ','2023-05-18 17:18:22','2023-05-18 17:18:22',26,7),(9,'금방 가겠습니다 ㅜㅜ','2023-05-18 17:18:34','2023-05-18 17:18:34',27,7),(10,'??','2023-05-18 17:19:15','2023-05-18 17:19:15',22,6),(11,'댓글','2023-05-19 01:50:13','2023-05-19 01:50:13',22,6),(12,'아\n','2023-05-19 01:51:39','2023-05-19 01:51:39',2,1),(13,'ㅠ','2023-05-19 01:57:28','2023-05-19 01:57:28',2,1),(14,'ㅠ','2023-05-19 01:58:15','2023-05-19 01:58:15',2,1),(15,'어ㅐ','2023-05-19 01:58:56','2023-05-19 01:58:56',2,1),(16,'어','2023-05-19 02:01:54','2023-05-19 02:01:54',38,9),(17,'집이 너무  좋아요','2023-05-19 06:38:51','2023-05-19 06:38:51',65,14),(18,'잠온다...','2023-05-19 06:39:16','2023-05-19 06:39:16',66,14),(19,'집가자','2023-05-19 06:39:40','2023-05-19 06:39:40',65,14),(20,'댓글','2023-05-19 07:14:10','2023-05-19 07:14:10',2,1),(21,'하하하하','2023-05-19 07:40:54','2023-05-19 07:40:54',33,8),(22,'ㄷ9','2023-05-19 09:13:04','2023-05-19 09:13:04',51,12),(23,'댓글','2023-05-19 09:13:12','2023-05-19 09:13:12',2,1),(24,'최고다','2023-05-19 09:45:10','2023-05-19 09:45:10',66,14),(25,'안녕','2023-05-19 11:01:32','2023-05-19 11:01:32',53,12);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-19 11:38:58
