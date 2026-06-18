package com.javahero.repository;

import com.javahero.model.HeroContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroContentRepository extends JpaRepository<HeroContent, String> {
}
