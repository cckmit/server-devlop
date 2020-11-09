package com.glodon.pcop.cimsvc.service.stat;


import com.glodon.pcop.cimsvc.repository.BusinessStatRepository;

import com.glodon.pcop.cimsvc.repository.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class BusinessStatService {


	@Autowired
	private BusinessStatRepository businessStatRepository;


	public List<MatchResult> multiObjectGeneralGroupQueryMatch(String match, String groupByName,String containName){
		List<MatchResult> matchResults = businessStatRepository.multiObjectGeneralGroupQueryMatch(match, groupByName, containName);
		return matchResults;
	}
	


}
