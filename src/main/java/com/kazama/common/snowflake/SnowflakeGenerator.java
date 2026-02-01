package com.kazama.common.snowflake;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowflakeGenerator {

    private final Snowflake snowflake;

    public SnowflakeGenerator(long workerId , long datacenterId){
        this.snowflake = IdUtil.getSnowflake(workerId , datacenterId);
    }

    public long nextId(){
        return snowflake.nextId();
    }
}
