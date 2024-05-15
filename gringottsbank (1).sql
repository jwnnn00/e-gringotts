-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 15, 2024 at 10:14 AM
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
(10, '8875903051416354', '236', '2028-05-14', 'Debit');

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

--
-- Dumping data for table `conversion`
--

INSERT INTO `conversion` (`currency`, `to_knut`, `to_sickle`, `to_galleon`) VALUES
('Knut', 1, 29, 493),
('Sickle', 0.03448, 1, 17),
('Galleon', 0.002028, 0.05882, 1);

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transactionId` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `tofrom_userID` int(255) NOT NULL,
  `transactionType` enum('Debit','Credit') NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `transactionDate` datetime NOT NULL,
  `category` enum('Food','Grocery','Shopping','Transportation','Entertainment','Utilities','Other') NOT NULL
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
(0, 'admin', 'admin', 'jingwen0421@gmail.com', '123456', '2024-05-11', 'harry potter', '3377889', 'Goblin', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\USER_ICON.png', 'Knut'),
(10, 'jwn', 'Wong', 'jingwen0421@gmail.com', 'Jw000000!', '2004-04-21', 'kk8', '01127365038', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\deleteIcon.png', 'Knut');

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
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

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
