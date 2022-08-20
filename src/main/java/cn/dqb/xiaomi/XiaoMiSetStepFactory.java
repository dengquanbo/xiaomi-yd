package cn.dqb.xiaomi;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XiaoMiSetStepFactory {

	public static XiaoMiSetStep getBoZai9() {
		int step = randomStep(2000, 3000);
		return new XiaoMiSetStep("18040454015", "QUAN1994425", step);
	}

	public static XiaoMiSetStep getBoZai14() {
		int step = randomStep(5000, 6000);
		return new XiaoMiSetStep("18040454015", "QUAN1994425", step);
	}

	public static XiaoMiSetStep getBoZai18() {
		int step = randomStep(8000, 9000);
		return new XiaoMiSetStep("18040454015", "QUAN1994425", step);
	}


	public static XiaoMiSetStep getBoZai21() {
		int step = randomStep(10000, 13000);
		return new XiaoMiSetStep("18040454015", "QUAN1994425", step);
	}

	private static int randomStep(int min, int max) {
		return RandomUtil.randomInt(min, max);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			System.out.println(randomStep(2000, 3000));
		}
	}
}