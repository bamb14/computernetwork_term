package com.messenger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateUser extends JFrame {

    Statement state;
    String user_id, nickname, pw, pwc, user_name, email, year, month, day, date, phone_number, site_url, intro_article;
    ResultSet rs;
    JFrame f1;
    JTextField tf1;
    JPasswordField pf1;
    JPasswordField pf2;
    JTextField tf2;
    JTextField tf3;
    JTextField tf4;
    JTextField tf5;
    JTextField tf6;
    JTextField tf7;
    JTextField tf8;
    JComboBox<String> cb1;
    JComboBox<String> cb2;

    public CreateUser(Statement stmt) {
        state = stmt;
    }

    public void main() {
        f1 = new JFrame("Create new user");

        pf1 = new JPasswordField();
        pf2 = new JPasswordField();

        tf1 = new JTextField("NO SPACE, 5+"); // user_id
        tf2 = new JTextField(); // user_name
        tf3 = new JTextField(); // nickname
        tf4 = new JTextField("YYYY"); // year
        tf5 = new JTextField(); // email
        tf6 = new JTextField(); // phone_number
        tf7 = new JTextField(); // site_url
        tf8 = new JTextField(); // intro_article

        // 월
        cb1 = new JComboBox<String>(
                new String[] { "MM", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" });
        // 일
        cb2 = new JComboBox<String>(new String[] { "DD", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
                "28", "29", "30", "31" });

        JButton b1 = new JButton("SIGN UP");

        JLabel l1 = new JLabel("ID:");
        JLabel l2 = new JLabel("Password:");
        JLabel l3 = new JLabel("Double Check:");
        JLabel l4 = new JLabel("User Name:");
        JLabel l5 = new JLabel("nickname:");
        JLabel l6 = new JLabel("Birth Date:");
        JLabel l7 = new JLabel("Email:");
        JLabel l8 = new JLabel("Phone number:");
        JLabel l9 = new JLabel("Website URL:");
        JLabel l10 = new JLabel("Intro article:");

        pf1.setBounds(110, 65, 130, 20);
        pf2.setBounds(110, 115, 130, 20);

        tf1.setBounds(110, 30, 130, 20);
        tf2.setBounds(110, 165, 130, 20);
        tf3.setBounds(110, 215, 130, 20);
        tf4.setBounds(110, 265, 50, 20);
        tf5.setBounds(110, 315, 130, 20);
        tf6.setBounds(110, 365, 130, 20);
        tf7.setBounds(110, 415, 130, 20);
        tf8.setBounds(110, 465, 130, 20);

        cb1.setBounds(170, 265, 50, 20);
        cb2.setBounds(230, 265, 50, 20);

        l1.setBounds(90, 30, 100, 20);
        l2.setBounds(45, 65, 100, 20);
        l3.setBounds(20, 115, 100, 20);
        l4.setBounds(35, 165, 100, 20);
        l5.setBounds(40, 215, 100, 20);
        l6.setBounds(45, 265, 100, 20);
        l7.setBounds(65, 315, 100, 20);
        l8.setBounds(20, 365, 100, 20);
        l9.setBounds(40, 415, 100, 20);
        l10.setBounds(40, 465, 100, 20);

        b1.setBounds(150, 510, 90, 20);

        f1.add(l1);
        f1.add(l2);
        f1.add(l3);
        f1.add(l4);
        f1.add(l5);
        f1.add(l6);
        f1.add(l7);
        f1.add(l8);
        f1.add(l9);
        f1.add(l10);

        f1.add(pf1);
        f1.add(pf2);

        f1.add(tf1);
        f1.add(tf2);
        f1.add(tf3);
        f1.add(tf4);
        f1.add(tf5);
        f1.add(tf6);
        f1.add(tf7);
        f1.add(tf8);

        f1.add(cb1);
        f1.add(cb2);

        f1.add(b1);

        f1.setSize(400, 600);
        f1.setLayout(null);
        f1.setVisible(true);

        cb1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == cb1) {
                    JComboBox monthBox = (JComboBox) e.getSource();
                    month = (String) monthBox.getSelectedItem();
                    System.out.println(month);
                }

            }
        });
        // 일 눌렀을때 일 저장
        cb2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == cb2) {
                    JComboBox dayBox = (JComboBox) e.getSource();
                    day = (String) dayBox.getSelectedItem();
                    System.out.println(day);
                }
            }
        });

        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                user_id = tf1.getText();
                pw = new String(pf1.getPassword());
                pwc = new String(pf2.getPassword());
                user_name = tf2.getText();
                nickname = tf3.getText();
                year = tf4.getText();
                email = tf5.getText();
                phone_number = tf6.getText();
                site_url = tf7.getText();
                intro_article = tf8.getText();

                // String sql = "insert into users(user_id,user_name, pw,email, nickname,
                // birth,intro_article,phone_number,site_url) values (?,?,?,?,?,?,?,?,?)";

                String sql = "INSERT INTO users(user_id, pw, user_name, nickname, birth, email, phone_number, site_url, intro_article) VALUES (?,?,?,?,?,?,?,?,?)";

                // 8자 영문+특문+숫자(비밀번호 조건)
                Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
                Matcher passMatcher = passPattern1.matcher(pw);

                try {
                    rs = state.executeQuery(String.format("select * from users where user_id = \'%s\'", user_id));
                    // 입력한 user_id와 중복되는 user_id가 이미 존재할 때
                    while (rs.next()) {
                        if (tf1.getText().equals(rs.getString("user_id"))) {
                            JOptionPane.showMessageDialog(null, "ID already exists!", "Same user_id", 1);
                        }
                    }
                    // 입력한 user_id에 공백이 포함될 때
                    if (user_id.replace(" ", "").length() != user_id.length()) {
                        JOptionPane.showMessageDialog(null, "Space not allowed!", "Space not allowed", 1);

                    }
                    // 입력한 user_id의 길이가 5보다 짧을 때
                    else if (user_id.length() < 5)
                        JOptionPane.showMessageDialog(null, "ID at least 5 letters!", "ID length", 1);

                    else if (!passMatcher.find()) {
                        JOptionPane.showMessageDialog(null, "Password should be 8+ with Eng+Num+Special Symbol!",
                                "비밀번호 오류", 1);
                    }
                    // 비밀번호와 재확인란의 비밀번호가 서로 일치하지 않을때
                    else if (!pw.equals(pwc)) {
                        JOptionPane.showMessageDialog(null, "Password doesn't match!", "Password wrong", 1);

                    } else {
                        try {
                            String url = "jdbc:mysql://localhost:3306/messenger";
                            Connection conn = DriverManager.getConnection(url, "root", "alth8282");

                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            // 년/월/일 다 제대로 입력됐다면 생일 정보 저장
                            String date;
                            if (year == null || month == null || day == null)
                                date = null;
                            else
                                date = year + "-" + month + "-" + day;

                            user_id = tf1.getText();

                            // 각 정보들 db에 저장
                            // user_id
                            pstmt.setString(1, user_id);

                            // pw
                            pstmt.setString(2, pw);

                            // user_name
                            pstmt.setString(3, tf2.getText());

                            // nickname
                            pstmt.setString(4, tf3.getText());

                            // birthday
                            pstmt.setString(5, date);

                            // email
                            pstmt.setString(6, tf5.getText());

                            // phone_number
                            if (tf6.getText() == null)
                                pstmt.setString(7, "null");
                            else
                                pstmt.setString(7, tf6.getText());

                            // site_url
                            if (tf7.getText() == null)
                                pstmt.setString(8, "null");
                            else
                                pstmt.setString(8, tf7.getText());

                            // intro_article
                            if (tf8.getText() == null)
                                pstmt.setString(9, "null");
                            else
                                pstmt.setString(9, tf8.getText());

                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Registered Successfully!", "Register", 1);

                            f1.setVisible(false);// 다 완료되면 로그인 화면으로
                        } catch (SQLException e1) {
                            // 그 외의 오류가 날 경우
                            System.out.println("SQL error" + e1.getMessage());
                            JOptionPane.showMessageDialog(null, "Type information correctly!", "Wrong", 1);
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }

        });
    }
}