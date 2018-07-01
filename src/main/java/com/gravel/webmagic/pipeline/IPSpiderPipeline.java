package com.gravel.webmagic.pipeline;

import com.gravel.domain.Movies;
import com.gravel.domain.MoviesMapper;
import com.gravel.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

/**
 * Created by gravel on 2018/04/13.
 */
@Component("IPSpiderPipeline")
public class IPSpiderPipeline implements Pipeline {

    @Autowired
    MoviesMapper moviesMapper;

    @Autowired
    private RedisService redisService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        for(Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if (entry.getKey().equals("result")) {
                Movies movies = (Movies)entry.getValue();
                    moviesMapper.insert(movies);
            }
        }
    }
}
