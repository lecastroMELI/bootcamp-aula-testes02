package com.meli.obterdiploma.service;

import com.meli.obterdiploma.model.StudentDTO;

public interface IObterDiplomaService {

    StudentDTO analyzeScores(Long studentId);
}
