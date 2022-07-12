package com.meli.obterdiploma.service;

import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.repository.IStudentDAO;
import com.meli.obterdiploma.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class StudentService implements IStudentService {
    /* O SERVIÇO DEPENDE DAS DEPENDÊNCIAS DO REPOSITÓRIO. PORTANTO É NECESSÁRIO SIMULAR O QUE FOR NECESSÁRIO*/
    @Autowired
    IStudentDAO studentDAO;

    @Autowired
    IStudentRepository studentRepository;

    @Override
    public StudentDTO create(StudentDTO stu) {
        return studentDAO.save(stu);
    }

    @Override
    public StudentDTO read(Long id) {
        return studentDAO.findById(id);
    }

    @Override
    public void update(StudentDTO stu) {
        studentDAO.save(stu);
    }

    @Override
    public void delete(Long id) {
        studentDAO.delete(id);
    }

    @Override
    public Set<StudentDTO> getAll() {
        return this.studentRepository.findAll();
    }
}
