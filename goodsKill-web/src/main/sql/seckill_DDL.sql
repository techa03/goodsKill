ALTER TABLE
    `seckill`.`user` ADD (username VARCHAR(50));
ALTER TABLE
    `seckill`.`user` ADD (locked VARCHAR(1) DEFAULT '0');