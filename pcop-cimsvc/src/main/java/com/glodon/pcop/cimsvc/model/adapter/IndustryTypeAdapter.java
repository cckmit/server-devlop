package com.glodon.pcop.cimsvc.model.adapter;

import java.util.List;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cimsvc.model.IndustryTypeBean;
import com.glodon.pcop.cimsvc.model.IndustryTypeTreeBean;
import com.glodon.pcop.cimsvc.model.ObjectTypeBean;

public class IndustryTypeAdapter {

	public static IndustryTypeVO industryAdapter(IndustryTypeBean itb, String creatorId) {
		IndustryTypeVO itvo = new IndustryTypeVO();
		itvo.setCreatorId(creatorId);
		itvo.setIndustryTypeDesc(itb.getTypeName());
		itvo.setIndustryTypeName(itb.getTypeName());
		return itvo;
	}

	public static IndustryTypeBean industryAdapter(IndustryTypeVO itvo, String parentId) {
		IndustryTypeBean itb = new IndustryTypeBean();
		itb.setTypeId(itvo.getIndustryTypeId());
		itb.setTypeName(itvo.getIndustryTypeName());
		itb.setParentTypeId(parentId);
		itb.setCreateDateTime(itvo.getCreateDateTime());
		itb.setUpdateDateTime(itvo.getUpdateDateTime());
		return itb;
	}

	public static IndustryTypeTreeBean industryTreeAdapter(IndustryTypeVO itvo) {
		if (itvo == null) {
			return null;
		}
		IndustryTypeBean itb = industryAdapter(itvo, "");
		IndustryTypeTreeBean ittb = new IndustryTypeTreeBean(itb);
		List<IndustryTypeVO> itvos = itvo.getChildrenIndustryTypes();
		if (itvos != null) {
			for (IndustryTypeVO element : itvos) {
				ittb.childIndustryList.add(industryTreeAdapter(element));
			}
		}
		if (itvo.getLinkedInfoObjectTypes() != null) {
			for (InfoObjectTypeVO element : itvo.getLinkedInfoObjectTypes()) {
				// 过滤掉已删除的对象模型
				if (!element.isDisabled()) {
					ObjectTypeBean otb = ObjectTypeAdapter.objectTypeAdapterWithoutProperty(element, itvo.getIndustryTypeId());
					ittb.objectList.add(otb);
				}
			}
		}

		return ittb;
	}

}
