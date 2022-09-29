package uy.com.md.jcard.config;


import lombok.Data;

@Data
public class JcardRequestException extends Exception {
    private String trace;
    private String success;
    private String errors;
}
