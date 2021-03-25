package com.demo;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@RestController
@RequestMapping("/v1/tweets")
public class TwitterController {

	@Autowired
	private TwitterStreamService streamFeed;
	
	@Autowired
	private TwitterRepository repo;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweets(){
		streamFeed.run();
		return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
	}

	@GetMapping(value ="/{user}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweetsByUser(@PathVariable final String user){
		streamFeed.run();
		List<TweetEntity> result = repo.findAllByUserAndValidate(user, Boolean.TRUE);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PutMapping(value = "/{idTweet}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> validateTweet(@PathVariable final String idTweet) {
		try {
			streamFeed.run();
			Optional<TweetEntity> optionalTweet = repo.findById(Long.valueOf(idTweet));
			if (optionalTweet.isPresent()) {
				optionalTweet.get().setValidate(Boolean.TRUE);
				repo.save(optionalTweet.get());
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {	
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@GetMapping(value ="/trending/{top}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getTopTrending(@PathVariable final String top) throws TwitterException{
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		Trends trends = twitter.getPlaceTrends(1);
		List<String> result =  Arrays.stream(trends.getTrends()).map(trend->trend.getName()).limit(Integer.valueOf(top)).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
}
