package com.javahero.repository;

import com.javahero.model.HeroCard;
import com.javahero.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroCardRepository extends JpaRepository<HeroCard, String> {

    List<HeroCard> findByLearningPathOrderByOrderInPath(LearningPath learningPath);
}
