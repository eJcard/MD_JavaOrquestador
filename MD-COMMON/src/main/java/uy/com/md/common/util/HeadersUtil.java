package uy.com.md.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class HeadersUtil {

	@Autowired
    private Environment env;

	
	public String[] getHeadersToTransfer() {
        String[] headers = env.getProperty("md.headers.to.transfer", "Authorization,pdp-uuid,activityid,md-uact,md-consumer-app,md-origen,amd-clientapplication,amd-clientplatform,amd-clientdevice,amd-clientip").split(",");
        return headers;
	}
	
}
