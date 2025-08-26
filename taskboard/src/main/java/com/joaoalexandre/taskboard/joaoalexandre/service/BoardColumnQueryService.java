package com.joaoalexandre.taskboard.joaoalexandre.service;


import com.joaoalexandre.taskboard.joaoalexandre.persistence.dao.BoardColumnDAO;
import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {

    private final Connection connection;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        var dao = new BoardColumnDAO(connection);
        return dao.findById(id);
    }

}