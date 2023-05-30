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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` bigint NOT NULL AUTO_INCREMENT,
  `mod_dt` datetime DEFAULT NULL,
  `reg_dt` datetime DEFAULT NULL,
  `accumulated_time` int NOT NULL DEFAULT '0',
  `email` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `kakao_id` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `profile` varchar(2500) DEFAULT NULL,
  `profile_message` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `thyme` int NOT NULL DEFAULT '0',
  `total_in` int NOT NULL DEFAULT '0',
  `total_out` int NOT NULL DEFAULT '0',
  `total_wallet` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UK_q0uja26qgu1atulenwup9rxyr` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,NULL,NULL,-4,'lce511@naver.com',_binary '\0','$2a$10$Aqg4n6JTlMeemmCzfWz2ZeWa/1XiLOIQmQ9XEvkm4lXFAlf/JCRU6','채니','https://kr.object.ncloudstorage.com/dlt/profile/189893a0-793b-4495-a42a-61f589c2400edororo.jpg',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsY2U1MTFAbmF2ZXIuY29tIiwiZXhwIjoxNzM4NDM2MTgzLCJpYXQiOjE2ODQ0MzYxODN9._R2Ppv4dYqjypke3sMfK2Lligjn3Vg5C9e3qpYJK9BI',2580,18300,-3000,65800),(2,NULL,NULL,31,'pshnoran@naver.com',_binary '\0','$2a$10$4U24wC6QaVNHzzr62hLBge3gnZswhxVLMp9CQhMzprU73cLlWJBZ6','소현','http://k.kakaocdn.net/dn/n5e4A/btqyKI1bKJm/mK8LQlWTLmORKrZdgX0MW1/img_640x640.jpg','새콤달콤을 좋아합니다!','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwc2hub3JhbkBuYXZlci5jb20iLCJleHAiOjE3Mzg0NjM3MjksImlhdCI6MTY4NDQ2MzcyOX0.p3ZqydTlZi8pifvBt2JOYPFwCoc6QL-pxC9ALSWVp4c',510,3000,-6000,200),(3,NULL,NULL,-33,'sa01023@naver.com',_binary '\0','$2a$10$2R/s3tNp3n2r4PKuGMvW9Oxlwk41kyGnzk8jV3io/9EdKEjQW6m/.','부경','http://k.kakaocdn.net/dn/jtc4n/btr0xQXXoTP/139KCXLDgrW7ddfzm7ZLM0/img_640x640.jpg',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYTAxMDIzQG5hdmVyLmNvbSIsImV4cCI6MTczODM5NDIyNywiaWF0IjoxNjg0Mzk0MjI3fQ.7t2ikzYLE3bMcBDlDiR901KcmGRWBazUXIYJZkkuixg',370,6500,0,6500),(4,NULL,NULL,24,'sa7551@naver.com',_binary '\0','$2a$10$qMA.B7xkiAbhJYUgkpXmZeRGCUxBrxNlm0Nyfh6mghwyr4rLLzApe','선영','http://k.kakaocdn.net/dn/MLZPR/btrU1qw0177/sD4KfP2VKMqoBIy4MQJ200/img_640x640.jpg',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYTc1NTFAbmF2ZXIuY29tIiwiZXhwIjoxNzM4NDYzODg3LCJpYXQiOjE2ODQ0NjM4ODd9.CDPOjrjDK2KCcT5B8kMPGTUHDy758n47uWQhMFwHtB4',410,7200,-2000,4100),(5,NULL,NULL,22,'ljc9393@nate.com',_binary '\0','$2a$10$.cpPpf0RdDh0S7RoUGbU5uJDl1PkNUtSl0k1k5xr60wjfzPli2.Hm','공주','http://k.kakaocdn.net/dn/eqDJwl/btrSarMaEox/tDkdgDShgNHSZiwmTGbCA0/img_640x640.jpg',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsamM5MzkzQG5hdGUuY29tIiwiZXhwIjoxNzM4NDYyNjU1LCJpYXQiOjE2ODQ0NjI2NTV9.P7gap8u3zOfoRsPV7hWA6cjFRL12mxQNY7ePey-foHk',340,5800,-9600,800),(6,NULL,NULL,4,'dpdms2148@naver.com',_binary '\0','$2a$10$U.VphsrWJYMLie1tdW0Bq.vnA9ahzO4CI/DkpTkOoeHg1IaYhxHY.','짱예','http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkcGRtczIxNDhAbmF2ZXIuY29tIiwiZXhwIjoxNzM4NDYzNDY5LCJpYXQiOjE2ODQ0NjM0Njl9.WXMlPMwkgQnsXmsVDTYAX_rf3FcHDSwktXMRuz9TNWk',20,5000,-6500,5700);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-19 11:39:00
