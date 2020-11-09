package com.glodon.pcop.cimsvc.model.spatial;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AreaInputParserUtil {
    private static Logger log = LoggerFactory.getLogger(AreaInputParserUtil.class);

    public static String polygonParser(List<BufferIntersectQueryInput.GPoint> gPoints) {
        StringBuffer sb = new StringBuffer();
        if (gPoints == null || gPoints.size() < 1) {
            log.error("error input area input");
            return sb.toString();
        }

        sb.append("POLYGON ((");
        boolean ff = true;
        for (BufferIntersectQueryInput.GPoint gp : gPoints) {
            if (ff) {
                sb.append(gp.toString());
                ff = false;
            } else {
                sb.append(", ").append(gp.toString());
            }
        }
        sb.append("))");

        return sb.toString();
    }

    public static String circleParser(ODatabaseSession db, BufferIntersectQueryInput.GPoint center, Double radius) {
        String wktCircle = "";
        if (center != null && radius > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ST_AsText(ST_Buffer(ST_GeomFromText('POINT(").append(center.toString()).append(")'),").append(0.01D * (radius / 1113D)).append(")) as wkt");
            OResultSet resultSet = db.query(sb.toString());
            if (resultSet.hasNext()) {
                OResult ru = resultSet.next();
                wktCircle = ru.getProperty("wkt");
            }
        }

        return wktCircle;
    }

    public static String getWktArea(ODatabaseSession db, BufferIntersectQueryInput.AreaInput customArea) {
        switch (customArea.getAreaInputType()) {
            case CIRCLE:
                return circleParser(db, customArea.getgPoints().get(0), customArea.getRadius());
            case POLYGON:
                return polygonParser(customArea.getgPoints());
            default:
                log.error("not support polygon type: {}", customArea.getAreaInputType());
                return "";
        }
    }

}
