package com.meli.obterdiploma.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.meli.obterdiploma.model.StudentDTO;
import com.meli.obterdiploma.model.SubjectDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

// GERA OS ESTUDANTES QUE SERÃO USADOS NOS TESTES

public class TestUtilsGenerator {

    private static String SCOPE;
    private static ObjectWriter mapper;

    // CADA VEZ QUE RODAR UM TESTE, ESSE MÉTODO SERÁ CHAMADO PARA APAGAR TUDO O QUE ESTIVER DENTRO DO ARQUIVO
    // POIS UMA COISA NÃO PODE DEPENDER DE OUTRA
    public static void emptyUsersFile() {
        Properties properties = new Properties();

        try {
            properties.load(new ClassPathResource("application.properties").getInputStream());
            SCOPE = properties.getProperty("api.scope");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(ResourceUtils.getFile("./src/" + SCOPE + "/resources/users.json"));
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        writer.print("[]");
        writer.close();
    }

    // GERA 1 ESTUDANTE SEM ID
    public static StudentDTO getNewStudentWithOneSubject() {
        List<SubjectDTO> subjects = new ArrayList<>();

        SubjectDTO subject1 = new SubjectDTO("Matemática", 8.0);
        subjects.add(subject1);

        return StudentDTO.builder()
                .studentName("Marcos")
                .subjects(subjects)
                .build();
    }

    // GERA 1 ESTUDANTE COM ID
    public static StudentDTO getStudentWithId() {
        List<SubjectDTO> subjects = new ArrayList<>();

        SubjectDTO subject1 = new SubjectDTO("Matemática", 8.0);
        subjects.add(subject1);

        return StudentDTO.builder()
                .id(1L) // RECOMENDADO USAR NÚMERO SIMPLES, PARA NÃO GERAR CONFUSÃO
                .studentName("Marcos")
                .subjects(subjects)
                .build();
    }

    public static StudentDTO getStudentWith3Subjects(String name) {
        SubjectDTO subject1 = new SubjectDTO("Matemática", 8.0);
        SubjectDTO subject2 = new SubjectDTO("Língua Portuguesa", 6.0);
        SubjectDTO subject3 = new SubjectDTO("Física", 4.0);

        List<SubjectDTO> subjects = new ArrayList<>();
        subjects.add(subject1);
        subjects.add(subject2);
        subjects.add(subject3);

        StudentDTO stu = new StudentDTO();
        stu.setId(9999L);
        stu.setStudentName(name);
        stu.setSubjects(subjects);

        return stu;
    }

    public static StudentDTO getStudentWith3SubjectsAverageOver9(String name) {
        SubjectDTO subject1 = new SubjectDTO("Matemática", 8.0);
        SubjectDTO subject2 = new SubjectDTO("Língua Portuguesa", 9.0);
        SubjectDTO subject3 = new SubjectDTO("Física", 10.0);

        List<SubjectDTO> subjects = new ArrayList<>();
        subjects.add(subject1);
        subjects.add(subject2);
        subjects.add(subject3);

        StudentDTO stu = new StudentDTO();
        stu.setId(9999L);
        stu.setStudentName(name);
        stu.setSubjects(subjects);

        return stu;
    }

    public static StudentDTO getStudentWithId(Long id) {
        SubjectDTO subject1 = new SubjectDTO("Matemática", 8.0);
        SubjectDTO subject2 = new SubjectDTO("Língua Portuguesa", 6.0);
        SubjectDTO subject3 = new SubjectDTO("Física", 4.0);

        List<SubjectDTO> subjects = new ArrayList<>();
        subjects.add(subject1);
        subjects.add(subject2);
        subjects.add(subject3);

        StudentDTO stu = new StudentDTO();
        stu.setId(id);
        stu.setStudentName("student1");
        stu.setSubjects(subjects);

        return stu;
    }

    public static Set<StudentDTO> getStudentSet() {
        StudentDTO stu1 = getStudentWith3Subjects("Marcos");
        StudentDTO stu2 = getStudentWith3Subjects("Carlos");
        StudentDTO stu3 = getStudentWith3Subjects("Felipe");

        return new HashSet<StudentDTO>() {{
            add(stu1);
            add(stu2);
            add(stu3);
        }};
    }

    public static void appendNewStudent(StudentDTO stu) {
        mapper = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer().withDefaultPrettyPrinter();

        PrintWriter writer = null;

        try {
            String content = Files.readString(new File("./src/" + SCOPE + "/resources/users.json").getAbsoluteFile().toPath(), StandardCharsets.US_ASCII);
            writer = new PrintWriter(ResourceUtils.getFile("./src/" + SCOPE + "/resources/users.json"));

            try {
                String studentAsString = mapper.writeValueAsString(stu);
                writer.print(content.substring(0, content.length() - 1));
                if (content.length() > 2) {
                    writer.print(", ");
                }
                writer.print(studentAsString);
                writer.print("]");
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.close();
    }

}
