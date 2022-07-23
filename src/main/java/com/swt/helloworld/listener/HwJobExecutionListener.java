package com.swt.helloworld.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Component
public class HwJobExecutionListener implements org.springframework.batch.core.JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    System.out.println("before starting the Job getJobInstance -" + jobExecution.getJobInstance().getJobName());
    jobExecution.getExecutionContext().put("my name", "yao");
    System.out.println("before starting the Job getExecutionContext - " + jobExecution.getExecutionContext().toString());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    System.out.println("after starting the Job " + jobExecution.getJobInstance().getJobName());
  }
}
