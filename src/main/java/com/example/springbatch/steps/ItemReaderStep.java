package com.example.springbatch.steps;

import com.example.springbatch.entities.Person;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemReaderStep implements Tasklet {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("========== INICIO DEL PASO DE LECTURA ==========");

        File file = resourceLoader.getResource("classpath:files/target/persons.csv").getFile();
        Reader reader = new FileReader(file);

        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(',')
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(csvParser)
                .withSkipLines(1)
                .build();

        List<Person> persons = new ArrayList<>();
        String[] line;

        while ((line = csvReader.readNext()) != null) {
            Person person = new Person();
            person.setName(line[0]);
            person.setLastName(line[1]);
            person.setAge(Integer.parseInt(line[2]));
            persons.add(person);
        }

        csvReader.close();
        reader.close();

        String json = new GsonBuilder().create().toJson(persons);

        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("persons", json);

        log.info("=========== FIN DEL PASO DE LECTURA ============");

        return RepeatStatus.FINISHED;
    }
}
