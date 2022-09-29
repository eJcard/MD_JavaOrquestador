package uy.com.md.sistar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uy.com.md.sistar.dto.in.NewCardRequestDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class SchemaValidationJson {

    private static final Logger logger = LoggerFactory.getLogger(CardCreditService.class);


    public static void validateReqDto(NewCardRequestDto requestDto, String schema) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File ReqDto = new File("ReqDto.json");
        objectMapper.writeValue(ReqDto, requestDto);
        InputStream jsonStream = new FileInputStream(ReqDto);

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        String classpath = System.getProperty("java.class.path");
        try (
                InputStream schemaStream =  (InputStream) Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream(schema);
        ) {
            JsonNode json = objectMapper.readTree(jsonStream);
            JsonSchema jsonSchema = schemaFactory.getSchema(schemaStream);

            Set<ValidationMessage> validationResult  =   ((com.networknt.schema.JsonSchema) jsonSchema).validate(json);
            if ((validationResult).isEmpty()) {
                logger.info(" Sin errores de validacion ");
            } else {
                logger.info(" Errores de validacion ");
                validationResult.forEach(vm -> {
                    String[] campo = vm.getMessage().split(":");
                    String msg = (vm.getCode().equals("1028")) ? "Campo requerido":"No concuerda con valores propuestos en el esquema";
                    throw new SistarbancOperationException("Error en validacion por schema", campo[0].substring(2) +  ": " + msg );
                });
            }
        } catch (IOException e) {
            logger.info("No consigue los archivos" );
            e.printStackTrace();
        }
    }
}
