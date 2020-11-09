package com.glodon.pcop.cimsvc.service.client;

import com.glodon.pcop.cimapi.common.ReturnInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("pcop-spacialimportsvc")
public interface SpacialServiceInterface {

    @RequestMapping(value = "/fileParserShp/{objectName}", method = RequestMethod.GET)
    ReturnInfo fileParserShp(@PathVariable("objectName") String objectName);

}
