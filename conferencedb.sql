-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 10, 2017 at 02:20 PM
-- Server version: 10.1.25-MariaDB
-- PHP Version: 7.1.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `conferencedb`
--

-- --------------------------------------------------------

--
-- Table structure for table `attend`
--

CREATE TABLE `attend` (
  `memberId` varchar(20) NOT NULL,
  `lectureId` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `attend`
--

INSERT INTO `attend` (`memberId`, `lectureId`) VALUES
('0000000000', '8675690987'),
('0000000001', '6567897656'),
('1111111111', '6567897656'),
('2222222222', '6567897656');

-- --------------------------------------------------------

--
-- Table structure for table `company`
--

CREATE TABLE `company` (
  `companyId` varchar(20) NOT NULL,
  `name` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `company`
--

INSERT INTO `company` (`companyId`, `name`) VALUES
('5678767890', 'Second Company'),
('8675656898', 'First Company');

-- --------------------------------------------------------

--
-- Table structure for table `do`
--

CREATE TABLE `do` (
  `memberId` varchar(20) DEFAULT NULL,
  `transactionId` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `do`
--

INSERT INTO `do` (`memberId`, `transactionId`) VALUES
('0000000000', '1483593141'),
('0000000001', '1483593115'),
('0000000002', '1483593371');

-- --------------------------------------------------------

--
-- Table structure for table `engage`
--

CREATE TABLE `engage` (
  `memberId` varchar(20) NOT NULL,
  `companyId` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `engage`
--

INSERT INTO `engage` (`memberId`, `companyId`) VALUES
('0000000000', '8675656898'),
('1111111111', '5678767890'),
('1111111111', '8675656898');

-- --------------------------------------------------------

--
-- Table structure for table `have`
--

CREATE TABLE `have` (
  `transactionId` varchar(20) NOT NULL,
  `productId` varchar(20) NOT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `have`
--

INSERT INTO `have` (`transactionId`, `productId`, `quantity`) VALUES
('1483593115', '129381293', 3),
('1483593115', '129381923', 4),
('1483593115', '129387192837', 2),
('1483593141', '129381293', 2),
('1483593141', '129381923', 1),
('1483593141', '129387192837', 3),
('1483593371', '129381293', 3),
('1483593371', '129381923', 2),
('1483593371', '129387192837', 1);

-- --------------------------------------------------------

--
-- Table structure for table `lecture`
--

CREATE TABLE `lecture` (
  `lectureId` varchar(20) NOT NULL,
  `title` varchar(20) DEFAULT NULL,
  `duration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `lecture`
--

INSERT INTO `lecture` (`lectureId`, `title`, `duration`) VALUES
('123', '123', 120),
('6567897656', 'Marketing', 120),
('8675690987', 'Programming', 120);

-- --------------------------------------------------------

--
-- Table structure for table `member`
--

CREATE TABLE `member` (
  `memberId` varchar(20) NOT NULL,
  `firstName` varchar(20) DEFAULT NULL,
  `lastName` varchar(20) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL,
  `contactNo` varchar(20) DEFAULT NULL,
  `address` varchar(80) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `position` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `member`
--

INSERT INTO `member` (`memberId`, `firstName`, `lastName`, `gender`, `contactNo`, `address`, `dob`, `position`) VALUES
('0000000000', 'Visitor', 'Zero', 'M', '123', '123', '2017-09-06', 0),
('0000000001', 'Visitor', 'One', 'M', '123', '123', '2017-09-04', 0),
('0000000002', 'Visitor', 'Two', 'M', '123', '123', '2017-09-04', 0),
('1111111111', 'Retailer', 'Company One', 'M', '123', '123', '2017-09-12', 1),
('1111111112', 'Retailer', 'Company Two', 'M', '123', '123', '2017-09-12', 1),
('1232132132', 'akjsd', 'alskdj', 'M', '123123', '1232', '2017-09-08', 0),
('2222222222', 'Receptionist', 'Receptionist', 'M', '123', '123', '2017-09-06', 2),
('3333333333', 'Admin', 'Admin', 'M', '333333', 'Admin Address', '2017-09-03', 3),
('5675675675', 'Retailer', 'Company Two Two', 'M', '123', '123', '2017-09-13', 1),
('6546546546', 'Retailer', 'Company One Two', 'M', '123', '213', '2017-09-13', 1),
('7657657657', 'Retailer', 'Company Two One', 'M', '123', '123', '2017-09-08', 1),
('8768768768', 'Retailer', 'Company One One', 'M', '123', '123', '2017-09-13', 1);

-- --------------------------------------------------------

--
-- Table structure for table `occupy`
--

CREATE TABLE `occupy` (
  `lectureId` varchar(20) DEFAULT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `roomId` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `occupy`
--

INSERT INTO `occupy` (`lectureId`, `date`, `time`, `roomId`) VALUES
('123', '2017-09-10', '10:00:00', '7867890987'),
('6567897656', '2017-09-09', '00:00:00', '5679546789'),
('6567897656', '2017-09-09', '02:00:00', '5679546789'),
('8675690987', '2017-09-10', '00:00:00', '7867890987'),
('8675690987', '2017-09-10', '02:00:00', '7867890987');

-- --------------------------------------------------------

--
-- Table structure for table `own`
--

CREATE TABLE `own` (
  `companyId` varchar(20) NOT NULL,
  `productId` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `own`
--

INSERT INTO `own` (`companyId`, `productId`) VALUES
('5678767890', '127365716235'),
('5678767890', '239847238947'),
('5678767890', '91283981273'),
('8675656898', '129381293'),
('8675656898', '129381923'),
('8675656898', '129387192837');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `productId` varchar(20) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`productId`, `name`, `price`, `stock`) VALUES
('127365716235', 'Coke Can', 5, 10),
('129381293', 'Fanta Bottle', 5, 2),
('129381923', 'Coke Bottle', 5, 3),
('129387192837', 'Sprite Bottle', 5, 4),
('239847238947', 'Sprite Can', 5, 10),
('91283981273', 'Fanta Can', 5, 10);

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

CREATE TABLE `room` (
  `roomId` varchar(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(20) NOT NULL,
  `seat` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `room`
--

INSERT INTO `room` (`roomId`, `name`, `description`, `seat`) VALUES
('5679546789', 'Secondary Room', 'Wing Auditorium', 100),
('7867890987', 'Main Room', 'Main Auditorium', 200);

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transactionId` varchar(20) NOT NULL,
  `total` double DEFAULT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transactionId`, `total`, `date`) VALUES
('1483592779', 25, '2017-09-05'),
('1483593115', 45, '2017-09-05'),
('1483593141', 30, '2017-09-05'),
('1483593371', 30, '2017-09-05');

-- --------------------------------------------------------

--
-- Table structure for table `work`
--

CREATE TABLE `work` (
  `memberId` varchar(20) NOT NULL,
  `companyId` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `work`
--

INSERT INTO `work` (`memberId`, `companyId`) VALUES
('1111111112', '5678767890'),
('5675675675', '5678767890'),
('7657657657', '5678767890'),
('1111111111', '8675656898'),
('8768768768', '8675656898');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attend`
--
ALTER TABLE `attend`
  ADD PRIMARY KEY (`memberId`,`lectureId`),
  ADD KEY `lectureId` (`lectureId`);

--
-- Indexes for table `company`
--
ALTER TABLE `company`
  ADD PRIMARY KEY (`companyId`);

--
-- Indexes for table `do`
--
ALTER TABLE `do`
  ADD PRIMARY KEY (`transactionId`),
  ADD KEY `memberId` (`memberId`);

--
-- Indexes for table `engage`
--
ALTER TABLE `engage`
  ADD PRIMARY KEY (`memberId`,`companyId`),
  ADD KEY `companyId` (`companyId`);

--
-- Indexes for table `have`
--
ALTER TABLE `have`
  ADD PRIMARY KEY (`transactionId`,`productId`),
  ADD KEY `productId` (`productId`);

--
-- Indexes for table `lecture`
--
ALTER TABLE `lecture`
  ADD PRIMARY KEY (`lectureId`);

--
-- Indexes for table `member`
--
ALTER TABLE `member`
  ADD PRIMARY KEY (`memberId`);

--
-- Indexes for table `occupy`
--
ALTER TABLE `occupy`
  ADD PRIMARY KEY (`date`,`time`,`roomId`),
  ADD KEY `lectureId` (`lectureId`),
  ADD KEY `roomId` (`roomId`);

--
-- Indexes for table `own`
--
ALTER TABLE `own`
  ADD PRIMARY KEY (`companyId`,`productId`),
  ADD KEY `productId` (`productId`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`productId`);

--
-- Indexes for table `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`roomId`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transactionId`);

--
-- Indexes for table `work`
--
ALTER TABLE `work`
  ADD PRIMARY KEY (`memberId`),
  ADD KEY `companyId` (`companyId`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attend`
--
ALTER TABLE `attend`
  ADD CONSTRAINT `attend_ibfk_1` FOREIGN KEY (`memberId`) REFERENCES `member` (`memberId`),
  ADD CONSTRAINT `attend_ibfk_2` FOREIGN KEY (`lectureId`) REFERENCES `lecture` (`lectureId`);

--
-- Constraints for table `do`
--
ALTER TABLE `do`
  ADD CONSTRAINT `do_ibfk_1` FOREIGN KEY (`memberId`) REFERENCES `member` (`memberId`),
  ADD CONSTRAINT `do_ibfk_2` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`transactionId`);

--
-- Constraints for table `engage`
--
ALTER TABLE `engage`
  ADD CONSTRAINT `engage_ibfk_1` FOREIGN KEY (`memberId`) REFERENCES `member` (`memberId`),
  ADD CONSTRAINT `engage_ibfk_2` FOREIGN KEY (`companyId`) REFERENCES `company` (`companyId`);

--
-- Constraints for table `have`
--
ALTER TABLE `have`
  ADD CONSTRAINT `have_ibfk_1` FOREIGN KEY (`transactionId`) REFERENCES `transaction` (`transactionId`),
  ADD CONSTRAINT `have_ibfk_2` FOREIGN KEY (`productId`) REFERENCES `product` (`productId`);

--
-- Constraints for table `occupy`
--
ALTER TABLE `occupy`
  ADD CONSTRAINT `occupy_ibfk_1` FOREIGN KEY (`lectureId`) REFERENCES `lecture` (`lectureId`),
  ADD CONSTRAINT `occupy_ibfk_2` FOREIGN KEY (`roomId`) REFERENCES `room` (`roomId`);

--
-- Constraints for table `own`
--
ALTER TABLE `own`
  ADD CONSTRAINT `own_ibfk_1` FOREIGN KEY (`companyId`) REFERENCES `company` (`companyId`),
  ADD CONSTRAINT `own_ibfk_2` FOREIGN KEY (`productId`) REFERENCES `product` (`productId`);

--
-- Constraints for table `work`
--
ALTER TABLE `work`
  ADD CONSTRAINT `work_ibfk_1` FOREIGN KEY (`memberId`) REFERENCES `member` (`memberId`),
  ADD CONSTRAINT `work_ibfk_2` FOREIGN KEY (`companyId`) REFERENCES `company` (`companyId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
