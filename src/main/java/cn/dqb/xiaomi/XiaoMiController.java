package cn.dqb.xiaomi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class XiaoMiController {

	@Autowired
	private RedisService redisService;

	@RequestMapping("/access/{mobile}")
	@ResponseBody
	public String access(String access, @PathVariable("mobile") String mobile) {
		log.info("收到access：{}", access);
		redisService.set(mobile, access, 3600 * 12);
		return "success";
	}
}