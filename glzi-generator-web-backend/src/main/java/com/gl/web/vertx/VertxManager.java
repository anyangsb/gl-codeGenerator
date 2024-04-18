package com.gl.web.vertx;

import com.gl.web.manager.CacheManager;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class VertxManager {

    @Resource
    private CacheManager cacheManager;

    @PostConstruct
    public void init(){
        Vertx vertx = Vertx.vertx();
        MainVerticle mainVerticle = new MainVerticle(cacheManager);
        vertx.deployVerticle(mainVerticle);
    }
}
