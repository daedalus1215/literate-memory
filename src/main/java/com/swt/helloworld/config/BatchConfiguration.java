package com.swt.helloworld.config;

import com.swt.helloworld.listener.HwJobExecutionListener;
import com.swt.helloworld.listener.HwStepExecutionListener;
import com.swt.helloworld.model.Product;
import com.swt.helloworld.processor.InMemItemProcessor;
import com.swt.helloworld.reader.InMemReader;
import com.swt.helloworld.writer.ConsoleItemWriter;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

  private final JobBuilderFactory jobs;
  private final StepBuilderFactory steps;
  private final HwJobExecutionListener jobExecutionListener;
  private final HwStepExecutionListener hwStepExecutionListener;
  private final DataSource dataSource;

  public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, HwJobExecutionListener jobExecutionListener,
      HwStepExecutionListener hwStepExecutionListener, DataSource dataSource) {
    this.jobs = jobs;
    this.steps = steps;
    this.jobExecutionListener = jobExecutionListener;
    this.hwStepExecutionListener = hwStepExecutionListener;
    this.dataSource = dataSource;
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
        .next(step2())
        .build();
  }

  @Bean
  public JdbcCursorItemReader jdbcCursorItemReader() {
    JdbcCursorItemReader reader = new JdbcCursorItemReader();
    reader.setDataSource(this.dataSource);
    reader.setSql("select prod_id as product_id, prod_name as product_name, prod_desc as product_desc, unit, price from products");
    reader.setRowMapper(new BeanPropertyRowMapper() {{
      setMappedClass(Product.class);
    }});
    return reader;
  }

  @Bean
  public Step step2() {
    return steps
        .get("step2")
        .<Integer, Integer>chunk(3)
//        .reader(flatFileItemReader(null))
        .reader(jdbcCursorItemReader())
        .writer(writer())
        .build();

  }

  // define a stepScope, so the jobParameter gets passed into this function
  @StepScope
  @Bean
  public FlatFileItemReader flatFileItemReader(@Value("#{jobParameters['fileInput']}") FileSystemResource inputFile) {
    FlatFileItemReader reader = new FlatFileItemReader();
    // step 1, let reader know where the file is.
    reader.setResource(inputFile);
    // step 2, create the line Mapper
    reader.setLineMapper(new DefaultLineMapper<Product>() {
                           {
                             setLineTokenizer(new DelimitedLineTokenizer() {
                               {
                                 setNames(new String[]{
                                     "productID", "productName", "productDesc", "price", "unit"
                                 });
                                 setDelimiter("|");
                               }

                             });
                             setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                               {
                                 setTargetType(Product.class);
                               }
                             });
                           }
                         }
    );
    // step 3, skip the header
    reader.setLinesToSkip(1);
    return reader;
  }

  private ItemWriter<? super Integer> writer() {
    return new ConsoleItemWriter();
  }

  private ItemProcessor<? super Integer, ? extends Integer> processor() {
    return new InMemItemProcessor();
  }

  private ItemReader<? extends Integer> reader() {
    return new InMemReader();
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
