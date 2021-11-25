package com.example.vocabulary.domain;

import java.io.Serializable;

public class Word implements Serializable {
    private String name;
    private String meaning;
    private String sentence;

    public Word() {

    }

    public Word(String name, String meaning) {
        this.name = name;
        this.meaning = meaning;
    }

    public Word(String name, String meaning, String sentence) {
        this(name, meaning);
        this.sentence = sentence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
