package cn.dqb.xiaomi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XiaomiApplicationTests {

	@Test
	void contextLoads() {
		XiaoMiSetStep task = new XiaoMiSetStep("18040454015", "QUAN1994425", 11252);
		task.run();
	}

}
