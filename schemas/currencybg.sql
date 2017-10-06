-- phpMyAdmin SQL Dump
-- version 4.0.10.12
-- http://www.phpmyadmin.net
--
-- Хост: 127.5.91.2:3306
-- Време на генериране: 26 май 2017 в 18:02
-- Версия на сървъра: 5.5.52
-- Версия на PHP: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- БД: `currencybg`
--

-- --------------------------------------------------------

--
-- Структура на таблица `cbg_apikeys`
--

CREATE TABLE IF NOT EXISTS `cbg_apikeys` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `KEY_VALUE` varchar(64) NOT NULL,
  `STATUS` int(11) NOT NULL DEFAULT '1' COMMENT '0-Active, 1-Inactive',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура на таблица `cbg_currencies`
--

CREATE TABLE IF NOT EXISTS `cbg_currencies` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CODE` varchar(10) NOT NULL,
  `RATIO` int(11) NOT NULL,
  `BUY` varchar(10) NOT NULL,
  `SELL` varchar(10) NOT NULL,
  `DATE` datetime NOT NULL,
  `SOURCE` int(3) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Структура на таблица `cbg_reports`
--

CREATE TABLE IF NOT EXISTS `cbg_reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createdon` datetime NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура на таблица `cbg_sources`
--

CREATE TABLE IF NOT EXISTS `cbg_sources` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SOURCE_ID` int(3) NOT NULL,
  `NAME` varchar(150) NOT NULL,
  `STATUS` int(1) NOT NULL DEFAULT '1' COMMENT '0-Active/1-Inactive',
  `UPDATE_PERIOD` int(11) NOT NULL DEFAULT '360' COMMENT 'Update interval in seconds',
  `UPDATE_RESTRICTIONS` varchar(1000) DEFAULT '{ 	"wdNotBefore": "06:00", 	"wdNotAfter": "23:00", 	"weNotBefore": "06:00", 	"weNotAfter": "23:00",	 	"weekends": true, 	"sundays": false }',
  `LAST_UPDATE` datetime NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
