-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: localhost    Database: mydb
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
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `idcourse` int NOT NULL,
  `title` varchar(45) DEFAULT NULL,
  `teacher` int NOT NULL,
  PRIMARY KEY (`idcourse`),
  KEY `fk_course_user_idx` (`teacher`),
  CONSTRAINT `fk_course_user` FOREIGN KEY (`teacher`) REFERENCES `user` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'Tiw',1),(2,'Analisi 1',3),(3,'Databases',1),(4,'Analisi 2',3);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enroll`
--

DROP TABLE IF EXISTS `enroll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enroll` (
  `student_student_number` int NOT NULL,
  `course_idcourse` int NOT NULL,
  PRIMARY KEY (`student_student_number`,`course_idcourse`),
  KEY `fk_student_has_course_course1_idx` (`course_idcourse`),
  KEY `fk_student_has_course_student1_idx` (`student_student_number`),
  CONSTRAINT `fk_student_has_course_course1` FOREIGN KEY (`course_idcourse`) REFERENCES `course` (`idcourse`),
  CONSTRAINT `fk_student_has_course_student1` FOREIGN KEY (`student_student_number`) REFERENCES `student` (`student_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enroll`
--

LOCK TABLES `enroll` WRITE;
/*!40000 ALTER TABLE `enroll` DISABLE KEYS */;
INSERT INTO `enroll` VALUES (2,1),(4,1),(5,1),(2,2),(5,2),(4,3),(5,3);
/*!40000 ALTER TABLE `enroll` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `idreport` int NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT (now()),
  `session_idsession` int DEFAULT NULL,
  PRIMARY KEY (`idreport`),
  UNIQUE KEY `report_session_idsession_fk` (`session_idsession`),
  CONSTRAINT `report_session_idsession_fk` FOREIGN KEY (`session_idsession`) REFERENCES `session` (`idsession`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `result`
--

DROP TABLE IF EXISTS `result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `result` (
  `student_student_number` int NOT NULL,
  `session_idsession` int NOT NULL,
  `grade` int DEFAULT NULL,
  `state` int DEFAULT '0',
  `isRefused` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`student_student_number`,`session_idsession`),
  KEY `fk_student_has_session_session1_idx` (`session_idsession`),
  KEY `fk_student_has_session_student1_idx` (`student_student_number`),
  CONSTRAINT `fk_student_has_session_session1` FOREIGN KEY (`session_idsession`) REFERENCES `session` (`idsession`),
  CONSTRAINT `fk_student_has_session_student1` FOREIGN KEY (`student_student_number`) REFERENCES `student` (`student_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `result`
--

LOCK TABLES `result` WRITE;
/*!40000 ALTER TABLE `result` DISABLE KEYS */;
INSERT INTO `result` VALUES (2,1,0,0,0),(2,2,0,0,0),(4,1,0,0,0),(4,3,0,0,0);
/*!40000 ALTER TABLE `result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session` (
  `idsession` int NOT NULL AUTO_INCREMENT,
  `course_idcourse` int NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`idsession`),
  KEY `fk_session_course1` (`course_idcourse`),
  CONSTRAINT `fk_session_course1` FOREIGN KEY (`course_idcourse`) REFERENCES `course` (`idcourse`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session`
--

LOCK TABLES `session` WRITE;
/*!40000 ALTER TABLE `session` DISABLE KEYS */;
INSERT INTO `session` VALUES (1,1,'2023-08-10 10:00:00'),(2,1,'2023-08-12 10:00:00'),(3,2,'2023-08-15 14:00:00'),(4,3,'2023-08-17 11:00:00'),(5,1,'2023-08-20 10:00:00'),(6,2,'2023-08-22 14:00:00'),(7,3,'2023-08-25 11:00:00'),(8,4,'2023-08-27 09:00:00');
/*!40000 ALTER TABLE `session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_number` int NOT NULL,
  `school` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`student_number`),
  KEY `fk_student_user1_idx` (`student_number`),
  CONSTRAINT `fk_student_user1` FOREIGN KEY (`student_number`) REFERENCES `user` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (2,'Ingegneria Informatica'),(4,'Ingegneria Eletronica'),(5,'Ingegneria Civile');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `login` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `surname` varchar(45) DEFAULT NULL,
  `password` varchar(32) NOT NULL,
  `role` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Giovanni','Rossi','password1','teacher','giovanni.rossi@example.com'),(2,'Maria','Verdi','password2','student','maria.verdi@example.com'),(3,'Alessio','Bianchi','password3','teacher','alessio.bianchi@example.com'),(4,'Laura','Ferrari','password4','student','laura.ferrari@example.com'),(5,'Marco','Neri','password5','student','marco.neri@example.com');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-08-07  1:28:00
