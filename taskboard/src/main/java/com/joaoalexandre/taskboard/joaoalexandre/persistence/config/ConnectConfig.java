package com.joaoalexandre.taskboard.joaoalexandre.persistence.config;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class ConnectConfig {
    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost:3306/board?useSSL=false&serverTimezone=UTC";
        var user = "board";
        var password = "board";

        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
