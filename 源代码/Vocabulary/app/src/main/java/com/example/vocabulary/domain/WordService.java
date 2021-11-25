package com.example.vocabulary.domain;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WordService {
    @POST("/api")
    Call<WordInformation> getWordData(@Query("q") String q, @Query("from") String from, @Query("to") String to, @Query("appKey") String appKey, @Query("salt") String salt,
                                      @Query("sign") String sign, @Query("signType") String signType, @Query("curtime") String curtime);
}
