package uy.com.md.sistar.dto.in;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

   private String success;

   private String realAccount;

   private String trace;

   private String errors;

}
