package com.demo;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import twitter4j.Status;

@Entity
@Table(name = "tweets")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TweetEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** The user. */
	private String user;

	/** The text. */
	private String text;

	/** The location. */
	private String location;

	/** The validate. */
	private boolean validate;

	/**
	 * Value of.
	 *
	 * @param status the status
	 * @return the tweet entity
	 */
	public static TweetEntity valueOf(Status status) {
		TweetEntity result = new TweetEntity();

		result.setId(status.getId());
		result.setLocation(status.getUser().getLocation());

		try {
			result.setText(new String(status.getText().substring(0, 254).getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			result.setText(new String(status.getText().getBytes(StandardCharsets.UTF_8)));
		}

		try {
			result.setUser(
					new String(status.getUserMentionEntities()[0].getScreenName().getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			result.setUser(new String(status.getUser().getName().getBytes(StandardCharsets.UTF_8)));
		}
		return result;

	}
}
