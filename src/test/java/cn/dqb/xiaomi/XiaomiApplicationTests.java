package cn.dqb.xiaomi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XiaomiApplicationTests {

	@Test
	void contextLoads() {
		XiaoMiSetStep task = new XiaoMiSetStep("18008105185", "asd5252262", 17998);
		task.run();
	}

	@Test
	void contextLoads1() {
		XiaoMiSetStep task = new XiaoMiSetStep("18040454015", "QUAN1994425", 12553);
		task.run();
	}

}
