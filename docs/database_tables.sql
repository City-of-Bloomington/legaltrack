CREATE TABLE `attorneys` (
  `id` int NOT NULL AUTO_INCREMENT,
  `empid` varchar(50) DEFAULT NULL,
  `fname` varchar(30) DEFAULT NULL,
  `lname` varchar(30) DEFAULT NULL,
  `position` enum('Assistant','Attorney','Counsel','Paralegal','Legal Secretary') DEFAULT NULL,
  `title` varchar(30) DEFAULT NULL,
  `barNum` varchar(10) DEFAULT NULL,
  `active` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `doc_texts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(4) DEFAULT NULL,
  `porder` int DEFAULT NULL,
  `ptext` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;
;;
CREATE TABLE `legal_actions` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `legal_id` int unsigned DEFAULT NULL,
  `actionDate` date DEFAULT NULL,
  `actionBy` varchar(70) DEFAULT NULL,
  `notes` varchar(2500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `legal_id` (`legal_id`),
  CONSTRAINT `legal_actions_ibfk_1` FOREIGN KEY (`legal_id`) REFERENCES `rental_legals` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13075 DEFAULT CHARSET=utf8mb3 ;
;;
CREATE TABLE `legal_addresses` (
  `id` mediumint NOT NULL AUTO_INCREMENT,
  `caseId` int NOT NULL,
  `street_num` varchar(8) DEFAULT NULL,
  `street_dir` enum('N','S','E','W') DEFAULT NULL,
  `street_name` varchar(50) NOT NULL,
  `street_type` varchar(10) DEFAULT NULL,
  `post_dir` enum('N','S','E','W') DEFAULT NULL,
  `sud_type` varchar(10) DEFAULT NULL,
  `sud_num` varchar(10) DEFAULT NULL,
  `invalid_addr` char(1) DEFAULT NULL,
  `rental_addr` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `caseId` (`caseId`)
) ENGINE=MyISAM AUTO_INCREMENT=4751 DEFAULT CHARSET=utf8mb3;
;;
;;
CREATE TABLE `legal_animals` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cid` int NOT NULL,
  `name` varchar(30) NOT NULL,
  `pet_type` enum('Dog','Cat','Other') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb3;
;;
;;
CREATE TABLE `users` (
  `userid` varchar(50) DEFAULT NULL,
  `dept` varchar(15) DEFAULT NULL,
  `fullName` varchar(50) DEFAULT NULL,
  `role` enum('Edit','Edit:Delete','Edit:Delete:Admin') DEFAULT NULL,
  UNIQUE KEY `userid` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_care_of` (
  `id` mediumint NOT NULL AUTO_INCREMENT,
  `def_id` int NOT NULL,
  `co_name` varchar(70) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;
;;
CREATE TABLE `legal_def_addresses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `defId` int NOT NULL,
  `street_num` varchar(8) DEFAULT NULL,
  `street_dir` enum('N','S','E','W') DEFAULT NULL,
  `street_name` varchar(50) NOT NULL,
  `street_type` varchar(10) DEFAULT NULL,
  `post_dir` enum('N','S','E','W') DEFAULT NULL,
  `sud_type` varchar(10) DEFAULT NULL,
  `sud_num` varchar(10) DEFAULT NULL,
  `invalid_addr` char(1) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(2) DEFAULT NULL,
  `zip` varchar(10) DEFAULT NULL,
  `addr_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `defId` (`defId`),
  CONSTRAINT `legal_def_addresses_ibfk_1` FOREIGN KEY (`defId`) REFERENCES `legal_defendents` (`did`)
) ENGINE=InnoDB AUTO_INCREMENT=19618 DEFAULT CHARSET=utf8mb3;
;;
CREATE TABLE `legal_case_status` (
  `statusId` char(2) NOT NULL,
  `statusDesc` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`statusId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_case_types` (
  `typeId` varchar(4) NOT NULL,
  `typeDesc` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_case_violations` (
  `vid` int NOT NULL AUTO_INCREMENT,
  `id` int DEFAULT NULL,
  `sid` int DEFAULT NULL,
  `ident` varchar(4) DEFAULT NULL,
  `dates` varchar(200) DEFAULT NULL,
  `amount` varchar(30) DEFAULT NULL,
  `entered` date DEFAULT NULL,
  `citations` varchar(200) DEFAULT NULL,
  `cid` int DEFAULT NULL,
  PRIMARY KEY (`vid`),
  KEY `id` (`id`),
  KEY `sid` (`sid`),
  KEY `cid` (`cid`)
) ENGINE=MyISAM AUTO_INCREMENT=26525 DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `legal_cases` (
  `id` int NOT NULL AUTO_INCREMENT,
  `received` date DEFAULT NULL,
  `case_type` varchar(4) NOT NULL,
  `status` char(2) NOT NULL,
  `sent_date` date DEFAULT NULL,
  `ini_hear_date` date DEFAULT NULL,
  `ini_hear_time` varchar(10) DEFAULT NULL,
  `contest_hear_date` date DEFAULT NULL,
  `contest_hear_time` varchar(10) DEFAULT NULL,
  `misc_hear_date` date DEFAULT NULL,
  `misc_hear_time` varchar(10) DEFAULT NULL,
  `filed` date DEFAULT NULL,
  `judgment_date` date DEFAULT NULL,
  `compliance_date` date DEFAULT NULL,
  `pro_supp_date` date DEFAULT NULL,
  `judgment_amount` double(10,2) DEFAULT '0.00',
  `fine` double(10,2) DEFAULT '0.00',
  `court_cost` double(10,2) DEFAULT '0.00',
  `last_paid_date` date DEFAULT NULL,
  `closed_date` date DEFAULT NULL,
  `closed_comments` varchar(80) DEFAULT NULL,
  `comments` varchar(10000) DEFAULT NULL,
  `pro_supp_time` varchar(10) DEFAULT NULL,
  `per_day` char(1) DEFAULT NULL,
  `mcc_flag` char(1) DEFAULT NULL,
  `pro_supp` char(1) DEFAULT NULL,
  `lawyerid` varchar(70) DEFAULT NULL,
  `rule_date` date DEFAULT NULL,
  `rule_time` varchar(8) DEFAULT NULL,
  `e41_date` date DEFAULT NULL,
  `citation_num` varchar(50) DEFAULT NULL,
  `trans_collect_date` date DEFAULT NULL,
  `citation_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `case_type` (`case_type`),
  KEY `status` (`status`)
) ENGINE=MyISAM AUTO_INCREMENT=28654 DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_cross_ref` (
  `typeId` varchar(5) NOT NULL DEFAULT '',
  `cid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`typeId`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
;;
;;
CREATE TABLE `legal_def_case` (
  `id` int NOT NULL DEFAULT '0',
  `did` int NOT NULL DEFAULT '0',
  `cause_num` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`did`),
  KEY `did` (`did`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_defendents` (
  `did` int NOT NULL AUTO_INCREMENT,
  `l_name` varchar(90) DEFAULT NULL,
  `f_name` varchar(70) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `ssn` varchar(12) DEFAULT NULL,
  `addr_req_date` date DEFAULT NULL,
  `addr_last_update` date DEFAULT NULL,
  `dln` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `phone_2` varchar(20) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB AUTO_INCREMENT=23418 DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `legal_depts` (
  `deptId` int NOT NULL AUTO_INCREMENT,
  `dept` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`deptId`),
  UNIQUE KEY `dept` (`dept`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `legal_files` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cid` int NOT NULL,
  `file_date` date DEFAULT NULL,
  `load_file` varchar(70) NOT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `old_name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb3;
;;
;;
CREATE TABLE `legal_judges` (
  `jid` int DEFAULT NULL,
  `judge` varchar(50) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `legal_lawyer_types` (
  `typeId` varchar(4) NOT NULL,
  `lawyerid` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_lawyers` (
  `lawyerid` varchar(8) NOT NULL,
  `fullName` varchar(50) DEFAULT NULL,
  `barNum` varchar(10) DEFAULT NULL,
  `title` varchar(30) DEFAULT NULL,
  `active` char(1) DEFAULT NULL,
  PRIMARY KEY (`lawyerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
;;
;;
 CREATE TABLE `legal_payments` (
  `pid` int NOT NULL AUTO_INCREMENT,
  `id` int DEFAULT NULL,
  `paid_date` date NOT NULL,
  `amount` double(8,2) NOT NULL,
  `paid_by` varchar(50) DEFAULT NULL,
  `paid_method` varchar(10) DEFAULT NULL,
  `check_no` varchar(15) DEFAULT NULL,
  `clerk` char(1) DEFAULT NULL,
  `paidFor` enum('Court','Fine') DEFAULT NULL,
  PRIMARY KEY (`pid`),
  KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13548 DEFAULT CHARSET=utf8mb3 ;
;;
;;
 CREATE TABLE `legal_type_dept` (
  `typeId` varchar(4) NOT NULL,
  `deptId` int NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ;
;;
;;
CREATE TABLE `legal_viol_cats` (
  `cid` int NOT NULL AUTO_INCREMENT,
  `category` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=MyISAM AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb3;
;;
;;
CREATE TABLE `legal_viol_subcats` (
  `sid` int NOT NULL AUTO_INCREMENT,
  `cid` int DEFAULT NULL,
  `subcat` varchar(100) DEFAULT NULL,
  `complaint` varchar(500) NOT NULL,
  `codes` varchar(100) NOT NULL,
  `amount` double DEFAULT '0',
  PRIMARY KEY (`sid`),
  KEY `cid` (`cid`)
) ENGINE=MyISAM AUTO_INCREMENT=318 DEFAULT CHARSET=utf8mb3 ;
;;
 CREATE TABLE `rental_legals` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `startDate` date DEFAULT NULL,
  `rental_id` int DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `startBy` varchar(70) DEFAULT NULL,
  `status` enum('New','Pending','Closed','Filed Suit') DEFAULT NULL,
  `attention` enum('HAND','Legal') DEFAULT NULL,
  `case_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2120 DEFAULT CHARSET=utf8mb3;
;;





