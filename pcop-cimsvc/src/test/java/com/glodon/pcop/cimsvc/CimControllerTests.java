package com.glodon.pcop.cimsvc;

import junit.framework.TestCase;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.web.client.TestRestTemplate;


/**
 * @author Jimmy.Liu(liuzm@glodon.com), Jul/07/2018.
 */
// @RunWith(SpringRunner.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CimControllerTests {

	// @Autowired
	private TestRestTemplate testRestTemplate;

	// @LocalServerPort
	private int port;

	// @Test
	public void testAboutMe() throws MalformedURLException {
		URL baseUrl = new URL("http://localhost:" + Integer.toString(port) + "/");

		String msg = testRestTemplate.getForObject(baseUrl.toString() + "hi", String.class);
		String info = "规建管一体化平台CIM数据服务 已启动。";
		TestCase.assertEquals(info, msg);
	}

	/*
	@Test
	public void post() throws Exception {
		HttpHeaders headers = new HttpHeaders();
        headers.set("token","xxxxx");
		MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("username","lake");
        HttpEntity formEntity = new HttpEntity(multiValueMap,headers);
        ResponseEntity<ActResult> result = testRestTemplate.exchange("/test/putHeader", HttpMethod.PUT,formEntity,ActResult.class);
        Assert.assertEquals(result.getBody().getCode(),0);
	}
	*/

}
