package com.glodon.pcop.cimsvc.controller.sso;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.glodon.pcop.cimsvc.service.sso.ISsoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yujunwei
 * @datetime 2020-06-15 09:50:45
 */
//@CrossOrigin
@RestController
public class SsoApiController {

    @Autowired
    private ISsoApiService ssoApiService;

    @GetMapping("/codes")
    public ResponseEntity<String> getCode(@RequestParam String code) {
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @GetMapping("/reqcodes")
    public ResponseEntity<String> reqCodes(@RequestParam String username, @RequestParam String password)
        throws UnsupportedEncodingException {
        return ssoApiService.reqCodes(username, password);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/usertokens/{code}")
    public ResponseEntity<Map> getToken(@PathVariable String code) throws UnsupportedEncodingException {
        return ssoApiService.getToken(code);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/tokens/{token}")
    public ResponseEntity<Map> checkToken(@PathVariable String token) {
        return ssoApiService.checkToken(token);
    }

    @DeleteMapping("/tokens/{token}")
    public ResponseEntity<String> deleteToken(@PathVariable String token) throws UnsupportedEncodingException {
        return ssoApiService.deleteToken(token);
    }

}