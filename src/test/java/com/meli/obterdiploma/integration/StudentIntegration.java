package com.meli.obterdiploma.integration;

import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.repository.StudentDAO;
import com.meli.obterdiploma.util.TestUtilsGenerator;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Log4j2 // PARA PRINTAR NA TELA OS LOGS
// INFORMA O TIPO DE CLASSE
/* Quando subimos a aplicação ela sobe no Tomcat na porta 8080.
* No nosso exemplo pedimos para que suba numa porta aleatoria, em qualquer uma que esteja livre*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentIntegration {

    // INICIALIZAR A VARIÁVEL COM O NÚMERO DA PORTA QUE O SPRING USOU PARA CARREGAR O APP
    @LocalServerPort
    private int PORT;

    // RestTemplate = CLIENTE QUE FAZ CHAMADAS PARA O SERVIDOR
    // TestRestTemplate = BIBLIOTECA DE TESTE
    @Autowired
    TestRestTemplate testRestTemplate;


    @BeforeEach // ANTES DE CADA TESTE
    public void setup() {
        // RESPONSÁVEL POR ZERAR O BANCO/ARQUIVO
        TestUtilsGenerator.emptyUsersFile();
    }


    @Test
    @DisplayName("Valida se um novo estudante será inserido com sucesso.")
    public void registerStudent_saveStudent_whenNewStudent() {
        // 1. PREPARAÇÃO

        // GERA UM ESTUDANTE NOVO
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        // NECESSÁRIO MONTAR A URL. ROTA BASE
        String baseUrl = "http://localhost:" + PORT + "/student";

        /* NO CORPO DA REQUISIÇÃO DEVE SER ENVIADO OS DADOS DO ESTUDANTE.
         PORTANTO CRIA UMA VARIÁVEL COM ESSAS INFORMAÇÕES, QUE SERÁ USADA NA REQUISIÇÃO */
        HttpEntity<StudentDTO> httpEntity = new HttpEntity<>(newStudent);

        // 2. CHAMADA

        /* A PARTIR DO TEST REST TEMPLANTE EU CHAMO A REQUISIÇÃO. FOR OBJECT ESPERA COMO RESULTADO DA CHAMADA
        * UM OBJETO E O FOR ENTITY ESPERA UM RESPONSE E DENTO DO RESPONSE TERÁ O OBJETO
        * EXCHANGE SERVE PARA TODOS OS MÉTODOS HTTP, E ACEITA COMO PARÂMETRO O TIPO DE
        * CHAMADA (GET, POST, DELETE E ETC)
        * Parâmetros do exchange(URL, método HTTP, body, tipo de objeto retorno)
        * O testRestTemplate SIMULA UMA REQUISIÇÃO FEITA NO NAVEGADOR OU POSTMAN */
        ResponseEntity<StudentDTO> retorno = testRestTemplate.exchange(
            baseUrl+"/registerStudent",
            HttpMethod.POST,
            httpEntity,
            StudentDTO.class
        );

        // OBTENHO O CONTEÚDO DO BODY RETORNADO
        StudentDTO studentReturned = retorno.getBody();

        // RETORNO DA CHAMADA

        // PARA RODAR O TESTE, O BANCO DE DADOS PREICSA ESTAR ZERADO.
        // NECESSÁRIO INCLUIR UM NOVO ESTUDANTE

        // 3. TESTA OS RESULTADOS DA REQUISIÇÃO

        assertThat(retorno.getStatusCode()).isEqualTo(HttpStatus.CREATED); // que o status code seja created
        assertThat(studentReturned).isNotNull(); // se não é nulo
        assertThat(studentReturned.getId()).isPositive(); // se tem id
        assertThat(studentReturned.getStudentName()).isEqualTo(newStudent.getStudentName()); // se os nome são iguais
    }


    @Test
    @DisplayName("Valida o erro.")
    public void registerStudent_returnBadRequest_whenStudentHasId() {
        // 1. PREPARAÇÃO

        // GERA UM ESTUDANTE NOVO
        StudentDTO newStudent = TestUtilsGenerator.getStudentWithId();

        // NECESSÁRIO MONTAR A URL. ROTA BASE
        String baseUrl = "http://localhost:" + PORT + "/student";

        /* NO CORPO DA REQUISIÇÃO DEVE SER ENVIADO OS DADOS DO ESTUDANTE.
         PORTANTO CRIA UMA VARIÁVEL COM ESSAS INFORMAÇÕES, QUE SERÁ USADA NA REQUISIÇÃO */
        HttpEntity<StudentDTO> httpEntity = new HttpEntity<>(newStudent);

        // 2. CHAMADA

        /* A PARTIR DO TEST REST TEMPLANTE EU CHAMO A REQUISIÇÃO. FOR OBJECT ESPERA COMO RESULTADO DA CHAMADA
         * UM OBJETO E O FOR ENTITY ESPERA UM RESPONSE E DENTO DO RESPONSE TERÁ O OBJETO
         * EXCHANGE SERVE PARA TODOS OS MÉTODOS HTTP, E ACEITA COMO PARÂMETRO O TIPO DE
         * CHAMADA (GET, POST, DELETE E ETC)
         * Parâmetros do exchange(URL, método HTTP, body, tipo de objeto retorno)
         * O testRestTemplate SIMULA UMA REQUISIÇÃO FEITA NO NAVEGADOR OU POSTMAN */
        ResponseEntity<StudentDTO> retorno = testRestTemplate.exchange(
            baseUrl+"/registerStudent",
            HttpMethod.POST,
            httpEntity,
            StudentDTO.class
        );

        // 3. TESTA OS RESULTADOS DA REQUISIÇÃO

        assertThat(retorno.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // que o status code seja created
    }

    @Test
    @DisplayName("Valida se não retorna o estudante quando ele existe")
    public void getStudent_returnStudent_whenStudentNotExist() {
        // 1. PREPARAÇÃO

        // GERA UM ESTUDANTE COM ID
        StudentDTO student = TestUtilsGenerator.getStudentWithId();

        // NECESSÁRIO MONTAR A URL. ROTA BASE
        String baseUrl = "http://localhost:" + PORT + "/student";

        // 2. CHAMADA

        /* Parâmetros do exchange(URL, método HTTP, body, tipo de objeto que espero receber no retorno)
         * O testRestTemplate SIMULA UMA REQUISIÇÃO FEITA NO NAVEGADOR OU POSTMAN */
        ResponseEntity<StudentDTO> retorno = testRestTemplate.exchange(
            baseUrl + "/getStudent/" + student.getId(),
            HttpMethod.GET,
            null,
            StudentDTO.class
        );

        // 3. TESTA OS RESULTADOS DA REQUISIÇÃO

        assertThat(retorno.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // que o status code seja not found
    }

    @Test
    @DisplayName("Valida se retorna o estudante quando ele existe")
    public void getStudent_returnStudent_whenStudentExist() {
        // 1. PREPARAÇÃO

        // GERA UM ESTUDANTE COM ID
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        // NECESSÁRIO MONTAR A URL. ROTA BASE
        String baseUrl = "http://localhost:" + PORT + "/student/getStudent/";


        // PERSISTO OS DADOS DO ESTUDANTE NO BANCO DE DADOS
        StudentDAO studentDAO = new StudentDAO();
        // retorna um estudante com id
        StudentDTO studentSaved = studentDAO.save(newStudent);

        // O %d SERÁ SUBSTITUÍDO PELO SEGUNDO PARÂMETRO
        String url = String.format(baseUrl + "%d", studentSaved.getId());
        log.info(url);

        // CHAMADA
        // O ESTUDANTE QUE FOI SALVO ESTÁ SENDO BUSCADO
        ResponseEntity<StudentDTO> retorno = testRestTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            StudentDTO.class
        );

        // 3. TESTA OS RESULTADOS DA REQUISIÇÃO

        assertThat(retorno.getStatusCode()).isEqualTo(HttpStatus.OK); // que o status code seja not found
        assertThat(retorno.getBody().getId()).isEqualTo(studentSaved.getId());
        assertThat(retorno.getBody().getStudentName()).isEqualTo(studentSaved.getStudentName());
    }

}
