package com.meli.obterdiploma.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.meli.obterdiploma.exception.StudentNotFoundException;
import com.meli.obterdiploma.model.StudentDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


// ESSA CLASSE TEM POR FUNÇÃO, MANIPULAR OS DADOS NO ARQUIVO JSON

@Repository
public class StudentDAO implements IStudentDAO {

    private String SCOPE;

    /* ESSA ESTRUTURA SET NÃO PERMITE REPETIÇÕES, LOGO QUANDO REALIZA O UPDATE ESTÁ CRIANDO UM NOVO OBJETO
    * E NÃO REALIZANDO A ATUALIZAÇÃO DO MESMO OBJETO. OU SEJA, NO SALVAR O OBJETO É ENCONTRADO PELO ID,
    * POREM COMO O SET NÃO DEIXA REPETIR O ID, ELE CRIA UM NOVO COM UM OUTRO ID. */
    // ⚠️ O PROFESSOR IRÁ REALIZAR O AJUSTE. BAIXAR A ATUALIZAÇÃO DEPOIS.
    private Set<StudentDTO> students; // CONJUNTO DE ESTUDANTES. UMA LISTA SALVA EM MEMÓRIA


    public StudentDAO() {
        Properties properties = new Properties();

        try {
            properties.load(new ClassPathResource("application.properties").getInputStream());
            this.SCOPE = properties.getProperty("api.scope");
            this.loadData(); // carega os dados que estão em arquivo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SE PASSAR UM ESTUDANTE SEM ID, É ADIÇÃO DE ALUNO
    // SE PASSAR UM ESTUDANTE COM ID, É UMA ATUALIZAÇÃO, E SE O ID FOR INVÁLIDO DEVE TER UM ERRO
    @Override
    public StudentDTO save(StudentDTO student) {
        Optional<StudentDTO> studentDTO = students.stream()
                .filter(s -> Objects.equals(s.getId(), student.getId())).findFirst();

        // SE O ID É INVÁLIDO
        if (student.getId() != null && studentDTO.isEmpty()) {
            throw new StudentNotFoundException(student.getId());
        }

        // SE O ESTUDANTE NÃO EXISTE, GERA UM ID. TAMANHO DA LISTA + 1
        if (studentDTO.isEmpty()) {
            student.setId((this.students.size() + 1L)); // 1L É PORQUE O TIPO É Long id;
        }

        // ADICIONA NA LISTA
        students.add(student);

        // GRAVA OS DADOS NO ARQUIVO
        this.saveData();

        // RETORNA O ESTUDANTE CRIADO OU ATUALIZADO
        return student;
    }

    @Override
    public void delete(Long id) {
        StudentDTO found = this.findById(id);

        students.remove(found);
        this.saveData();
    }

    public boolean exists(StudentDTO stu) {
        boolean ret = false;

        try {
            ret = this.findById(stu.getId()) != null;
        } catch (StudentNotFoundException e) {
        }

        return ret;
    }

    @Override
    public StudentDTO findById(Long id) {
        return students.stream()
                .filter(stu -> stu.getId().equals(id))
                .findFirst().orElseThrow(() -> new StudentNotFoundException(id));
    }

    // MÉTODOS AUXILIARES RESPONSÁVEIS POR CARREGAR E SALVAR DADOS NO ARQUIVO
    private void loadData() {
        Set<StudentDTO> loadedData = new HashSet<>();

        ObjectMapper objectMapper = new ObjectMapper();
        File file;
        try {
            // SCOPE DEFINE SE SERÁ O ARQUIVO DA MAIN OU TEST
            file = ResourceUtils.getFile("./src/" + SCOPE + "/resources/users.json");
            loadedData = objectMapper.readValue(file, new TypeReference<Set<StudentDTO>>() {
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed while initializing DB, check your resources files");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed while initializing DB, check your JSON formatting.");
        }

        this.students = loadedData;
    }

    private void saveData() {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = ResourceUtils.getFile("./src/" + SCOPE + "/resources/users.json");
            objectMapper.writeValue(file, this.students);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed while writing to DB, check your resources files");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed while writing to DB, check your JSON formatting.");
        }
    }
}
