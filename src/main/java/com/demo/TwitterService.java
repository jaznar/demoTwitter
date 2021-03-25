package com.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * The Class TwitterService.
 */
@Service
public class TwitterService {

	/** The stream feed. */
	@Autowired
	private TwitterStreamService streamFeed;

	/** The repo. */
	@Autowired
	private TwitterRepository repo;

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public List<TweetEntity> findAll() {
		streamFeed.run();
		return repo.findAll();
	}

	/**
	 * Find all by user and validate.
	 *
	 * @param user the user
	 * @return the list
	 */
	public List<TweetEntity> findAllByUserAndValidate(String user) {
		streamFeed.run();
		return repo.findAllByUserAndValidate(user, Boolean.TRUE);
	}

	/**
	 * Validate.
	 *
	 * @param idTweet the id tweet
	 * @return true, if successful
	 */
	public boolean validate(String idTweet) {
		boolean result = false;
		streamFeed.run();
		Optional<TweetEntity> optionalTweet = repo.findById(Long.valueOf(idTweet));
		if (optionalTweet.isPresent()) {
			optionalTweet.get().setValidate(Boolean.TRUE);
			repo.save(optionalTweet.get());
			result = true;
		}
		return result;
	}

	/**
	 * Top trending.
	 *
	 * @param top the top
	 * @return the list
	 * @throws TwitterException the twitter exception
	 */
	public List<String> topTrending(String top) throws TwitterException {
		int topHashtags = 10;

		try {
			topHashtags = Integer.valueOf(top);
		} catch (Exception e) {
			;
		}

		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		Trends trends = twitter.getPlaceTrends(1);

		return Arrays.stream(trends.getTrends()).map(trend -> trend.getName()).limit(topHashtags)
				.collect(Collectors.toList());

	}
}
