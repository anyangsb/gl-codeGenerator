package com.gl.web.vertx;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gl.web.common.ResultUtils;
import com.gl.web.controller.GeneratorController;
import com.gl.web.manager.CacheManager;
import com.gl.web.model.dto.generator.GeneratorQueryRequest;
import com.gl.web.model.vo.GeneratorVO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

public class MainVerticle extends AbstractVerticle {

    private CacheManager cacheManager;

    public MainVerticle(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @Override
    public void start() throws Exception{
        //Create the HTTP server
        vertx.createHttpServer()
                .requestHandler(req->{
                    HttpMethod httpMethod = req.method();
                    String path = req.path();
                    //分页获取生成器
                    if(HttpMethod.POST.equals(httpMethod)&&"/generator/page".equals(path)){
                        //设置请求体处理器
                        req.handler(buffer -> {
                            //获取请求体中的JSON数据
                            String requestBody = buffer.toString();
                            GeneratorQueryRequest generatorQueryRequest = JSONUtil.toBean(requestBody, GeneratorQueryRequest.class);
                            //处理JSON数据
                            String cacheKey = GeneratorController.getPageCacheKey(generatorQueryRequest);
                            //设置响应头
                            HttpServerResponse response = req.response();
                            response.putHeader("content-type","application/json");
                            //本地缓存
                            Object cacheValue = cacheManager.get(cacheKey);
                            if(cacheValue!=null){
                                //返回JSON响应
                                response.end(JSONUtil.toJsonStr(ResultUtils.success((Page<GeneratorVO>)cacheValue)));
                                return;
                            }
                            response.end("");
                        });
                    }
                })
                .listen(8888)
                .onSuccess(server->{
                    System.out.println("Http server started on port" + server.actualPort());
                });
    }

}
