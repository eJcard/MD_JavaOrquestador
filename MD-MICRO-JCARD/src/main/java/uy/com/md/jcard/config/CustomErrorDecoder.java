package uy.com.md.jcard.config;

import com.google.gson.Gson;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.com.md.jcard.service.impl.JcardServiceImpl;

import java.io.IOException;

public class CustomErrorDecoder implements ErrorDecoder {

    private static Logger log = LoggerFactory.getLogger(JcardServiceImpl.class);

    Gson gson = new Gson();

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            String responseBody = new String(bodyData);
            JcardRequestException jcardRequestException = gson.fromJson(responseBody, JcardRequestException.class);
            if (jcardRequestException == null || jcardRequestException.getErrors() == null) {
                throw new Exception();
            }
            return jcardRequestException;
        } catch (Exception e) {
            log.error("CustomErrorDecoder.decode", e);
            throw new Exception();
        }
    }
}
