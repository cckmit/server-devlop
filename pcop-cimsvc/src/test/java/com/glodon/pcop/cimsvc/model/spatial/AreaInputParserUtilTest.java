package com.glodon.pcop.cimsvc.model.spatial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AreaInputParserUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void polygonParser() {
        BufferIntersectQueryInput.GPoint gp1 = new BufferIntersectQueryInput.GPoint(119.95366D, 2.34374D);
        BufferIntersectQueryInput.GPoint gp2 = new BufferIntersectQueryInput.GPoint(119.95366D, 2.34828D);
        BufferIntersectQueryInput.GPoint gp3 = new BufferIntersectQueryInput.GPoint(119.965993D, 2.34828D);
        BufferIntersectQueryInput.GPoint gp4 = new BufferIntersectQueryInput.GPoint(119.965993D, 2.34374D);
        BufferIntersectQueryInput.GPoint gp5 = new BufferIntersectQueryInput.GPoint(119.95366D, 2.34374D);

        List<BufferIntersectQueryInput.GPoint> gPoints = new ArrayList<>();
        gPoints.add(gp1);
        gPoints.add(gp2);
        gPoints.add(gp3);
        gPoints.add(gp4);
        gPoints.add(gp5);

        System.out.println("polygon: " + AreaInputParserUtil.polygonParser(gPoints));
    }

    @Test
    public void polygonParserByStr() {
        String inputStr = "[{\"lng\":116.31342155196835,\"lat\":39.50524294641478},{\"lng\":116.30750610156778," +
                "\"lat\":39.50209490828025},{\"lng\":116.31558447667292,\"lat\":39.501763017426384},{\"lng\":116" +
                ".31558447667292,\"lat\":39.501763017426384},{\"lng\":116.31342155196835,\"lat\":39.50524294641478}]";

        List<BufferIntersectQueryInput.GPoint> gPoints = JSON.parseObject(inputStr,
                new TypeReference<List<BufferIntersectQueryInput.GPoint>>() {
        });

        System.out.println("polygon: " + AreaInputParserUtil.polygonParser(gPoints));
    }

    @Test
    public void circleParser() {
        BufferIntersectQueryInput.GPoint gp = new BufferIntersectQueryInput.GPoint(12.31401499999999949D,
                41.8262816000000015D);
        Double radius = 1000D;

        OrientDB orientDB = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
        try (ODatabaseSession db = orientDB.open("test", "root", "wyc")) {
            System.out.println("circle: " + AreaInputParserUtil.circleParser(db, gp, radius));
        }
    }

}