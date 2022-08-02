package com.swt.helloworld.config;

import com.swt.helloworld.listener.HwJobExecutionListener;
import com.swt.helloworld.listener.HwStepExecutionListener;
import com.swt.helloworld.model.Product;
import com.swt.helloworld.processor.InMemItemProcessor;
import com.swt.helloworld.processors.UppercaseProcessor;
import com.swt.helloworld.reader.InMemReader;
import com.swt.helloworld.writer.ConsoleItemWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
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
        .reader(flatFileItemReader(null))
        .processor(new UppercaseProcessor())
//        .reader(flatFileItemReader(null))
        .faultTolerant()
        .skip(FlatFileFormatException.class)
        .writer(dbWriter2())
        .build();

  }

  // define a stepScope, so the jobParameter gets passed into this function
  @StepScope
  @Bean
  public FlatFileItemReader flatFileItemReader(@Value("#{jobParameters['fileInput']}") FileSystemResource inputFile) {
    FlatFileItemReader reader = new FlatFileItemReader();
    // step 1, let reader know where the file is.
    reader.setResource(inputFile);
    // step 2, skip the header
    reader.setLinesToSkip(1);
    // step 3, create the line Mapper
    reader.setLineMapper(new DefaultLineMapper<Product>() {
                           {
                             setLineTokenizer(new DelimitedLineTokenizer() {
                               {
                                 setNames(new String[]{
                                     "productID", "productName", "productDesc", "price", "unit"
                                 });
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
    return reader;
  }

  @StepScope
  @Bean
  public FlatFileItemWriter flatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource outputFile) {
    FlatFileItemWriter writer = new FlatFileItemWriter();
    writer.setResource(outputFile);
    writer.setLineAggregator(new DelimitedLineAggregator() {
      {
        setDelimiter("|");
        setFieldExtractor(new BeanWrapperFieldExtractor() {{
          setNames(new String[]{"productID", "productName", "productDesc", "price", "unit"
          });
        }});
      }
    });
    return writer;
  }

  public JdbcBatchItemWriter dbWriter() {
    JdbcBatchItemWriter writer = new JdbcBatchItemWriter();
    writer.setDataSource(this.dataSource);
    writer.setSql("insert into spring.products (prod_id, prod_name, prod_desc, unit, price) VALUES  (?, ?, ?, ?, ?)");
    writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<Product>() {
      @Override
      public void setValues(Product item, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, item.getProductID());
        preparedStatement.setString(2, item.getProductName());
        preparedStatement.setString(3, item.getProductDesc());
        preparedStatement.setBigDecimal(4, item.getPrice());
        preparedStatement.setInt(5, item.getUnit());
      }
    });

    return writer;
  }

  @Bean
  public JdbcBatchItemWriter dbWriter2() {
    return new JdbcBatchItemWriterBuilder<Product>()
        .dataSource(this.dataSource)
        .sql("insert into spring.products (prod_id, prod_name, prod_desc, unit, price) VALUES  (:productID, :productName, :productDesc, :price, :unit)")
        .beanMapped()
        .build();
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
