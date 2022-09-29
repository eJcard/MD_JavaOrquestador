package uy.com.md.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import uy.com.md.common.dto.ErrorDto;
import uy.com.md.common.dto.ResponseBaseDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static uy.com.md.common.util.ConstantesMensajes.*;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    /*@ExceptionHandler(Exception.class)
    public final ResponseEntity<ResponseBaseDto> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(INTERNAL_SERVER_ERROR_DESCRIPTION);
        //TODO Validar que no sea necesario devolver para no retornar excepciones java internas.
        /*ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorDescription(ex.getLocalizedMessage());
        errorDto.setErrorType(ex.getClass().getName());
        response.getErrors().add(errorDto);*/
       /* return new ResponseEntity<ResponseBaseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }*/


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public final ResponseEntity<ResponseBaseDto> onConstraintValidationException(ConstraintViolationException e, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(HttpStatus.BAD_REQUEST.getReasonPhrase());
        for (ConstraintViolation violation: e.getConstraintViolations()) {
            response.getErrors().add(new ErrorDto(violation.getPropertyPath().toString(), violation.getInvalidValue().toString(), violation.getMessage()));
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

	/*@ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ResponseBaseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(HttpStatus.BAD_REQUEST.getReasonPhrase());
        if(ex.getBindingResult().hasFieldErrors()){
            ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
                response.getErrors().add(new ErrorDto(fieldError.getField() + " " + fieldError.getDefaultMessage(), fieldError.getField(), fieldError.getCode()));
            });
        } else{
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                ErrorDto errorDto = new ErrorDto();
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    response.getErrors().add(new ErrorDto(fieldError.getField() + " " + fieldError.getDefaultMessage(), fieldError.getField(), fieldError.getCode()));
                }
                errorDto.setErrorDescription(error.getDefaultMessage());
                response.getErrors().add(errorDto);
            });
        }
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
    @ExceptionHandler({ BadRequestException.class , JsonMappingException.class, InvalidDefinitionException.class, UnrecognizedPropertyException.class, InvalidFormatException.class})
    public final ResponseEntity<ResponseBaseDto> handleAuthenticationException(BadRequestException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(HttpStatus.BAD_REQUEST.getReasonPhrase());
		ErrorDto errorDto = new ErrorDto();
		errorDto.setErrorDescription(ex.getField() + " " + ex.getMessage());
		errorDto.setErrorOn(ex.getField());
		errorDto.setErrorType(ex.getType());
		response.getErrors().add(errorDto);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({ HttpMessageNotReadableException.class})
    public final ResponseEntity<ResponseBaseDto> handleAuthenticationException(HttpMessageNotReadableException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(HttpStatus.BAD_REQUEST.getReasonPhrase());
		ErrorDto errorDto = new ErrorDto();		
		JsonMappingException jme = (JsonMappingException) ex.getCause();
		errorDto.setErrorOn(jme.getPath().get(0).getFieldName());
		errorDto.setErrorDescription(jme.getPath().getClass().getCanonicalName());
		response.getErrors().add(errorDto);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

	
    @ExceptionHandler({ TokenNotValidException.class })
    public final ResponseEntity<ResponseBaseDto> handleAuthenticationException(TokenNotValidException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(TOKEN_NOT_VALID_DESCRIPTION);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler({ UserNotFoundException.class})
    public final ResponseEntity<ResponseBaseDto> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(Strings.isBlank(ex.getMessage()) ? USER_NOT_FOUND_DESCRIPTION : ex.getMessage());	 	
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler({ IllegalArgumentException.class})
    public final ResponseEntity<ResponseBaseDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ResponseBaseDto response = new ResponseBaseDto();
        response.setErrors(new ArrayList<>());
        response.setExito(false);
        response.setMensaje(Strings.isBlank(ex.getMessage()) ? ILEGAL_ARGUMENT_EXCEPTION_DESCRIPTION : ex.getMessage());	 	
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}