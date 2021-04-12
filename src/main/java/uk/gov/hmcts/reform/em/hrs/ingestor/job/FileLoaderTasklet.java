package uk.gov.hmcts.reform.em.hrs.ingestor.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class FileLoaderTasklet implements Tasklet {

    private final Logger log = LoggerFactory.getLogger(FileLoaderTasklet.class);

    private FileLoaderJob fileLoaderJob;

    public FileLoaderTasklet(FileLoaderJob fileLoaderJob) {
        this.fileLoaderJob = fileLoaderJob;
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution,
                                ChunkContext chunkContext) {

        log.info("About to run file loader job.");

        fileLoaderJob.run();

        log.info("File loader job complete.");

        return RepeatStatus.FINISHED;
    }
}
