package com.joaoalexandre.taskboard.joaoalexandre.service;

import com.joaoalexandre.taskboard.joaoalexandre.dto.CardDetailsDTO;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.dao.CardDAO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }

}