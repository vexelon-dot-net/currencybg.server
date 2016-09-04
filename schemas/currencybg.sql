/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cbg_apikeys`
--

DROP TABLE IF EXISTS `cbg_apikeys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbg_apikeys` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `KEY_VALUE` varchar(64) NOT NULL,
  `STATUS` int(11) NOT NULL DEFAULT '1' COMMENT '0-Active, 1-Inactive',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbg_currencies`
--

DROP TABLE IF EXISTS `cbg_currencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbg_currencies` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CODE` varchar(3) NOT NULL,
  `RATIO` int(11) NOT NULL,
  `BUY` varchar(10) NOT NULL,
  `SELL` varchar(10) NOT NULL,
  `DATE` datetime NOT NULL,
  `SOURCE` int(3) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbg_sources`
--

DROP TABLE IF EXISTS `cbg_sources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbg_sources` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SOURCE_ID` int(3) NOT NULL,
  `NAME` varchar(150) NOT NULL,
  `STATUS` int(1) NOT NULL DEFAULT '1' COMMENT '0-Active/1-Inactive',
  `UPDATE_PERIOD` int(11) NOT NULL DEFAULT '360' COMMENT 'Update interval in seconds',
  `UPDATE_RESTRICTIONS` varchar(1000) DEFAULT '{ 	"wdNotBefore": "06:00", 	"wdNotAfter": "23:00", 	"weNotBefore": "06:00", 	"weNotAfter": "23:00",	 	"weekends": true, 	"sundays": false }',
  `LAST_UPDATE` datetime NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-04 11:26:17
