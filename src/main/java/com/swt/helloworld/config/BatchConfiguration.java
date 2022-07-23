package com.swt.helloworld.config;

import com.swt.helloworld.listener.HwJobExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

  private final JobBuilderFactory jobs;
  private final StepBuilderFactory steps;
  private final HwJobExecutionListener jobExecutionListener;

  public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, HwJobExecutionListener jobExecutionListener) {
    this.jobs = jobs;
    this.steps = steps;
    this.jobExecutionListener = jobExecutionListener;
  }

  @Bean
  public Step step1() {
    return steps.get("step1")
        .tasklet(helloWorldTasklet())
        .build();
  }

  @Bean
  public Job helloWorldJob() {
    return jobs.get("helloWorldJob")
        .listener(jobExecutionListener)
        .start(step1())
        .build();
  }

  public Tasklet helloWorldTasklet() {
    return (new Tasklet() {
      @Override
      public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Hello world");
        return RepeatStatus.FINISHED;
      }
    });
  }


}
