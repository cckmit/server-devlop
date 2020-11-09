package com.glodon.pcop.cimsvc.service.v2.engine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class IndustryTypeDefServiceTest {

    private static IndustryTypeDefService industryTypeService;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String tenantId;
    private static String industryTypeId;

    @BeforeClass
    public static void setUp() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
        tenantId = "1";
        industryTypeService = new IndustryTypeDefService();
        InfoObjectTypeDefService objectTypeDefService = new InfoObjectTypeDefService();
        // industryTypeService.objectTypeDefService = objectTypeDefService;

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                jsonGenerator.writeString("");
            }
        });
    }

    // @Test
    public void addIndustryType() throws DataServiceModelRuntimeException {
        IndustryTypeVO industryTypeVO = new IndustryTypeVO();
        industryTypeVO.setCreatorId(tenantId);
        industryTypeVO.setTenantId(tenantId);
        industryTypeVO.setIndustryTypeName("测试-新增行业分类a");
        industryTypeVO.setIndustryTypeDesc("测试-新增行业分类a");

        industryTypeVO = industryTypeService.addIndustryType(tenantId, industryTypeVO);
        industryTypeId = industryTypeVO.getIndustryTypeId();
    }

    // @Test
    public void removeIndustryType() throws DataServiceModelRuntimeException {
        industryTypeService.removeIndustryType(tenantId, industryTypeId);
    }

    // @Test
    public void updateIndustryType() throws DataServiceModelRuntimeException, EntityNotFoundException {
        IndustryTypeVO industryTypeVO = new IndustryTypeVO();
        industryTypeVO.setCreatorId(tenantId);
        industryTypeVO.setTenantId(tenantId);
        industryTypeVO.setIndustryTypeName("测试-gengxin行业分类a");
        industryTypeVO.setIndustryTypeDesc("测试-gengxin行业分类a");

        industryTypeService.updateIndustryType(tenantId, industryTypeId, industryTypeVO);
    }

    @Test
    public void getIndustryType() {

    }

    // @Test
    public void getAllChildIndustryTypesAndLinkedObjectTypes() throws DataServiceModelRuntimeException, EntityNotFoundException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(industryTypeService.getAllChildIndustryTypesAndLinkedObjectTypes(tenantId, "#34:0")));
    }
}