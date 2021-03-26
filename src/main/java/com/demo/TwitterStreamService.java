package com.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
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

	/**
	 * Run.
	 */
	public void run() {
		if (running) {
			return;
		}

		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(getListener());
		twitterStream.sample();
		
//		twitterStream.filter(new FilterQuery("*", " ").language("es", "fr", "it").follow(1500l));

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			twitterStream.shutdown();
		}));

		running = Boolean.TRUE;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	private StatusListener getListener() {
		List<String> languages = Arrays.asList(new String[]{"es", "fr", "it"});
		
		StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				boolean language = languages.contains(status.getLang());
				if (language && status.getUser().getFollowersCount() >= 1500) {
					log.info(status.getLang());
					repo.save(TweetEntity.valueOf(status));
				}	
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// TODO Auto-generated method stub
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// TODO Auto-generated method stub
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub

			}
		};
		return listener;
	}
}
