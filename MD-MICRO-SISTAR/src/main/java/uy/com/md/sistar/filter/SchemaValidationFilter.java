package uy.com.md.sistar.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uy.com.md.sistar.service.SistarbancOperationException;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

public class SchemaValidationFilter extends OncePerRequestFilter {

    public static class CachedRequest extends HttpServletRequestWrapper {
        private final byte[] cached;

        public CachedRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cached = StreamUtils.copyToByteArray(request.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream()  {
            return new CachedInputStream(this.cached);
        }
    }

    public static class CachedInputStream extends ServletInputStream {

        private final InputStream cached;

        CachedInputStream(byte[] cache) {
            this.cached = new ByteArrayInputStream(cache);
        }
        @Override
        public boolean isFinished() {
            try {
                return this.cached.available() == 0;
            } catch (IOException ignore) {}

            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.cached.read();
        }
    }

    protected JsonSchema getJsonSchemaFromClasspath(String name) {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(name);
            return factory.getSchema(is);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        CachedRequest cachedRequest = new CachedRequest(httpServletRequest);

        String uri = cachedRequest.getRequestURI();
        String method = cachedRequest.getMethod();
        String operationId = method.toLowerCase() +  uri.replace("/", "_");
        String fileName = operationId + ".json"; //eg: post_sistar_card.json
        System.out.println("file " + fileName);
                JsonSchema jsonSchema = getJsonSchemaFromClasspath(fileName);

        if(jsonSchema == null){
            filterChain.doFilter(cachedRequest, httpServletResponse);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(cachedRequest.getInputStream());

        Set<ValidationMessage> errors = jsonSchema.validate(json);
        if(errors.isEmpty()){
            filterChain.doFilter(cachedRequest, httpServletResponse);
        }else{
            Optional<ValidationMessage> error = errors.stream().findFirst();
            errors.forEach(vm -> {
                String[] campo = vm.getMessage().split(":");
                String msg = (vm.getCode().equals("1028")) ? campo[0].substring(2) + " es requerido" :  vm.getPath().substring(2) +  " " + vm.getMessage();
                throw new SistarbancOperationException("El campo " + msg);
            });
        }
    }
}