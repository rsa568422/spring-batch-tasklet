package com.example.springbatch.configurations;

import com.example.springbatch.services.IPersonService;
import com.example.springbatch.steps.ItemDecompressorStep;
import com.example.springbatch.steps.ItemProcessorStep;
import com.example.springbatch.steps.ItemReaderStep;
import com.example.springbatch.steps.ItemWriterStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final IPersonService service;

    private final ResourceLoader resourceLoader;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfig(IPersonService service, ResourceLoader resourceLoader, StepBuilderFactory stepBuilderFactory) {
        this.service = service;
        this.resourceLoader = resourceLoader;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job importPersonsFromZipJob(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("importPersonsFromZipJob")
                .start(decompressorStep())
                .next(readerStep())
                .next(processorStep())
                .next(writerStep())
                .build();
    }

    @Bean
    public Step decompressorStep() {
        return stepBuilderFactory.get("itemDecompressorStep")
                .tasklet(itemDecompressorStep())
                .build();
    }

    @Bean
    public Step readerStep() {
        return stepBuilderFactory.get("itemReaderStep")
                .tasklet(itemReaderStep())
                .build();
    }

    @Bean
    public Step processorStep() {
        return stepBuilderFactory.get("itemProcessorStep")
                .tasklet(itemProcessorStep())
                .build();
    }

    @Bean
    public Step writerStep() {
        return stepBuilderFactory.get("itemWriterStep")
                .tasklet(itemWriterStep())
                .build();
    }

    @Bean
    @JobScope
    public ItemDecompressorStep itemDecompressorStep() {
        return new ItemDecompressorStep(resourceLoader);
    }

    @Bean
    @JobScope
    public ItemReaderStep itemReaderStep() {
        return new ItemReaderStep();
    }

    @Bean
    @JobScope
    public ItemProcessorStep itemProcessorStep() {
        return new ItemProcessorStep();
    }

    @Bean
    @JobScope
    public ItemWriterStep itemWriterStep() {
        return new ItemWriterStep(service);
    }
}
