package cn.dqb.xiaomi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class XiaomiApplication {

	public static void main(String[] args) {
		SpringApplication.run(XiaomiApplication.class, args);
	}


}
