package com.meli.obterdiploma.model;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // PARA CONSTRUIR OS ESTUDANTES
public class StudentDTO {

    Long id;

    @NotBlank(message = "O nome do aluno não pode ficar vazio.")
    @Pattern(regexp="([A-Z]|[0-9])[\\s|[0-9]|A-Z|a-z|ñ|ó|í|á|é|ú|Á|Ó|É|Í|Ú]*$", message = "O nome do aluno deve começar com letra maiúscula.")
    @Size(max = 50, message = "O comprimento do nome do aluno não pode exceder 50 caracteres.")
    String studentName;

    String message;
    Double averageScore;

    @NotEmpty(message = "A lista de assuntos não pode ficar vazia.")
    List<@Valid SubjectDTO> subjects;
}
