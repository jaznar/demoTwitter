package com.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.TwitterStreamFactory;

/**
 * The Class TwitterStreamService.
 */
@Service
@Slf4j
public class TwitterStreamService {

	@Getter
	@Setter
	private boolean running;

	@Autowired
	private TwitterRepository repo;
	
	private List<String> languages = Arrays.asList(new String[]{"es", "fr", "it"});

	
	/**
	 * Run Twitter Stream service after startup.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void runTwitterStreamService() {
		if (this != null) {
			this.run();
		}
	}

	/**
	 * Run.
	 */
	
	public void run() {
		if (running) {
			return;
		}
		
		new TwitterStreamFactory()
				.getInstance()
				.onStatus(this::saveEntity)
				.sample();
		
		running = Boolean.TRUE;
	}
	

	/**
	 * Run Twitter Stream service after startup.
	 */
	
	private void saveEntity(Status status) {
		if (status == null) return;
		
		try {
			boolean language = languages.contains(status.getLang());
			if (language && status.getUser().getFollowersCount() >= 1500) {
//				log.debug(TweetEntity.valueOf(status).toString());
				repo.save(TweetEntity.valueOf(status));
			}
		} catch (Exception e) {
			log.debug(e.toString());
		}
	}

	
}
