package com.foreach.across.modules.platform.application.controllers;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Marc Vanbrabant
 */
@RestController
public class SpringBatchController
{
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobRegistry jobRegistry;

	@RequestMapping("springBatch/run")
	public Map<String, Object> run() throws NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InterruptedException {
		Map<String, JobParameter> params = new HashMap<>();
		params.put( "randomString", new JobParameter( RandomStringUtils.randomAscii( 50 ) ) );
		params.put( "jobDone", new JobParameter( "ok" ) );

		Job job = jobRegistry.getJob( "PlatformTestApplicationModule.testJob" );
		JobParameters parameters = new JobParameters( params );
		JobExecution execution = jobLauncher.run( job, parameters );
		boolean found = false;
		for ( int i = 0; i < 3; i++ ) {
			JobExecution jobExecution = jobRepository.getLastJobExecution( "PlatformTestApplicationModule.testJob",
			                                                               parameters );
			if ( BatchStatus.COMPLETED == jobExecution.getStatus() ) {
				Map<String, Object> jobParameters =
						jobExecution.getJobParameters().getParameters().entrySet().stream().collect(
								Collectors.toMap( Map.Entry::getKey, entry -> entry.getValue().getValue() ) );
				return jobParameters;
			}
			Thread.sleep( 500 );
		}
		return Collections.emptyMap();
	}

	public static class JobStatus
	{

	}
}
