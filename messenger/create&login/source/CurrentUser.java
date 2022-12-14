package com.messenger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CurrentUser {
    static String user_id;
    static String password;
    static String nickname;
    static String name;
    static String email;
    static String phone_number;
    static String site_url;
    static String article;
    static String birthday;
    static String created_date;
    ResultSet rs;
    Statement state;

    // 현재 로그인된 user의 정보들을 다 가져와서 넣어주기
    public void setUser(String user) {
        user_id = user;
    }

    public void setOthers(Statement stmt) throws SQLException {
        state = stmt;
        rs = state.executeQuery(String.format("select * from users where user_id = \'%s\'", user_id));

        while (rs.next()) {
            nickname = rs.getString("nickname");
            password = rs.getString("pw");
            name = rs.getString("user_name");
            email = rs.getString("email");
            phone_number = rs.getString("phone_number");
            site_url = rs.getString("site_url");
            article = rs.getString("intro_article");
            birthday = rs.getString("birth");
            created_date = rs.getString("is_created");

        }

    }

}
