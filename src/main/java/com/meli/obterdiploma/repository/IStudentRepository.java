package com.meli.obterdiploma.repository;

import com.meli.obterdiploma.model.StudentDTO;

import java.util.Set;

public interface IStudentRepository {

    Set<StudentDTO> findAll();

}
