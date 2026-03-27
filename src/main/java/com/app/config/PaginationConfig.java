package com.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PaginationConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(java.util.List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomPageableHandler());
    }

    private static class CustomPageableHandler extends PageableHandlerMethodArgumentResolver {
        @Override
        public Pageable resolvePageArgument(org.springframework.core.MethodParameter parameter, 
                                         org.springframework.web.method.HandlerMethodArgumentResolver context) {
            Pageable pageable = super.resolvePageArgument(parameter, context);
            if (pageable != null && pageable.getPageSize() > 100) {
                return PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort());
            }
            return pageable;
        }
    }
}
