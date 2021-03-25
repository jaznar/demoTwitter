package com.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import twitter4j.TwitterException;

/**
 * The Class TwitterController.
 */
@RestController
@RequestMapping("/v1/tweets")
public class TwitterController {

	/** The service. */
	@Autowired
	private TwitterService service;

	/**
	 * Gets the tweets.
	 *
	 * @return the tweets
	 */
	@ApiOperation(value = "Tweets", notes = "Retrieve list tweets.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Request is malformed or there are missing mandatory parameters."),
			@ApiResponse(code = 405, message = "Request method not supported."),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweets() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	/**
	 * Gets the tweets by user.
	 *
	 * @param user the user
	 * @return the tweets by user
	 */
	@ApiOperation(value = "Tweets", notes = "Retrieve list validate tweets by user.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Request is malformed or there are missing mandatory parameters."),
			@ApiResponse(code = 405, message = "Request method not supported."),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TweetEntity>> getTweetsByUser(@PathVariable final String user) {
		return new ResponseEntity<>(service.findAllByUserAndValidate(user), HttpStatus.OK);
	}

	/**
	 * Validate tweet.
	 *
	 * @param idTweet the id tweet
	 * @return the response entity
	 */
	@ApiOperation(value = "Validate a Tweet", notes = "Method to enabled validated a tweet")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Technical Error") })
	@PutMapping(value = "/{idTweet}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> validateTweet(@PathVariable final String idTweet) {
		try {
			if (service.validate(idTweet)) {
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * Gets the top trending.
	 *
	 * @param top the top
	 * @return the top trending
	 * @throws TwitterException the twitter exception
	 */
	@ApiOperation(value = "Tweets", notes = "Retrieve list tweets trending by default 10")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 500, message = "Technical Error") })
	@GetMapping(value = "/trending/{top}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getTopTrending(
			@ApiParam(value = "The top. Default 10", required = false) @PathVariable final String top)
			throws TwitterException {
		return new ResponseEntity<>(service.topTrending(top), HttpStatus.OK);
	}

}
