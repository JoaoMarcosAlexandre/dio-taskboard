package com.joaoalexandre.taskboard.joaoalexandre.dto;

import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cardsAmount) {
}