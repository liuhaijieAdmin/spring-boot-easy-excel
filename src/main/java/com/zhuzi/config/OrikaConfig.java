package com.zhuzi.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaConfig {

    @Bean
    public MapperFactory mapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

    @Bean
    public ma.glasnost.orika.MapperFacade mapperFacade() {
        return mapperFactory().getMapperFacade();
    }

}
