package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefsDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.InfoObjectTypeDetailInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeCopyInputBean;
import com.glodon.pcop.cimsvc.model.vo.InfoObjectTypeDetailVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangd-a
 * 对象类型树展示
 */

@Service
public class InfoObjectTypeManagementService {
	public List<InfoObjectTypeDetailVO> queryInfoObjectTypeDetails(String tenantId, String userId, InfoObjectTypeDetailInputBean inputBean) {

		String infoObjectTypeName = inputBean.getInfoObjectTypeName();
		String infoObjectTypeDesc = inputBean.getInfoObjectTypeDesc();
		List<InfoObjectTypeDetailVO> infoObjectTypeDetailVOList = new ArrayList<>();

		CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
		try {
			CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, null);
			targetCIMModelCore.setCimDataSpace(cimDataSpace);
			InfoObjectDefsDSImpl infoObjectDefsDSImpl = (InfoObjectDefsDSImpl) targetCIMModelCore.getInfoObjectDefs();

			ExploreParameters exploreParameters = new ExploreParameters();
			if (infoObjectTypeName != null) {
				exploreParameters.setDefaultFilteringItem(new SimilarFilteringItem(
						BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_NAME, infoObjectTypeName, SimilarFilteringItem.MatchingType.Contain, false));
				if (infoObjectTypeDesc != null) {
					exploreParameters.addFilteringItem(new SimilarFilteringItem(
							BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC, infoObjectTypeDesc, SimilarFilteringItem.MatchingType.Contain, false), ExploreParameters.FilteringLogic.AND);
				}
			} else {
				if (infoObjectTypeDesc != null) {
					exploreParameters.setDefaultFilteringItem(new SimilarFilteringItem(
							BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC, infoObjectTypeDesc, SimilarFilteringItem.MatchingType.Contain, false));
				}
			}
			List<InfoObjectDef> matchedInfoObjectDefList = infoObjectDefsDSImpl.queryAllInfoObjectDefsByNoTenantId(exploreParameters);
			for (InfoObjectDef currentInfoObjectDef : matchedInfoObjectDefList) {
				boolean isValidType = true;
				String statusFactRid = ((InfoObjectDefDSImpl) currentInfoObjectDef).getInfoObjectTypeStatusFactRid();
				if (tenantId != null) {
					isValidType = CommonOperationUtil.isTenantContainsData(tenantId, cimDataSpace.getFactById(statusFactRid));
				}
				if (isValidType) {
					InfoObjectTypeDetailVO infoObjectTypeDetailVO = new InfoObjectTypeDetailVO();
					infoObjectTypeDetailVO.setCreateDateTime(((InfoObjectDefDSImpl) currentInfoObjectDef).getCreateDateTime());
					infoObjectTypeDetailVO.setUpdateDateTime(((InfoObjectDefDSImpl) currentInfoObjectDef).getUpdateDateTime());
					infoObjectTypeDetailVO.setObjectTypeDesc(currentInfoObjectDef.getObjectTypeDesc());
					infoObjectTypeDetailVO.setObjectTypeName(currentInfoObjectDef.getObjectTypeName());
					infoObjectTypeDetailVO.setTenantId(getInstanceBelongedTenantsInfo(cimDataSpace, statusFactRid));
					infoObjectTypeDetailVOList.add(infoObjectTypeDetailVO);
				}
			}
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			cimDataSpace.closeSpace();
		}
		return infoObjectTypeDetailVOList;
	}

	public static String getInstanceBelongedTenantsInfo(CimDataSpace cimDataSpace, String instanceRID) {
		String belongedTenants = "无所属租户信息";
		Fact datasetDefFact = null;
		if (instanceRID != null) {
			try {
				datasetDefFact = cimDataSpace.getFactById(instanceRID);
				List<String> belongedTenantsList = CommonOperationUtil.getBelongedTenants(datasetDefFact);
				if (belongedTenantsList.size() > 0) {
					belongedTenants = formatBelongedTenantsInfo(belongedTenantsList);
				}
			} catch (CimDataEngineRuntimeException e) {
				e.printStackTrace();
			}
		}
		return belongedTenants;
	}

	private static String formatBelongedTenantsInfo(List<String> belongedTenantsList) {
		StringBuffer tenantsInfoString = new StringBuffer();
		for (int i = 0; i < belongedTenantsList.size(); i++) {
			String currentTenant = belongedTenantsList.get(i);
			if (currentTenant.equals(BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME)) {
				tenantsInfoString.append("全局租户共享");
			} else {
				tenantsInfoString.append(currentTenant);
			}
			if (i < belongedTenantsList.size() - 1) {
				tenantsInfoString.append(" , ");
			}
		}
		return tenantsInfoString.toString();
	}
}
