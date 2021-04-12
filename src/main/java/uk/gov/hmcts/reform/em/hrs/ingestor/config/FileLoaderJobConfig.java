package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.em.hrs.ingestor.job.FileLoaderJob;
import uk.gov.hmcts.reform.em.hrs.ingestor.job.FileLoaderTasklet;

@Configuration
public class FileLoaderJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private FileLoaderJob fileLoaderJob;

    @Bean
    public Job fileLoaderJob(Step fileLoaderStep) {
        return jobBuilders.get("fileLoaderJob").start(fileLoaderStep)
            .build();
    }

    @Bean
    public Step fileLoaderStep() {
        return stepBuilders.get("fileLoaderStep")
            .tasklet(tasklet())
            .build();
    }

    public Tasklet tasklet() {
        return new FileLoaderTasklet(fileLoaderJob);
    }

}


