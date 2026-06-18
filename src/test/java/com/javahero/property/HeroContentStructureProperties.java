package com.javahero.property;

import com.javahero.model.HeroContent;
import com.javahero.repository.HeroContentRepository;
import jakarta.transaction.Transactional;
import net.jqwik.api.*;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * Property 6: Hero content structure validity
 *
 * For any hero card in the system, its content has:
 * 1. Non-empty whatItIs text
 * 2. Non-empty whyItMatters text
 * 3. Non-empty howToUseIt text
 * 4. At least one CodeExample in the codeExamples list
 *
 * Validates: Requirements 4.2, 4.3
 */
@JqwikSpringSupport
@SpringBootTest
@Transactional
class HeroContentStructureProperties {

    private static final List<String> ALL_HERO_IDS = List.of(
            "bronze-variable", "bronze-string", "bronze-number", "bronze-boolean",
            "bronze-if", "bronze-loop", "bronze-method",
            "silver-class", "silver-object", "silver-constructor",
            "silver-collection", "silver-exception",
            "gold-interface", "gold-generic", "gold-stream",
            "gold-lambda", "gold-optional", "gold-record",
            "spring-controller", "spring-service", "spring-repository",
            "spring-bean", "spring-di"
    );

    @Autowired
    private HeroContentRepository heroContentRepository;

    /**
     * **Validates: Requirements 4.2, 4.3**
     *
     * Property: For any hero card in the system, its content has non-empty
     * whatItIs, whyItMatters, howToUseIt, and at least one code example.
     */
    @Property(tries = 100)
    void everyHeroCardHasCompleteContent(@ForAll("heroCardIds") String heroId) {
        Optional<HeroContent> optContent = heroContentRepository.findById(heroId);

        assert optContent.isPresent() :
                "Hero content not found for heroId: " + heroId;

        HeroContent content = optContent.get();

        assert content.getWhatItIs() != null && !content.getWhatItIs().isBlank() :
                "Hero " + heroId + " has empty or null 'whatItIs'";

        assert content.getWhyItMatters() != null && !content.getWhyItMatters().isBlank() :
                "Hero " + heroId + " has empty or null 'whyItMatters'";

        assert content.getHowToUseIt() != null && !content.getHowToUseIt().isBlank() :
                "Hero " + heroId + " has empty or null 'howToUseIt'";

        assert content.getCodeExamples() != null && !content.getCodeExamples().isEmpty() :
                "Hero " + heroId + " has no code examples";
    }

    @Provide
    Arbitrary<String> heroCardIds() {
        return Arbitraries.of(ALL_HERO_IDS);
    }
}
