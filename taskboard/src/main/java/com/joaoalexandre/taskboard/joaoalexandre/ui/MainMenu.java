package com.joaoalexandre.taskboard.joaoalexandre.ui;

import com.joaoalexandre.taskboard.joaoalexandre.persistence.config.ConnectConfig;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnEntity;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnKindEnum;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardEntity;
import com.joaoalexandre.taskboard.joaoalexandre.service.BoardQueryService;
import com.joaoalexandre.taskboard.joaoalexandre.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainMenu {

    private final Scanner scanner = new Scanner(System.in); // Scanner normal, sem useDelimiter

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.nextLine());

        int additionalColumns = readInt(
                "Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0': ");

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.nextLine();
        var initialColumn = createColumn(initialColumnName, BoardColumnKindEnum.INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.nextLine();
            var pendingColumn = createColumn(pendingColumnName, BoardColumnKindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.nextLine();
        var finalColumn = createColumn(finalColumnName, BoardColumnKindEnum.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.nextLine();
        var cancelColumn = createColumn(cancelColumnName, BoardColumnKindEnum.CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);

        try (var connection = ConnectConfig.getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);
        }

        System.out.println("Board criado com sucesso!");
    }

    private void selectBoard() throws SQLException {
        long id = readLong("Informe o id do board que deseja selecionar: ");
        try (var connection = ConnectConfig.getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s%n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        long id = readLong("Informe o id do board que será excluído: ");
        try (var connection = ConnectConfig.getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluído%n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s%n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

    // ---------------- Helper methods ----------------

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido!");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Long.parseLong(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido!");
            }
        }
    }
}
