package com.demo;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Service
public class TwitterStreamService {

	@Getter
	@Setter
	private boolean running;
	
	@Autowired
	private TwitterRepository repo;
	
	public void run() {
		if (running) {return;}
		
		StatusListener listener = new StatusListener(){
	        public void onStatus(Status status) {
		            TweetEntity tweet = new TweetEntity();
		            tweet.setLocation(status.getUser().getLocation());
		            
		            String text;
		            if(status.getText().length() > 255 ){
		                text = status.getText().substring(0, 254);
		            } else {
		            	text = status.getText();
		            }
		            tweet.setId(status.getId());
		            tweet.setText(new String(text.getBytes(StandardCharsets.UTF_8)));
		            try {
		            	tweet.setUser(new String(status.getUserMentionEntities()[0].getScreenName().getBytes(StandardCharsets.UTF_8)));
		            } catch (Exception e) {
		            	tweet.setUser(new String(status.getUser().getName().getBytes(StandardCharsets.UTF_8)));
		            }
		            repo.save(tweet);
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
		
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    
	    twitterStream.addListener(listener);
	    twitterStream.sample();
	    twitterStream.filter(new FilterQuery("*", " ").language("es","fr","it").follow(1500l));
	    
	    Runtime.getRuntime().addShutdownHook(
	            new Thread(() -> {
	              twitterStream.shutdown();
	            }));
	    
	    running = Boolean.TRUE;
	}
}
