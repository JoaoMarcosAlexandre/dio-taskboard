package com.joaoalexandre.taskboard.joaoalexandre;



import com.joaoalexandre.taskboard.joaoalexandre.persistence.migration.MigrationStrategy;
import com.joaoalexandre.taskboard.joaoalexandre.ui.MainMenu;

import java.sql.SQLException;

import static com.joaoalexandre.taskboard.joaoalexandre.persistence.config.ConnectConfig.getConnection;


public class Main {

    public static void main(String[] args) throws SQLException {
        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}
