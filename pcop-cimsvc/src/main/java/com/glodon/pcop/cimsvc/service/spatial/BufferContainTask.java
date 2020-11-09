package com.glodon.pcop.cimsvc.service.spatial;

import com.glodon.pcop.cimsvc.model.spatial.BCQueryInput;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class BufferContainTask implements Callable<String> {

    private static final Logger log = LoggerFactory.getLogger(BufferContainTask.class);

    private List<BCQueryInput.BufferInput> bufferInputs;
    private String wktGeom;
    private String cimId;
    private ODatabasePool pool;

    @Override
    public String call() {
        boolean flag = false;
        try (ODatabaseSession db = pool.acquire()) {
            for (BCQueryInput.BufferInput bufferInput : bufferInputs) {
                flag = filterBybuffer(db, wktGeom, bufferInput);
                if (!flag) {
                    break;
                }
            }
        }
        if (flag) {
            return cimId;
        } else {
            return "";
        }
    }

    /**
     * 单实例，单对象类型缓冲区查询
     *
     * @param db
     * @param wktGeom
     * @param bufferInput
     * @return
     */
    private boolean filterBybuffer(ODatabaseSession db, String wktGeom, BCQueryInput.BufferInput bufferInput) {
        if (bufferInput.getDistance() < 0) {
            log.info("distance less than 0");
            return true;
        }
        String statement = getQueryByBufferSql(wktGeom, bufferInput);

        OResultSet oResultSet = db.query(statement);
        if (oResultSet.hasNext()) {
            oResultSet.close();
            return true;
        } else {
            oResultSet.close();
            return false;
        }
    }

    /**
     * 构造缓冲区查询SQL
     *
     * @param wktGeom
     * @param bufferInput
     * @return
     */
    public String getQueryByBufferSql(String wktGeom, BCQueryInput.BufferInput bufferInput) {
        double distenceByDegree = 0.01D * (bufferInput.getDistance() / 1113D);
        // log.info("distince by degree: {}", distenceByDegree);
        String statement = String.format("SELECT FROM ( SELECT ID, ST_Intersects(ST_Buffer(ST_GeomFromText('%s'), %s), geom) AS GALF FROM %s ) WHERE GALF = true", wktGeom, distenceByDegree, SpatialService.getClassNameByObjectTypeId(bufferInput.getObjectTypeId()));
        log.info("{} query by buffer: {}", Thread.currentThread());
        return statement;
    }

    public BufferContainTask(List<BCQueryInput.BufferInput> bufferInputs, String wktGeom, String cimId, ODatabasePool pool) {
        this.bufferInputs = bufferInputs;
        this.wktGeom = wktGeom;
        this.cimId = cimId;
        this.pool = pool;
    }
}
