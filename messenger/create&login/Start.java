package com.messenger;

import java.sql.SQLException;
import java.sql.Statement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Start {
    Statement state;
    Font font1;
    static JFrame f1;
    JButton b1;
    JButton b2;
    Login login;
    CreateUser createUser;

    public Start(Statement stmt) {
        state = stmt;
    }

    public void main() {

        font1 = new Font("Segoe print", Font.BOLD, 18);
        f1 = new JFrame("MESSENGER");
        b1 = new JButton("LOGIN");
        login = new Login(state);

        // login 버튼을 눌렀을 때
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 로그인 창 불러오기
                    login.main();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        b2 = new JButton("CREATE AN ACCOUNT");
        createUser = new CreateUser(state);

        // CREATE AN ACCOUNT 버튼을 눌렀을 때
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 회원가입 창 불러오기

                createUser.main();
            }
        });

        JLabel l1 = new JLabel("Messenger Start Page");
        l1.setFont(font1);
        b1.setBounds(150, 200, 100, 40);
        b2.setBounds(100, 300, 200, 40);
        l1.setBounds(80, 100, 500, 50);

        f1.add(b1);
        f1.add(b2);
        f1.add(l1);
        f1.setSize(400, 500);
        f1.setLayout(null);
        f1.setVisible(true);

    }
}
