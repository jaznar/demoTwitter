package com.demo;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@ApiOperation(value = "Tweets", notes = "Retrieve list tweets.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Request is malformed or there are missing mandatory parameters."),
			@ApiResponse(code = 405, message = "Request method not supported."),
			@ApiResponse(code = 500, message = "Internal server error")})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweets(){
		return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
	}

	@ApiOperation(value = "Tweets", notes = "Retrieve list validate tweets by user.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Request is malformed or there are missing mandatory parameters."),
			@ApiResponse(code = 405, message = "Request method not supported."),
			@ApiResponse(code = 500, message = "Internal server error")})
	@GetMapping(value ="/{user}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweetsByUser(@PathVariable final String user){
		List<TweetEntity> result = repo.findAllByUserAndValidate(user, Boolean.TRUE);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "Validate a Tweet", notes = "Method to enabled validated a tweet")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Technical Error")})
	@PutMapping(value = "/{idTweet}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> validateTweet(@PathVariable final String idTweet) {
		try {
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

	@ApiOperation(value = "Tweets", notes = "Retrieve list tweets trending by default 10")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Technical Error")})
	@GetMapping(value ="/trending/{top}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getTopTrending(@ApiParam(value = "The top. Default 10", required = false) @PathVariable final String top) throws TwitterException{
		int topHashtags = 10;
		try{
			topHashtags = Integer.valueOf(top);
		} catch (Exception e){
			;
		}

		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		Trends trends = twitter.getPlaceTrends(1);

		List<String> result =  Arrays.stream(trends.getTrends()).map(trend->trend.getName()).limit(topHashtags).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "Run feed tweets", notes = "Start to popullate database with tweets")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Technical Error")})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> run() {
		streamFeed.run();
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
