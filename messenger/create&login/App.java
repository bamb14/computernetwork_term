package com.messenger;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class App {
    static Connection conn;

    public static void main(String[] args) {
        final Start start;
        try {
            // 연결
            String url = "jdbc:mysql://localhost:3306/messenger";
            conn = DriverManager.getConnection(url, "root", "alth8282");
            Statement stmt = conn.createStatement();

            // 시작화면 불러오기

            // createUser = new CreateUser(stmt);
            // createUser.main();

            start = new Start(stmt);
            start.main();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
