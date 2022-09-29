package uy.com.md.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uy.com.md.common.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

	@Autowired
    private HeadersUtil headersUtil;


    @Override
    public void apply(RequestTemplate requestTemplate) {
      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (requestAttributes == null) {
          return;
      }
      HttpServletRequest request = requestAttributes.getRequest();
      String[] headers = headersUtil.getHeadersToTransfer();
        for(String header : headers) {
    	    if(request.getHeader(header) != null) {
    	    	requestTemplate.header(header, request.getHeader(header));
    		  }
        }
    }
}
