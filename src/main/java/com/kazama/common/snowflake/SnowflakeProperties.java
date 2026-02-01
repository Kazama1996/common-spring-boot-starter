package com.kazama.common.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

@ConfigurationProperties(prefix = "common.snowflake")
public class SnowflakeProperties {


    private Long workerId;

    private Long datacenterId;

    public long getWorkerId(){
        if(workerId!=null) {
            return workerId;
        }

        String envWorkerId = System.getenv("WORKER_ID");
        if(envWorkerId!=null){
            return Long.parseLong(envWorkerId);
        }

        return generateWorkerIdFormIp();

    }

    public long getDatacenterId() {
        if (datacenterId != null) {
            return datacenterId;
        }

        String envDatacenterId = System.getenv("DATACENTER_ID");
        if (envDatacenterId != null) {
            return Long.parseLong(envDatacenterId);
        }

        return 1L; // 最終預設值
    }


    private long generateWorkerIdFormIp(){
        try{
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipBytes = ip.getAddress();
            return (ipBytes[ipBytes.length-1]&0xFF)%32;
        }catch (Exception e){
            return 1L;
        }
    }

    public void setWorkerId(Long workerId){
        this.workerId = workerId;
    }

    public void setDatacenterId(Long datacenterId){
        this.datacenterId = datacenterId;
    }

}
