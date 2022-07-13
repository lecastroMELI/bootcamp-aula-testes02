package com.meli.obterdiploma.controller;

import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    IStudentService studentService;

    @PostMapping("/registerStudent")
    public ResponseEntity<StudentDTO> registerStudent(@RequestBody @Valid StudentDTO stu) {
        // se mandar uma chamada para registrar um NOVO estudante e ela já vem com ID está errado
        if(stu.getId() != null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(stu));
    }

    @GetMapping("/getStudent/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.read(id));
    }

    @PostMapping("/modifyStudent")
    public ResponseEntity<?> modifyStudent(@RequestBody @Valid StudentDTO stu) {
        this.studentService.update(stu);
        return ResponseEntity.ok(null);
    }

    // O MÉTODO NÃO VAI RETORNAR NADA
    @GetMapping("/removeStudent/{id}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long id) {
        this.studentService.delete(id);
        // NO CONTENT SIGNIFICA QUE NÃO TEM NENHUM CONTEÚDO PARA RETORNAR
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/listStudents")
    public Set<StudentDTO> listStudents() {
        return this.studentService.getAll();
    }

}
