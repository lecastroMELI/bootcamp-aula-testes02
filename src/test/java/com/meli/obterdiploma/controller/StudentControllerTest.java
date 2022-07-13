package com.meli.obterdiploma.controller;

import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.service.IStudentService;
import com.meli.obterdiploma.util.TestUtilsGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest // sobe a aplicação
// COMO VAMOS USAR UM MOCK É NECESSÁRIO ANOTAR O AMBIENTE QUE SERÁ USADO PARA RODAR ESSA CLASSE DE TESTE AQUI
@ExtendWith(MockitoExtension.class)
// DESLIGA AS VALIDAÇÕES
@MockitoSettings(strictness = Strictness.LENIENT)
class StudentControllerTest {

    @InjectMocks
    private StudentController controller;

    @Mock
    IStudentService studentService;

    @BeforeEach
    public void setup() {
        // OS MOCKS SÃO NECESSÁRIOS, PARA EVITAR A DEPENDÊNCIA DESTA CAMADA COM A CAMANDA ANTERIOR
        // A CAMADA REPOSITORY NÃO PRECISOU DE MOCK, JUSTAMENTE POR ELA SER A PRIMEIRA

        BDDMockito.when(studentService.create(ArgumentMatchers.any(StudentDTO.class)))
            .thenReturn(TestUtilsGenerator.getStudentWithId());

        BDDMockito.when(studentService.read(ArgumentMatchers.anyLong()))
            .thenReturn(TestUtilsGenerator.getStudentWithId());

        // NÃO FAÇA NADA QUANDO STUDENT SERVICE EXECUTAR O DELETE COM QQ ARGUMENTO
        BDDMockito.doNothing().when(studentService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    void registerStudent() {
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        ResponseEntity<StudentDTO> response = controller.registerStudent(newStudent);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId())
            .isNotNull()
            .isPositive();

        Mockito.verify(studentService, Mockito.atLeastOnce()).create(newStudent);
    }

    @Test
    void getStudent() {
        StudentDTO studentDTO = TestUtilsGenerator.getStudentWithId();

        // AO CHAMAR O CONTROLLER, ELE CHAMA O MÉTODO MOCK, E TEM COMO RESPOSTA UM STUDENT COM ID
        ResponseEntity<StudentDTO> response = controller.getStudent(studentDTO.getId());

        Mockito.verify(studentService, Mockito.atLeastOnce()).read(studentDTO.getId());

        // VERIFICA SE TEM O MESMO ID
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentDTO.getId());
    }

    @Test
    void removeStudent() {
        StudentDTO studentDTO = TestUtilsGenerator.getStudentWithId();

        ResponseEntity<Void> response = controller.removeStudent(studentDTO.getId());

        // VALIDA SE O MÉTODO FOI CHAMADO 1 VEZ
        Mockito.verify(studentService, Mockito.atLeastOnce()).delete(studentDTO.getId());

        // TESTA
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}