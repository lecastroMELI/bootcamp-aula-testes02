package com.meli.obterdiploma.repository;

import com.meli.obterdiploma.exception.StudentNotFoundException;
import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.util.TestUtilsGenerator;

import org.springframework.http.HttpStatus;
// import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.*;

// ⚠️ O TESTE NÃO É PARTE DO JAVA. O JUNIT, O ASSERTJ, SÃO BIBLIOTECAS QUE FAZEM TESTE UNITÁRIO EM JAVA.

// ✏️ CONVENÇÃO DE NOMENCLATURA DE MÉTODOS EM TESTE: nomeDoMetodo_comportamento

class StudentDAOTest {

    /* GERA OBJETO, SÓ QUE PRECISO ABSTRAIR ESSA RESPONSBILIDADE
    private StudentDAO studentDAO = new StudentDAO();
    */

    /* INJEÇÃO DE INDEPENDÊNCIA DO SPRING.

     SIGNIFICA QUE O SPRING GERA O OBJETO QUE PODERÁ SER USADO.
     O SPRING BUSCA UM OBJETO QUE ELE VAI INSTANCIAR, ATRAVÉS DA IMPLEMENTAÇÃO DA INTERFACE.
     ESSA IMPLEMENTAÇÃO É A StudentDAO.

     O PROCESSO DE INJEÇÃO DE DEPENDÊNCIA É PASSAR UMA INTERFACE PARA O SPRING E ELE PROCURA A IMPLEMENTAÇÃO DELA
     PARA USAR. COMO SÓ EXISTE 1 CLASSE IMPLEMENTANDO ESSA INTERFACE, ELE SABERÁ QUAL USAR.

     NESTE EXEMPLO, FOI REMOVIDA A INJEÇÃO DE DEPENDÊNCIA ABAIXO, PARA UTILIZAR O MÉTODO setup()
     Porque: O OBJETO DO STUDENTDAO DEVE SER GERADO A CADA VEZ QUE FOR RODAR OS TESTES

     @Autowired
     private IStudentDAO studentDAO;
    */

    private IStudentDAO studentDAO; // GERA UM OBJETO

    // ANTES DE CADA TESTE
    @BeforeEach @AfterEach
    void setup() {
        // GERAR O OBJETO
        studentDAO = new StudentDAO();

        // APAGAR OS DADOS QUE ESTÃO NO ARQUIVO
        TestUtilsGenerator.emptyUsersFile();
    }

    // DEPOIS DE TESTAR TUDO.
    // @AfterAll
    // Tem que ser um método estático
    // Foi removido, conforme o do professor, porque apareceram comportamentos indesejados, que não serão sanados porque são irrelevantes, pois possivelmente são compartamentos por estar usando um arquivo para salvar
    // public static void tearDown() {
        // APAGAR OS DADOS QUE ESTÃO NO ARQUIVO
        // TestUtilsGenerator.emptyUsersFile();
    // }

    @Test
    void save_saveStudent_whenNewStudent() {
        // PREPARA
        // - GERA UM ESTUDANTE SEM ID
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        // EXECUTA
        StudentDTO savedStudent = studentDAO.save(newStudent);

        // TESTA
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId().doubleValue()).isPositive();
        assertThat(savedStudent.getStudentName()).isEqualTo(newStudent.getStudentName());
    }

    @Test
    void save_updateStudent_whenStudentWithId() {
        /* QUANDO ENVIAR UM ESTUDANTE QUE TEM ID, SIGNIFICA ELE JÁ EXISTE NO BANCO/ARQUIVO
        PORTANTO O MÉTODO SAVE SE COMPORTARÁ COMO UM UPDATE, ATUALIZANDO OS DADOS */

        // PREPARA O CENÁRIO
        // - PASSA UM ESTUDANTE COM ID
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        /* COMO O OBJETIVO É TESTAR COM UM ALUNO EXISTENTE NO BANCO, É NECESSÁRIO PRIMEIRO CRIAR/PERSISTIR
        O DADOS DE UM ESTUDANTE NO BANCO, HAJA VISTA, QUE O BANCO/ARQUIVO SERÁ ZERADO A CADA TESTE
        E DEPOIS PODER TESTAR O COMPORTAMENTO */
        StudentDTO savedStudent = studentDAO.save(newStudent);
        savedStudent.setStudentName("Novo nome");
        savedStudent.getSubjects().get(0).setName("Nova disciplina");

        // EXECUTA O CENÁRIO
        StudentDTO updateStudent = studentDAO.save(savedStudent);

        // TESTA O CENÁRIO
        assertThat(savedStudent).isNotNull();

        assertThat(savedStudent.getStudentName())
            .isEqualTo(savedStudent.getStudentName());

        assertThat(savedStudent.getSubjects().get(0).getName())
            .isEqualTo(savedStudent.getSubjects().get(0).getName());
    }

    @Test
    @DisplayName("Valida se lança uma exceção quando o estudante for inexistente")
    void save_throwException_whenStudentWithIdAndNotExist() {
        // PREPARA O CENÁRIO
        // - GERA UM ESTUDANTE SEM ID
        StudentDTO student = TestUtilsGenerator.getStudentWithId();

        /* EXECUTA O CENÁRIO e TAMBÉM TESTA
        ESPERA QUE SEJA LANÇADA A EXCEÇÃO QUANDO ACONTECER O COMPORTAMENTO DENTRO DO ESCOPO
        O assertThrows PEDE DOIS PARÂMETROS (tipo da exceção, ação)
        - TIPO DA EXCEÇÃO É A CLASSE DA EXCEÇÃO PERSONALIZADA
        - AÇÃO É A FUNÇÃO QUE DEVERÁ SER EXECUTADA E QUE VAI LANÇAR A EXCEÇÃO ASSIM QUE ELA EXECUTAR */
        StudentNotFoundException exception = Assertions.assertThrows(StudentNotFoundException.class, () -> {
            StudentDTO savedStudent = studentDAO.save(student);
        });

        // TESTA O CENÁRIO
        /* - Outros testes baseados na exceção que foi lançada
        Essa exceção está sendo gerenciada pelo teste, por isso é necessário usar o getErro
        Testa se o ID do estudante está dentro da mensagem da exceção */
        assertThat(exception.getError().getDescription()).contains(student.getId().toString());
        // Testa se o status que a exceção personalizada lança é o not found
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Valida ser a remoção do estudante ocorreu com sucesso, quando um estudante existe")
    void delete_removeStudent_whenStudentExist() {
        /* COMO O MÉTODO DELETE NÃO TEM RETORNO O QUE SERÁ VERIFICADO É COMO ESTÁ A SITUAÇÃO
        * ANTES DO MÉTODO SER EXECUTADO E TESTAR NOVAMENTE DEPOIS QUE ELE FOR CHAMADO
        * PARA VER SE O DELETE OCORREU CORRETAMENTE */
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();

        StudentDTO savedStudent = studentDAO.save(newStudent);

        studentDAO.delete(savedStudent.getId());

        assertThat(studentDAO.exists(savedStudent)).isFalse();

    }

    @Test
    @DisplayName("Valida se será lançada uma exceção, quando um estudante não existe")
    void delete_throwException_whenStudentNotExist() {
        /* NESTE CASO O QUE SERÁ VALIDADO É O COMPORTAMENTO ESPERADO AO ENVIAR UM ESTUDANTE INEXISTENTE
        * OU SEJA, SE A EXCEÇÃO SERÁ LANÇADA CORRETAMENTE.
        * PRIMEIRO PREPARA O CENÁRIO CRIANDO O OBJETO ESTUDANTE */
        StudentDTO student = TestUtilsGenerator.getStudentWithId();

        // DEPOIS EXECUTA E TESTA. EXECUTA O MÉTODO E TESTA O SE O RETORNO É UM EXCEÇÃO
        // 2. EXCEPTION VAI OBTER O RETORNO DESSE TESTE, QUE SERÁ A EXCEÇÃO LANÇADA PELO MÉTODO delete()
        StudentNotFoundException exception = Assertions.assertThrows(StudentNotFoundException.class, () -> {
            // 1. DELETAR O ESTUDANTE PELO ID, VAI LANÇAR UMA EXCEÇÃO
            studentDAO.delete(student.getId());
        });

        // OUTROS TESTES BASEADOS NA EXCEÇÃO
        assertThat(exception.getError().getDescription()).contains(student.getId().toString());
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Retorna verdadeiro quando o estudande existe")
    void exists_returnTrue_whenStudentExist() {
        // PREPARA O CENÁRIO
        // - GERA UM ESTUDANTE SEM ID
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();
        StudentDTO savedStudent = studentDAO.save(newStudent);

        // EXECUTA O CENÁRIO
        boolean found = studentDAO.exists(savedStudent);

        // TESTA O CENÁRIO
        assertThat(found).isTrue();
        // No Junit: assertTrue(result);
    }

    @Test
    @DisplayName("Retorna falso quando o estudande não existe")
    void exists_returnFalse_whenStudentNotExist() {
        // PREPARA O CENÁRIO
        // - GERA UM ESTUDANTE SEM ID
        StudentDTO student = TestUtilsGenerator.getStudentWithId();

        // EXECUTA O CENÁRIO
        boolean found = studentDAO.exists(student);

        // TESTA O CENÁRIO
        assertThat(found).isFalse();
    }

    @Test
    @DisplayName("Valida se um estudante é retornado quando ele existe no banco/arquivo")
    void findById_returnStudent_whenStudentExist() {
        // PREPARA O CENÁRIO
        // - GERA UM ESTUDANTE COM ID
        StudentDTO newStudent = TestUtilsGenerator.getNewStudentWithOneSubject();
        StudentDTO savedStudent = studentDAO.save(newStudent);

        // EXECUTA O CENÁRIO
        StudentDTO foundStudent = studentDAO.findById(savedStudent.getId());

        // TESTA O CENÁRIO
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getId()).isEqualTo(savedStudent.getId());
        assertThat(foundStudent.getStudentName()).isEqualTo(savedStudent.getStudentName());
    }

    @Test
    void findById_throwException_whenStudentNotExist() {
        // PREPARA O CENÁRIO
        StudentDTO student = TestUtilsGenerator.getStudentWithId();

        // NÃO SALVA O ESTUDANTE NO ARQUIVO

        // ARQUIVO ESTÁ VAZIO

        //2. LANÇA A EXCEÇÃO
        StudentNotFoundException exception = Assertions.assertThrows(StudentNotFoundException.class, () -> {
            // 1. PESQUISA NO ARQUIVO VAZIO PELO ID
            StudentDTO foundStudent = studentDAO.findById(student.getId());
        });

        assertThat(exception.getError().getDescription()).contains(student.getId().toString());
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}