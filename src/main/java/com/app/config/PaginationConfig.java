package com.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PaginationConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(java.util.List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomPageableHandler());
    }

    private static class CustomPageableHandler extends PageableHandlerMethodArgumentResolver {
        @Override
        public Pageable resolveArgument(MethodParameter methodParameter, 
                                     ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest,
                                     WebDataBinderFactory binderFactory) {
            Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
            if (pageable != null && pageable.getPageSize() > 100) {
                return PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort());
            }
            return pageable;
        }
    }
}
