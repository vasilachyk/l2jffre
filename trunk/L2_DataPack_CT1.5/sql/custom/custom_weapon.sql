DROP TABLE IF EXISTS `custom_weapon`;
CREATE TABLE IF NOT EXISTS `custom_weapon` (
  `item_id` decimal(11,0) NOT NULL DEFAULT '0',
  `item_display_id` decimal(11,0) NOT NULL DEFAULT '0',
  `name` varchar(70) DEFAULT NULL,
  `bodypart` varchar(15) DEFAULT NULL,
  `crystallizable` varchar(5) DEFAULT NULL,
  `weight` decimal(4,0) DEFAULT NULL,
  `soulshots` decimal(2,0) DEFAULT NULL,
  `spiritshots` decimal(1,0) DEFAULT NULL,
  `material` varchar(11) DEFAULT NULL,
  `crystal_type` varchar(4) DEFAULT NULL,
  `p_dam` decimal(5,0) DEFAULT NULL,
  `rnd_dam` decimal(2,0) DEFAULT NULL,
  `weaponType` varchar(8) DEFAULT NULL,
  `critical` decimal(2,0) DEFAULT NULL,
  `hit_modify` decimal(6,5) DEFAULT NULL,
  `avoid_modify` decimal(2,0) DEFAULT NULL,
  `shield_def` decimal(3,0) DEFAULT NULL,
  `shield_def_rate` decimal(2,0) DEFAULT NULL,
  `atk_speed` decimal(3,0) DEFAULT NULL,
  `mp_consume` decimal(2,0) DEFAULT NULL,
  `m_dam` decimal(3,0) DEFAULT NULL,
  `duration` decimal(3,0) DEFAULT NULL,
  `price` decimal(11,0) DEFAULT NULL,
  `crystal_count` int(4) DEFAULT NULL,
  `sellable` varchar(5) NOT NULL DEFAULT 'true',
  `dropable` varchar(5) NOT NULL DEFAULT 'true',
  `destroyable` varchar(5) NOT NULL DEFAULT 'true',
  `tradeable` varchar(5) NOT NULL DEFAULT 'true',
  `skills_item` varchar(70) NOT NULL DEFAULT '',
  `skills_enchant4` varchar(70) NOT NULL DEFAULT '',
  `skills_onCast` varchar(70) NOT NULL DEFAULT '',
  `skills_onCrit` varchar(70) NOT NULL DEFAULT '',
  `change_weaponId` decimal(11,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) DEFAULT CHARSET=utf8;

ALTER TABLE `custom_weapon` ADD `races` VARCHAR(20) NOT NULL DEFAULT '-1';
ALTER TABLE `custom_weapon` ADD `classes` VARCHAR(255) NOT NULL DEFAULT '-1';
ALTER TABLE `custom_weapon` ADD `sex` INT (1) NOT NULL DEFAULT -1;