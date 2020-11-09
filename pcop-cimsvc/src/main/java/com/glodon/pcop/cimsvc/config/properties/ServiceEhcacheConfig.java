package com.glodon.pcop.cimsvc.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceEhcacheConfig {

    @Value("${cim.ehcache.cluster-enable}")
    private Boolean clusterEnable;

    @Value("${cim.ehcache.terracotta-server}")
    private String terracottaServer;

    @Value("${cim.ehcache.server-resources.default}")
    private String defaultResources;

    @Value("${cim.ehcache.server-resources.primary}")
    private String primaryResources;

    public Boolean getClusterEnable() {
        return clusterEnable;
    }

    public void setClusterEnable(Boolean clusterEnable) {
        this.clusterEnable = clusterEnable;
    }

    public String getTerracottaServer() {
        return terracottaServer;
    }

    public void setTerracottaServer(String terracottaServer) {
        this.terracottaServer = terracottaServer;
    }

    public String getDefaultResources() {
        return defaultResources;
    }

    public void setDefaultResources(String defaultResources) {
        this.defaultResources = defaultResources;
    }

    public String getPrimaryResources() {
        return primaryResources;
    }

    public void setPrimaryResources(String primaryResources) {
        this.primaryResources = primaryResources;
    }
}
