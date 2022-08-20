package cn.dqb.xiaomi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> jedisTemplate;

	private ValueOperations<String, String> stringValueOperations;

	@PostConstruct
	public void init() {
		stringValueOperations = jedisTemplate.opsForValue();
	}


	public Long decrement(String redisKey) {
		return stringValueOperations.decrement(redisKey);
	}


	public Long increment(String redisKey) {
		return stringValueOperations.increment(redisKey);
	}


	public void set(String key, String value, long second) {
		stringValueOperations.set(key, value, second, TimeUnit.SECONDS);
	}


	public String get(String key) {
		return stringValueOperations.get(key);
	}

	public Boolean del(String key) {
		return jedisTemplate.delete(key);
	}
}
