-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 27, 2024 at 05:37 AM
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
(24, '0273507635773943', '152', '2028-05-18', 'Debit'),
(0, '1234567890123456', '123', '2027-05-12', 'Debit'),
(25, '2246729492053647', '862', '2028-05-18', 'Debit'),
(22, '2677149033192237', '315', '2028-05-18', 'Debit'),
(27, '6320835450338915', '640', '2028-05-18', 'Debit'),
(21, '6372570157932878', '883', '2028-05-18', 'Debit'),
(32, '6778149843867845', '357', '2028-05-23', 'Debit'),
(31, '6806955120755399', '408', '2028-05-23', 'Debit'),
(26, '6968896652781313', '530', '2028-05-18', 'Debit'),
(10, '8765432109080807', '321', '2027-05-12', 'Debit'),
(23, '9970296003424844', '902', '2028-05-18', 'Debit');

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
  `userId` int(11) NOT NULL,
  `tofrom_userID` int(255) NOT NULL,
  `transactionType` enum('Debit','Credit') NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `transactionDate` date NOT NULL,
  `category` enum('Food','Grocery','Shopping','Transportation','Entertainment','Utilities','Other') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transactionId`, `userId`, `tofrom_userID`, `transactionType`, `amount`, `balance`, `transactionDate`, `category`) VALUES
(12345, 10, 12365, 'Debit', 50.00, 12.00, '2024-05-18', 'Food'),
(12346, 10, 12546, 'Credit', 23.00, 33.00, '2024-05-18', 'Utilities'),
(12367, 10, 12365, 'Debit', 50.00, 34.00, '2024-05-18', 'Transportation'),
(23455, 10, 12365, 'Credit', 50.00, 0.00, '2024-05-20', 'Other'),
(34643, 10, 67890, 'Debit', 70.00, 34.00, '2024-05-19', 'Entertainment'),
(45780, 10, 67890, 'Debit', 70.00, 34.00, '2024-05-19', 'Entertainment'),
(65564, 10, 67890, 'Debit', 70.00, 34.00, '2024-05-20', 'Utilities'),
(87854, 10, 67890, 'Credit', 999.00, 96.00, '2024-05-19', 'Food');

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
(10, 'jwn', 'Wong', 'jingwen0421@gmail.com', 'Jw000000!', '2004-04-21', 'kk8', '01127365038', 'Platinum_Patronus', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\deleteIcon.png', 'Knut'),
(21, 'balaa', 'baaa', 'baa@gmail.com', 'Baa123456!', '2024-05-01', 'daw', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\e-gringotts\\src\\img\\1979790.jpg', 'Sickle'),
(22, 'hahaha', 'hahaha', 'hahaha@gmail.com', 'Ha123456!', '2024-04-30', 'hasid', '1234', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\address.png', 'Knut'),
(23, 'heihei', 'heihei', 'heihei@gmail.com', 'Hei123456!', '2024-04-30', 'das', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\address.png', 'Knut'),
(24, 'ok', 'okok', 'ok@gmail.com', 'Ok123456!', '2024-05-01', 'acs', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\address.png', 'Knut'),
(25, 'last', 'hopefully', 'last@gmail.com', 'Last123456!', '2024-05-01', 'abc', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\addToCartIcon.png', 'Knut'),
(26, 'last2', 'last2', 'last2@gmail.com', 'Last123456!', '2024-05-03', 'abc', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\address.png', 'Knut'),
(27, 'lasttt', 'lasttt', 'last@gmail.com', 'Last123456!', '2024-05-02', 'abc', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\calendar.png', 'Sickle'),
(31, 'hualaa', 'Hua', 'jingwen0421@gmail.com', '$2a$10$TJwkJr6TC4Yfb3/MxTqMrOVcROV0/88950Zh87H9S/Fi69Cpff9Y6', '2024-04-30', 'abc', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\cheapest.png', 'Knut'),
(32, 'idk', 'IDK', '23004924@siswa.um.edu.my', 'Jw000000!', '2024-04-29', 'abc', '123', 'Silver_Snitch', 'C:\\Users\\jwenn\\Desktop\\CODE\\design\\src\\main\\java\\com\\example\\design\\Images\\cheapest.png', 'Knut');

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
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transactionId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87855;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
