package com.meli.obterdiploma.exception;

import org.springframework.http.HttpStatus;

public class StudentNotFoundException extends ObterDiplomaException {

    public StudentNotFoundException(Long id) {
        super("O aluno com Id " + id + " não está registrado.", HttpStatus.NOT_FOUND);
    }
}
