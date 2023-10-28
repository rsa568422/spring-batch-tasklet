package com.example.springbatch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/upload")
    public ResponseEntity<Object> receiveFile(@RequestParam(name = "file")MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();

        try {
            Path path = Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "files" + File.separator + fileName);

            Files.createDirectories(path.getParent());
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            log.info("=========== INICIO DEL PROCESO BATCH ===========");

            assert fileName != null;
            JobParameters parameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .addString("fileName", fileName)
                    .toJobParameters();

            jobLauncher.run(job, parameters);

            log.info("============ FIN DEL PROCESO BATCH =============");

            Map<String, String> response = new HashMap<>();
            response.put("file", fileName);
            response.put("status", "received");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al iniciar el proceso batch: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
