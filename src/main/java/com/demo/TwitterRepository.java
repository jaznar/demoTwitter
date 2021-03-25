package com.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TwitterRepository extends JpaRepository<TweetEntity, Long>{
	List<TweetEntity> findAllByUserAndValidate(String user, boolean validate);
}
