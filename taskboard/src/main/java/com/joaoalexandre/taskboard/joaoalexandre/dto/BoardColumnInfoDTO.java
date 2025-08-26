package com.joaoalexandre.taskboard.joaoalexandre.dto;

import com.joaoalexandre.taskboard.joaoalexandre.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id,
                                 int order,
                                 BoardColumnKindEnum kind) {
}
