package com.glodon.pcop.cimsvc.service.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.service.v2.engine.IndustryTypeDefService;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RelationshipsServiceTest {

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


    @Test
    public void jsonOrder() throws JsonProcessingException {
        RelationshipEntity relationshipEntity = new RelationshipEntity();

        System.out.println(objectMapper.writeValueAsString(relationshipEntity));

        ObjectTypeEntity objectTypeEntity = new ObjectTypeEntity();

        System.out.println(objectMapper.writeValueAsString(objectTypeEntity));

    }

}