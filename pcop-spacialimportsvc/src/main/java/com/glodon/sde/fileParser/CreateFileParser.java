package com.glodon.sde.fileParser;

import java.util.Map;

public class CreateFileParser {
	public native boolean Load(String filePath);

	public native Map<String, String> getStruct();

	public native long getRecordCount();

	public native Map<String, Object> getNextRow();
}
