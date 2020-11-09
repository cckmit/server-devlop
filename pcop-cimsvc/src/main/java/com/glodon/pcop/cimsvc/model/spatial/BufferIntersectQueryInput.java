package com.glodon.pcop.cimsvc.model.spatial;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "缓冲区包含查询输入")
public class BufferIntersectQueryInput extends BCQueryInput {
    @ApiModelProperty(value = "选定区域的自定义表示")
    private AreaInput customArea;

    public AreaInput getCustomArea() {
        return customArea;
    }

    public void setCustomArea(AreaInput customArea) {
        this.customArea = customArea;
    }

    @ApiModel(value = "查询区域")
    public static class AreaInput {
        @ApiModelProperty(value = "查询区域类型")
        private AreaInputType areaInputType;
        @ApiModelProperty(value = "半径：米")
        private Double radius;
        @ApiModelProperty(value = "点坐标")
        private List<GPoint> gPoints;

        public AreaInputType getAreaInputType() {
            return areaInputType;
        }

        public void setAreaInputType(AreaInputType areaInputType) {
            this.areaInputType = areaInputType;
        }

        public Double getRadius() {
            return radius;
        }

        public void setRadius(Double radius) {
            this.radius = radius;
        }

        public List<GPoint> getgPoints() {
            return gPoints;
        }

        public void setgPoints(List<GPoint> gPoints) {
            this.gPoints = gPoints;
        }

        public enum AreaInputType {
            CIRCLE, POLYGON;
        }

    }

    @ApiModel(value = "经纬度表示的点")
    public static class GPoint {
        @ApiModelProperty(value = "维度")
        private Double lat;
        @ApiModelProperty(value = "经度")
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(lng).append(' ').append(lat);

            return sb.toString();
        }

        /**
         * defalut construct
         */
        public GPoint() {
        }

        public GPoint(Double lng, Double lat) {
            this.lat = lat;
            this.lng = lng;
        }
    }


}
