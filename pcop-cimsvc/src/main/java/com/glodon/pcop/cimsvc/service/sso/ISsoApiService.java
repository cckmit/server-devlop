package com.glodon.pcop.cimsvc.service.sso;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

/**
 * @author yujunwei
 * @datetime 2020-06-15 09:50:05
 */
public interface ISsoApiService {

    ResponseEntity<String> reqCodes(String username, String password) throws UnsupportedEncodingException;

    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> getToken(String code) throws UnsupportedEncodingException;

    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> checkToken(String token);

    ResponseEntity<String> deleteToken(String token) throws UnsupportedEncodingException;

}