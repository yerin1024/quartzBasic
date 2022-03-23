package com.example.demo;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

@Component //Spring bean으로 등록하기 위해 
public class testScheduler {
	
	private SchedulerFactory schedulerFactory;
	private Scheduler scheduler;
	
	//실제 스케줄러를 구현한 start() 메소드에 @postConstruct 어노테이션을 선언하여 해당 클래스가 인스턴스화 되자마자 자동으로 동작하도록 한다.
	//즉 Class가 인스턴스화 되자마자 start() 메소드가 동작하게 되며, 
	//스케줄러를 통한 배치 Job은 사용자의 동작 없이 자동으로 수행하게 하기 위한 로직이므로 어딘가에서 method를 호출하여 실행하기보다는 자동으로 로직이 수행되도록 구현해야 한다.
	@PostConstruct  
	public void start() throws SchedulerException {
		schedulerFactory = new StdSchedulerFactory();
		scheduler = schedulerFactory.getScheduler(); //quartzScheduler를 quartz.SchedulerFactory Class의 getScheduler() 메소드를 통해 지정
		scheduler.start(); //quartzScheduler를 start() 해주는 것으로 스케줄러를 시작하겠다는 명령을 내리게 된다.
		
		//작성한 Job을 지정한다. 이때 identity는 해당 Job의 고유명을 지정하도록 한다. 
		//같은 Job로직이라도 서로 다른 스케줄로 동작하게 할 경우가 있기 때문에 각각의 Job은 고유한 identity를 가져야 한다.
		JobDetail job = JobBuilder.newJob(testJob.class).withIdentity("firstJob").build();
		
		//Trigger를 생성한다.
		//Trigger는 TriggerBuilder Class를 사용하여 구현하며, 스케줄러를 수행할 스케줄 정보를 담고 있다. 
		//이때 cron문법을 사용하여 스케줄을 지정하는 방법을 주로 사용한다.
		Trigger trigger = TriggerBuilder.newTrigger()
										.withSchedule(CronScheduleBuilder.cronSchedule("30 * * * * ?"))
//										.startAt(new Date()).endAt(new Date()) //startAt과 endAt을 사용하여 Job 스케줄의 시작, 종료 시간을 지정할 수 있다.
										.build();
		
		//qurtz스케줄러에 Job과 Trigger를 연결한다. 
		//Job과 Trigger를 여러 개 만들어 각각 quartz스케줄러에 지정해주면 여러 개의 Job스케줄이 동시에 작동하기 된다.
		scheduler.scheduleJob(job, trigger);
	}
}
