-- 1. User类
CREATE TABLE `User` (
  `userID` varchar(255) NOT NULL,
  `userPassword` varchar(255) NOT NULL,
  `UserType` varchar(255) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. OrdinaryUser类
CREATE TABLE `OrdinaryUser` (
  `userID` varchar(255) NOT NULL,
  `userPassword` varchar(255) NOT NULL,
  `UserType` varchar(255) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `Phonenumber` varchar(255) DEFAULT NULL,
  `userMailbox` varchar(255) DEFAULT NULL,
  `RealName` varchar(255) DEFAULT NULL,
  `Gender` varchar(255) DEFAULT NULL,
  `Degree` varchar(255) DEFAULT NULL,
  `School` varchar(255) DEFAULT NULL,
  `RegisterTime` datetime DEFAULT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Administrator类
CREATE TABLE `Administrator` (
  `UserID` varchar(255) NOT NULL,
  `userPassword` varchar(255) NOT NULL,
  `userType` varchar(255) NOT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Club类[cite: 2]
CREATE TABLE `Club` (
  `Clubid` varchar(255) NOT NULL,
  `UserID` varchar(255) NOT NULL,
  `ClubName` varchar(255) NOT NULL,
  `Clubinformation` text,
  `School` varchar(255) DEFAULT NULL,
  `ClubCategory` varchar(255) DEFAULT NULL,
  `ClubCoverImage` varchar(255) DEFAULT NULL,
  `ClubState` varchar(255) DEFAULT NULL,
  `EstablishmentTime` datetime DEFAULT NULL,
  PRIMARY KEY (`Clubid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. ClubActivity类[cite: 2]
CREATE TABLE `ClubActivity` (
  `ActivityID` varchar(255) NOT NULL,
  `ClubID` varchar(255) NOT NULL,
  `UserID` varchar(255) NOT NULL,
  `Title` varchar(255) NOT NULL,
  `Content` text,
  `CapacityLimit` int DEFAULT NULL,
  `ActivityState` varchar(255) DEFAULT NULL,
  `PublishTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ActivityID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. ClubMember类[cite: 2]
CREATE TABLE `ClubMember` (
  `ClubID` varchar(255) NOT NULL,
  `UserID` varchar(255) NOT NULL,
  `ReviewState` varchar(255) DEFAULT NULL,
  `ClubManager` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ClubID`, `UserID`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. RegistrationInfo类[cite: 2]
CREATE TABLE `RegistrationInfo` (
  `RegistrationID` varchar(255) NOT NULL,
  `ActivityID` varchar(255) NOT NULL,
  `UserID` varchar(255) NOT NULL,
  `RealName` varchar(255) DEFAULT NULL,
  `PhoneNumer` varchar(255) DEFAULT NULL,
  `ReviewState` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`RegistrationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. RatingClub类[cite: 2]
CREATE TABLE `RatingClub` (
  `RatingID` varchar(255) NOT NULL,
  `ClubID` varchar(255) NOT NULL,
  `UserID` varchar(255) NOT NULL,
  `Rating` varchar(255) DEFAULT NULL,
  `RatingTime` datetime DEFAULT NULL,
  PRIMARY KEY (`RatingID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;