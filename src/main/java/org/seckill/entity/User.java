package org.seckill.entity;

import lombok.Data;

@Data
public class User {
    private String account;
    private String password;

    public User(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public User() {
        super();
    }
}
