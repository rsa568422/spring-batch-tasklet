package com.example.springbatch.steps;

import com.example.springbatch.entities.Person;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ItemProcessorStep implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("======= INICIO DEL PASO DE PROCESAMIENTO =======");

        String personsJson = (String) chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("persons");

        Person[] persons = new GsonBuilder().create().fromJson(personsJson, Person[].class);

        assert persons != null;
        List<Person> processedPersons = Arrays.stream(persons).map(person -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            Person newPerson = new Person();
            newPerson.setId(person.getId());
            newPerson.setName(person.getName());
            newPerson.setLastName(person.getLastName());
            newPerson.setAge(person.getAge());
            newPerson.setInsertionDate(formatter.format(LocalDateTime.now()));
            return newPerson;
        }).collect(Collectors.toList());

        String processedPersonsJson = new GsonBuilder().create().toJson(processedPersons);

        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("processedPersons", processedPersonsJson);

        log.info("======== FIN DEL PASO DE PROCESAMIENTO =========");

        return RepeatStatus.FINISHED;
    }
}
