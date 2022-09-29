package uy.com.md.global.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uy.com.md.common.util.DateUtil;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper model = new ModelMapper();
        model.addConverter(DateUtil.getLocalDateTimeToGregorian());
        model.addConverter(DateUtil.getLocalDateToGregorian());
        model.addConverter(DateUtil.getStringToGregorian());
        return model;
    }
}
