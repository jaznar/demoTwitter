package com.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Interface TwitterRepository.
 */
public interface TwitterRepository extends JpaRepository<TweetEntity, Long> {

	/**
	 * Find all by user and validate.
	 *
	 * @param user     the user
	 * @param validate the validate
	 * @return the list
	 */
	List<TweetEntity> findAllByUserAndValidate(String user, boolean validate);
}
