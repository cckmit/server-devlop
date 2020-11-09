package com.glodon.pcop.cim.common.model.gis;

import java.util.List;

public class GisSpatialQueryResponseBean {
    private Long count;
    private List<GisResponseFeature> features;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<GisResponseFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GisResponseFeature> Features) {
        this.features = Features;
    }

    @Override
    public String toString() {
        return "GisSpatialQueryResponseBean{" +
                "count=" + count +
                ", features=" + features +
                '}';
    }
}

