package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;

public class InfoDiscoverySpaceTest {

	public static void main(String[] args) {
		System.out.println("start...");
		CimDataEngineComponentFactory.connectInfoDiscoverSpace("remote:10.129.27.104/", CimConstants.defauleSpaceName,
				"root", "wyc");
		System.out.println("end!!!");
	}

}
