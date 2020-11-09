package com.glodon.pcop.cimsvc.service.sso;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author yujunwei
 * @datetime 2020-06-15 09:50:15
 */
@Service
public class SsoApiServiceImpl implements ISsoApiService {

    @Value("${sso.auth.clientId}")
    private String clientId;

    @Value("${sso.auth.clientSecret}")
    private String clientSecret;

    @Value("${sso.auth.redirectUri}")
    private String redirectUri;

    @Value("${sso.auth.oauthAuthorize}")
    private String oauthAuthorize;

    @Value("${sso.auth.accessTokenUri}")
    private String accessTokenUri;

    @Value("${sso.auth.checkTokenUri}")
    private String checkTokenUri;

    @Value("${sso.auth.userInfoUri}")
    private String userInfoUri;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> reqCodes(String username, String password) throws UnsupportedEncodingException {
        // 设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        String authorization = String.format("%s %s", "Basic",
            Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes("UTF-8")));
        httpHeaders.add("Authorization", authorization);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            oauthAuthorize + "?response_type=code&client_id=" + clientId, HttpMethod.GET, httpEntity, String.class);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public ResponseEntity<Map> getToken(String code) throws UnsupportedEncodingException {
        // 获取token
        Map tokenInfoMap = getTokenInfo(code);
        // 获取用户信息
        Map userInfoMap = getUserInfo((String)tokenInfoMap.get("access_token"));
        Map resultMap = new HashMap();
        resultMap.put("tokenInfo", tokenInfoMap);
        resultMap.put("userInfo", userInfoMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Map> checkToken(String token) {
        ResponseEntity<Map> responseEntity =
            restTemplate.exchange(checkTokenUri + "?token=" + token, HttpMethod.GET, HttpEntity.EMPTY, Map.class);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteToken(String token) throws UnsupportedEncodingException {
        // 设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        String authorization = String.format("%s %s", "Basic",
            Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes("UTF-8")));
        httpHeaders.add("Authorization", authorization);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("token", token);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(param, httpHeaders);
        ResponseEntity<String> responseEntity =
            restTemplate.exchange(accessTokenUri, HttpMethod.DELETE, httpEntity, String.class);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    /**
     * 获取令牌
     * 
     * @param code
     *            授权码
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("rawtypes")
    public Map getTokenInfo(String code) throws UnsupportedEncodingException {
        // 设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        String authorization = String.format("%s %s", "Basic",
            Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes("UTF-8")));
        httpHeaders.add("Authorization", authorization);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("code", code);
        param.add("redirect_uri", redirectUri);
        param.add("scope", "all");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(param, httpHeaders);
        ResponseEntity<Map> responseEntity =
            restTemplate.exchange(accessTokenUri, HttpMethod.POST, httpEntity, Map.class);
        return responseEntity.getBody();
    }

    /**
     * 获取用户信息
     * 
     * @param accessToken
     *            访问令牌
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map getUserInfo(String accessToken) {
        ResponseEntity<Map> responseEntity = restTemplate.exchange(userInfoUri + "?access_token=" + accessToken,
            HttpMethod.GET, HttpEntity.EMPTY, Map.class);
        Map responseMap = responseEntity.getBody();
        String name = (String)responseMap.get("name");
        responseMap.put("name", name.substring(0, name.indexOf("@")));
        return responseEntity.getBody();
    }

}