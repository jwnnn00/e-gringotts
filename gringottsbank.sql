-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 11, 2024 at 11:45 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gringottsbank`
--

-- --------------------------------------------------------

--
-- Table structure for table `card`
--

CREATE TABLE `card` (
  `userId` int(11) DEFAULT NULL,
  `cardNum` varchar(255) NOT NULL,
  `cvv` varchar(255) NOT NULL,
  `expiryDate` date NOT NULL,
  `cardType` enum('Credit','Debit') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `card`
--

INSERT INTO `card` (`userId`, `cardNum`, `cvv`, `expiryDate`, `cardType`) VALUES
(3, '6595580716115509', '787', '2028-05-11', 'Debit');

-- --------------------------------------------------------

--
-- Table structure for table `conversion`
--

CREATE TABLE `conversion` (
  `currency` enum('Knut','Sickle','Galleon') NOT NULL,
  `to_knut` double NOT NULL,
  `to_sickle` double NOT NULL,
  `to_galleon` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transactionId` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `tofrom_userID` varchar(255) NOT NULL,
  `transactionType` enum('Debit','Credit') NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `transactionDate` datetime NOT NULL,
  `category` enum('Food','Grocery','Shopping','Transportation','Entertainment','Utilities','Other') NOT NULL,
  `paymentMethod` enum('Credit Card','Debit Card','Cash') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `userId` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `fullName` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `DOB` date NOT NULL,
  `address` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) NOT NULL,
  `userType` enum('Platinum_Patronus','Silver_Snitch','Golden_Galleon','Goblin') NOT NULL,
  `avatarImagePath` varchar(255) DEFAULT NULL,
  `currency` enum('Knut','Sickle','Galleon','') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userId`, `username`, `fullName`, `email`, `password`, `DOB`, `address`, `phoneNumber`, `userType`, `avatarImagePath`, `currency`) VALUES
(0, 'admin', 'admin', 'jingwen0421@gmail.com', '123456', '2024-05-11', 'harry potter', '3377889', 'Goblin', 'adhuiqwehqw', 'Knut'),
(1, 'jwn', 'jingwen', '20873kadsbkj', 'weohqwdnwj', '2000-09-08', 'iaojdqhodquwq', 'jwiodjqowiwq', 'Silver_Snitch', 'qjowdwqwohuqh', 'Knut'),
(3, 'jingwen', 'WJW', 'nhdihw@eihdwi.com', 'Jw123456!', '1999-11-30', 'jaoisjdqw', 'iejweqsda', 'Silver_Snitch', 'jioejdoiqwq', 'Knut');

-- --------------------------------------------------------

--
-- Table structure for table `useravatar`
--

CREATE TABLE `useravatar` (
  `avatarId` int(11) NOT NULL,
  `imagePath` varchar(255) NOT NULL,
  `userId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `card`
--
ALTER TABLE `card`
  ADD PRIMARY KEY (`cardNum`),
  ADD KEY `userId` (`userId`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transactionId`),
  ADD KEY `userId` (`userId`),
  ADD KEY `tofrom_userID` (`tofrom_userID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `useravatar`
--
ALTER TABLE `useravatar`
  ADD PRIMARY KEY (`avatarId`),
  ADD KEY `userId` (`userId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transactionId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `useravatar`
--
ALTER TABLE `useravatar`
  MODIFY `avatarId` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `card`
--
ALTER TABLE `card`
  ADD CONSTRAINT `card_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`);

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`);

--
-- Constraints for table `useravatar`
--
ALTER TABLE `useravatar`
  ADD CONSTRAINT `useravatar_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
