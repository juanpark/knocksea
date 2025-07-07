package com.board.repository;

import com.board.domain.Tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
	public Optional<Tag> findByName(String name);
}
