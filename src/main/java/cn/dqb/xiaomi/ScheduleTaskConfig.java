package cn.dqb.xiaomi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTaskConfig {

	// @Scheduled(cron = "5 * * * * ? ")
	@Scheduled(cron = "0 0 9 * * ?")
	public void step1() {
		log.info("每天9点运行");
		XiaoMiSetStep step = XiaoMiSetStepFactory.getBoZai9();
		step.run();
	}

	@Scheduled(cron = "0 0 14 * * ?")
	public void step2() {
		log.info("每天14点运行");
		XiaoMiSetStep step = XiaoMiSetStepFactory.getBoZai14();
		step.run();
	}

	@Scheduled(cron = "0 0 18 * * ?")
	public void step3() {
		log.info("每天18点运行");
		XiaoMiSetStep step = XiaoMiSetStepFactory.getBoZai18();
		step.run();
	}

	@Scheduled(cron = "0 0 21 * * ?")
	public void step4() {
		log.info("每天21点运行");
		XiaoMiSetStep step = XiaoMiSetStepFactory.getBoZai21();
		step.run();
	}
}
