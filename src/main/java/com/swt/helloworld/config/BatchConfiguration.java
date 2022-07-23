package com.swt.helloworld.config;

import com.swt.helloworld.listener.HwJobExecutionListener;
import com.swt.helloworld.listener.HwStepExecutionListener;
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
  private  final HwStepExecutionListener hwStepExecutionListener;

  public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, HwJobExecutionListener jobExecutionListener,
      HwStepExecutionListener hwStepExecutionListener) {
    this.jobs = jobs;
    this.steps = steps;
    this.jobExecutionListener = jobExecutionListener;
    this.hwStepExecutionListener = hwStepExecutionListener;
  }

  @Bean
  public Step step1() {
    return steps.get("step1")
        .listener(hwStepExecutionListener)
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
        System.out.println("helloWorldTasklet - Hello world");
        return RepeatStatus.FINISHED;
      }
    });
  }


}
