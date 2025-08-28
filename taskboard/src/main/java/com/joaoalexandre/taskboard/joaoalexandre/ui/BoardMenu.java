package com.joaoalexandre.taskboard.joaoalexandre.ui;

import com.joaoalexandre.taskboard.joaoalexandre.dto.BoardColumnInfoDTO;
import com.joaoalexandre.taskboard.joaoalexandre.exception.EntityNotFoundException;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnEntity;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardEntity;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.CardEntity;
import com.joaoalexandre.taskboard.joaoalexandre.service.BoardColumnQueryService;
import com.joaoalexandre.taskboard.joaoalexandre.service.BoardQueryService;
import com.joaoalexandre.taskboard.joaoalexandre.service.CardQueryService;
import com.joaoalexandre.taskboard.joaoalexandre.service.CardService;

import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static com.joaoalexandre.taskboard.joaoalexandre.persistence.config.ConnectConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Seja bem vindo ao board %s, selecione a operação desejada%n", entity.getId());
            int option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("10 - Sair");

                option = readInt("Escolha uma opção: ");

                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o menu anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.nextLine());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.nextLine());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).create(card);
        }
        System.out.println("Card criado com sucesso!");
    }

    private void moveCardToNextColumn() throws SQLException {
        long cardId = readLong("Informe o id do card que deseja mover para a próxima coluna: ");
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException | EntityNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        long cardId = readLong("Informe o id do card que será bloqueado: ");
        System.out.println("Informe o motivo do bloqueio do card");
        String reason = scanner.nextLine();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException | EntityNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        long cardId = readLong("Informe o id do card que será desbloqueado: ");
        System.out.println("Informe o motivo do desbloqueio do card");
        String reason = scanner.nextLine();
        try (var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        long cardId = readLong("Informe o id do card que deseja mover para a coluna de cancelamento: ");
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]%n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards%n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        long selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna do board %s pelo id%n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]%n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = readLong("ID da coluna: ");
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s%n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s%nDescrição: %s%n",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        long selectedCardId = readLong("Informe o id do card que deseja visualizar: ");
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.%n", c.id(), c.title());
                                System.out.printf("Descrição: %s%n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes%n", c.blockAmount());
                                System.out.printf("Está no momento na coluna %s - %s%n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s%n", selectedCardId)
                    );
        }
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
