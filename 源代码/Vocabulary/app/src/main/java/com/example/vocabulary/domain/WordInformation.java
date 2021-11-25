package com.example.vocabulary.domain;

import java.util.List;

public class WordInformation {
    private String errorCode;
    private String query;
    private List<String> translation;
    private Basic basic;
    private List<SimpleWord> web;
    private Dict dict;
    private WebDict webdict;
    private String l;
    private String tSpeakUrl;
    private String speakUrl;

    public static class Basic {
        private String phonetic;
        private String uk_phonetic;
        private String us_phonetic;
        private String uk_speech;
        private String us_speech;
        private List<String> explains;

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUk_phonetic() {
            return uk_phonetic;
        }

        public void setUk_phonetic(String uk_phonetic) {
            this.uk_phonetic = uk_phonetic;
        }

        public String getUs_phonetic() {
            return us_phonetic;
        }

        public void setUs_phonetic(String us_phonetic) {
            this.us_phonetic = us_phonetic;
        }

        public String getUk_speech() {
            return uk_speech;
        }

        public void setUk_speech(String uk_speech) {
            this.uk_speech = uk_speech;
        }

        public String getUs_speech() {
            return us_speech;
        }

        public void setUs_speech(String us_speech) {
            this.us_speech = us_speech;
        }

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class SimpleWord {
        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }

    public static class Dict {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class WebDict {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public List<SimpleWord> getWeb() {
        return web;
    }

    public void setWeb(List<SimpleWord> web) {
        this.web = web;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

    public WebDict getWebdict() {
        return webdict;
    }

    public void setWebdict(WebDict webdict) {
        this.webdict = webdict;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String gettSpeakUrl() {
        return tSpeakUrl;
    }

    public void settSpeakUrl(String tSpeakUrl) {
        this.tSpeakUrl = tSpeakUrl;
    }

    public String getSpeakUrl() {
        return speakUrl;
    }

    public void setSpeakUrl(String speakUrl) {
        this.speakUrl = speakUrl;
    }
}
