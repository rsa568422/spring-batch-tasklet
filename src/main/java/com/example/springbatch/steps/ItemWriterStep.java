package com.example.springbatch.steps;

import com.example.springbatch.entities.Person;
import com.example.springbatch.services.IPersonService;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ItemWriterStep implements Tasklet {

    private final IPersonService service;

    public ItemWriterStep(IPersonService service) {
        this.service = service;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("========= INICIO DEL PASO DE ESCRITURA =========");

        String json = (String) chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("processedPersons");

        Person[] persons = new GsonBuilder().create().fromJson(json, Person[].class);

        assert persons != null;
        Arrays.stream(persons).map(Person::toString).forEach(log::info);

        service.saveAll(Arrays.asList(persons));

        log.info("========== FIN DEL PASO DE ESCRITURA ===========");

        return RepeatStatus.FINISHED;
    }
}
