-- MySQL dump 10.13  Distrib 8.0.35, for Linux (x86_64)
--
-- Host: localhost    Database: testDB
-- ------------------------------------------------------
-- Server version	8.0.35-0ubuntu0.23.10.1

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
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `product_category_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PRODUCT_ON_PRODUCT_CATEGORY` (`product_category_id`),
  CONSTRAINT `FK_PRODUCT_ON_PRODUCT_CATEGORY` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (_binary 'H\Ë>ÿL·J«,B•‚„m','Little bunny bag','This is Little bunny bag',3000,_binary '…œoÿÁ{@Ã·}b\È#©ñ¾'),(_binary 'b\É2/~=N Æ¨G\Ñ+½','Chip Chip','This is Chip Chip',100,_binary 'vx¤tV“Ke¬b’rI-ó\Ì'),(_binary '¦d\İR\ÓWJô‘X\î$\"w','Bundle 3 crochet patterns of cats and flowers','This is Bundle 3 crochet patterns of cats and flowers',120,_binary '\é<!\î‡Q@F›\èQ^ı}\Ú'),(_binary '\Ğe\Üı\Ó9JĞR\Ğ!òX','Pew the baby penguin','This is Pew the baby penguin',130,_binary 'vx¤tV“Ke¬b’rI-ó\Ì'),(_binary 'òc±uô\ÙA\ä¬\Ì\n.ˆğ\â¾','Kuku Penguin','This is Kuku Penguin',200,_binary 'vx¤tV“Ke¬b’rI-ó\Ì'),(_binary 'øc‘³Kƒ´Å˜\0\ÓG|p','Lovely crochet water bottle holder','This is Lovely crochet water bottle holder',1500,_binary '…œoÿÁ{@Ã·}b\È#©ñ¾');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-10 21:15:21
