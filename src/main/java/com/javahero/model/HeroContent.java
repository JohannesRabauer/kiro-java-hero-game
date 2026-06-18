package com.javahero.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HeroContent {

    @Id
    private String heroId;

    @Column(length = 2000)
    private String whatItIs;

    @Column(length = 2000)
    private String whyItMatters;

    @Column(length = 2000)
    private String howToUseIt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeExample> codeExamples = new ArrayList<>();

    private String animationUrl;

    public HeroContent() {
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getWhatItIs() {
        return whatItIs;
    }

    public void setWhatItIs(String whatItIs) {
        this.whatItIs = whatItIs;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public String getHowToUseIt() {
        return howToUseIt;
    }

    public void setHowToUseIt(String howToUseIt) {
        this.howToUseIt = howToUseIt;
    }

    public List<CodeExample> getCodeExamples() {
        return codeExamples;
    }

    public void setCodeExamples(List<CodeExample> codeExamples) {
        this.codeExamples = codeExamples;
    }

    public String getAnimationUrl() {
        return animationUrl;
    }

    public void setAnimationUrl(String animationUrl) {
        this.animationUrl = animationUrl;
    }
}
