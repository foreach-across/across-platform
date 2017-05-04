package com.foreach.across.modules.platform.application.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * @author Marc Vanbrabant
 */
@Configuration
public class SpringBatchJobConfiguration
{
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job testJob( Tasklet stepScopeTasklet ) {
		return jobBuilderFactory.get( "testJob" ).start( step( stepScopeTasklet ) ).build();
	}

	@Bean
	protected Step step( Tasklet stepScopeTasklet ) {
		return stepBuilderFactory.get( "step" )
		                         .tasklet( stepScopeTasklet )
		                         .listener(
				                         new StepExecutionListener()
				                         {
					                         @Override
					                         public void beforeStep( StepExecution stepExecution ) {

					                         }

					                         @Override
					                         public ExitStatus afterStep( StepExecution stepExecution ) {
						                         stepExecution.getJobExecution().getExecutionContext()
						                                      .put( "returnValue", new BigDecimal( "12.30" ) );

						                         return ExitStatus.COMPLETED;
					                         }
				                         } )
		                         .build();

	}

	@Bean
	protected Tasklet stepScopeTasklet() {
		return new Tasklet()
		{
			@Override
			public RepeatStatus execute( StepContribution contribution,
			                             ChunkContext chunkContext ) throws Exception {
				return RepeatStatus.FINISHED;
			}
		};
	}
}
