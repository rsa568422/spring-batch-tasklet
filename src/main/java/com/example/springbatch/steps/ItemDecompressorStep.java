package com.example.springbatch.steps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class ItemDecompressorStep implements Tasklet {

    private final ResourceLoader resourceLoader;

    public ItemDecompressorStep(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("======= INICIO DEL PASO DE DESCOMPRESIÓN =======");

        Resource resource = resourceLoader.getResource("classpath:files/persons.zip");
        String filePath = resource.getFile().getAbsolutePath();

        ZipFile zipFile = new ZipFile(filePath);
        File targetPath = new File(resource.getFile().getParent(), "target");

        if (!targetPath.exists())
            targetPath.mkdir();

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(targetPath, entry.getName());
            if (file.isDirectory()) {
                file.mkdir();
            } else {
                InputStream inputStream = zipFile.getInputStream(entry);
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                inputStream.close();
            }
        }

        zipFile.close();

        log.info("======== FIN DEL PASO DE DESCOMPRESIÓN =========");

        return RepeatStatus.FINISHED;
    }
}
