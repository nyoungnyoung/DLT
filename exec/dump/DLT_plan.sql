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
-- Table structure for table `plan`
--

DROP TABLE IF EXISTS `plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan` (
  `plan_id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `cost` int DEFAULT NULL,
  `is_settle` bit(1) DEFAULT b'0',
  `latitude` double DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `plan_date` date DEFAULT NULL,
  `plan_time` time DEFAULT NULL,
  `state` int NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plan`
--

LOCK TABLES `plan` WRITE;
/*!40000 ALTER TABLE `plan` DISABLE KEYS */;
INSERT INTO `plan` VALUES (1,'경상북도 구미시 진평동 446',5000,_binary '\0',36.1039,'가을아침',128.4186,'2023-05-10','18:30:00',3,'우리집에서 타코야키'),(2,'대구광역시 수성구 만촌동 300',5000,_binary '\0',35.8745,'호텔 인터불고',128.6599,'2023-05-13','16:30:00',3,'뷔페먹으러 가자~~'),(3,'대구광역시 동구 동부로 149',100,_binary '\0',35.8778,'신세계 백화점',128.6288,'2023-05-19','14:45:00',0,'봉봉타기'),(4,'대구광역시 신천동 295-4번지 동구',500,_binary '',35.8705,'만복이쭈꾸미낙지볶음 동대구점',128.6264,'2023-05-18','19:30:00',3,'쭈꾸미 회식하자!'),(5,'경북 구미시 구미중앙로 76',4000,_binary '',36.1282722213369,'구미역',128.331016235023,'2023-05-14','12:00:00',3,'구미역에서 점심 먹자!'),(6,'경북 구미시 구미중앙로 76',3000,_binary '\0',36.1282722213369,'구미역',128.331016235023,'2023-05-17','23:16:00',3,'야근하고 동대구 막차타고 가실분~'),(7,'경북 구미시 3공단3로 302',1000,_binary '',36.107366259336,'삼성전자2공장 후문',128.417229308225,'2023-05-18','15:00:00',3,'교육장 앞으로 집합'),(8,'경북 구미시 3공단3로 302',1000,_binary '\0',36.107366259336,'삼성전자2공장 후문',128.417229308225,'2023-05-19','10:50:00',2,'즐거운 유니티'),(9,'경북 김천시 남면 혁신1로 51',300,_binary '',36.1135300768872,'김천(구미)역',128.181084377015,'2023-05-17','18:30:00',3,'막창 먹자'),(10,'경북 김천시 남면 혁신1로 51',300,_binary '',36.1135300768872,'김천(구미)역',128.181084377015,'2023-05-18','17:18:00',3,'구미모여라'),(11,'경북 구미시 인동가산로 24',1000,_binary '\0',36.10639834734288,'투썸플레이스 구미인동점',128.41875993139388,'2023-05-19','20:30:00',0,'생성테스트'),(12,'대구 수성구 동원로 100',1000,_binary '',35.86842558924157,'메트로팔레스5단지아파트',128.64018513088118,'2023-05-16','21:00:00',3,'집에갈래'),(13,'대구광역시 수성구 만촌동 300',5000,_binary '\0',35.8745,'호텔 인터불고',128.6599,'2023-05-14','18:00:00',3,'뷔페먹을래~~~'),(14,'대구광역시 신천동 295-4번지 동구',100,_binary '',35.8705,'만복이쭈꾸미낙지볶음 동대구점',128.6264,'2023-05-19','10:40:00',3,'쭈꾸미 회식하자!'),(15,'대구광역시 동구 동부로 149',100,_binary '\0',35.8778,'신세계 백화점',128.6288,'2023-05-19','14:45:00',0,'봉봉타기'),(25,'충북 청주시 청원구 율량로 96',100,_binary '\0',36.67216312090818,'캡틴마블수학학원',127.49660776459791,'2023-05-19','07:30:00',3,'생성테스트'),(26,'경북 구미시 인동중앙로3길 29',50000,_binary '\0',36.108668348843054,'단골손님 구미인동점',128.41867971497396,'2023-05-19','18:00:00',0,'도파민즈 쫑파티'),(27,'대구 동구 동대구로 448',3000,_binary '\0',35.87027753189529,'만복이쭈꾸미낙지볶음 동대구점',128.62626114879956,'2023-05-22','18:30:00',0,'구미 자율 프로젝트 회식'),(28,'경북 구미시 인동가산로 14',3000,_binary '\0',36.1071765439437,'메가박스 구미강동',128.417878870048,'2023-05-27','02:00:00',0,'새로운 약속 생성'),(29,'경북 구미시 인동가산로 14',1000,_binary '',36.1071765439437,'삼성전자 2공장 후문',128.417878870048,'2023-05-19','10:20:00',3,'도파민즈'),(30,'경북 구미시 3공단3로 302',5000,_binary '\0',36.107366259336,'삼성전자2공장 후문',128.417229308225,'2023-05-19','18:00:00',0,'프로젝트 끝!!'),(31,'대구 북구 태평로 161',5000,_binary '\0',35.8759487657024,'대구역',128.596277196438,'2023-05-19','18:30:00',0,'집으로');
/*!40000 ALTER TABLE `plan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-19 11:38:55
