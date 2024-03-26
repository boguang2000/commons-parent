package cn.aotcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.yulichang.autoconfigure.consumer.MybatisPlusJoinPropertiesConsumer;

@Configuration
public class MybatisPlusJoinAutoConfig {

	@Bean
    public MybatisPlusJoinPropertiesConsumer mybatisPlusJoinPropertiesConsumer() {
        return prop -> prop
                .setBanner(false)
                .setTableAlias("t");
    }
}
