package com.glodon.pcop.cimsvc.dataimport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanjk
 *
 */
public class CreateFilePraser {

	private String filePath;

	public CreateFilePraser(String filePath) {
		this.filePath = filePath;
	}

	public Map<String, String> getStruct() {

		
		return new HashMap<>();
	}

	public long GetRecordCount() {

		return 0;
	}
	
	
	public Map<String, Object> getNextRow() {

		return new HashMap<>();
	}
}
