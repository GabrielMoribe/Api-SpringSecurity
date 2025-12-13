package com.example.SpringSecurity.PostgreSQL.exceptions;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions.*;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.*;
import com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions.HealthPlanNotFoundException;
import com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions.HealthPlanRetrievalException;
import com.example.SpringSecurity.PostgreSQL.exceptions.quotationExceptions.*;
import com.example.SpringSecurity.PostgreSQL.exceptions.refreshTokenExceptions.InvalidRefreshTokenException;
import com.example.SpringSecurity.PostgreSQL.exceptions.refreshTokenExceptions.RefreshTokenNotFoundException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    //==================EXCEPTIONS DE AUTENTICACAO E REGISTRO DE USUARIO==================

    //Email ja cadastrado ao tentar registrar novo usuario
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Erro ao enviar email (ex: falha no servico de email)
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailSendingException(EmailSendingException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Usuario ja verificado tentando verificar novamente
    @ExceptionHandler(UserAlreadyVerified.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyVerifiedException(UserAlreadyVerified ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Usuario nao encontrado ao tentar logar
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }
    //Codigo de verificacao expirado ao tentar verificar usuario
    @ExceptionHandler(ExpiredVerificationCodeException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredVerificationCodeException(ExpiredVerificationCodeException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Codigo de verificacao invalido ao tentar verificar usuario
    @ExceptionHandler(InvalidVerificationCode.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidVerificationCodeException(InvalidVerificationCode ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Token para reset de senha invalido
    @ExceptionHandler(InvalidPassworResetToken.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPassworResetToken(InvalidPassworResetToken ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Login com conta nao verificada
    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotVerifiedException(UserNotVerifiedException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }


    //==================EXCEPTIONS DE CLIENTE==================

    //Erro ao criar novo cliente
    @ExceptionHandler(ClientCreationException.class)
    public ResponseEntity<ApiResponse<Object>> handleClientCreationException(ClientCreationException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Cliente nao encontrado
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleClientNotFoundException(ClientNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }
    //Erro ao recuperar clientes
    @ExceptionHandler(ClientRetrievalException.class)
    public ResponseEntity<ApiResponse<Object>> handleClientRetrievalException(ClientRetrievalException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Erro ao atualizar cliente
    @ExceptionHandler(ClientUpdateException.class)
    public ResponseEntity<ApiResponse<Object>> handleClientUpdateException(ClientUpdateException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Erro ao deletar cliente
    @ExceptionHandler(ClientDeleteException.class)
    public ResponseEntity<ApiResponse<Object>> handleClientDeleteException(ClientDeleteException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //==================EXCEPTIONS DE HEALTH PLANS==================

    //Erro ao recuperar planos de saude
    @ExceptionHandler(HealthPlanRetrievalException.class)
    public ResponseEntity<ApiResponse<Object>> handleHealthPlanRetrievalException(HealthPlanRetrievalException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Plano de saude nao encontrado
    @ExceptionHandler(HealthPlanNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleHealthPlanNotFoundException(HealthPlanNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }


    //==================EXCEPTION DE USERS==================

    //Erro ao deletar usuario
    @ExceptionHandler(UserDeleteException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserDeleteException(UserDeleteException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }
    //Erro ao recuperar usuario
    @ExceptionHandler(UserRetrievalException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserRetrievalException(UserRetrievalException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Erro ao criar usuario
    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserCreationException(UserUpdateException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Usuario nao autenticado
    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.UNAUTHORIZED);
    }
    //Token de alteracao de email invalido
    @ExceptionHandler(InvalidEmailChangeTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidEmailChangeTokenException(InvalidEmailChangeTokenException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }


    //==================EXCEPTIONS DE COTACAO==================

    //Cotacao nao encontrada
    @ExceptionHandler(QuotationNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleQuotationNotFoundException(QuotationNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }

    //Erro ao deletar cotacao
    @ExceptionHandler(QuotationDeleteException.class)
    public ResponseEntity<ApiResponse<Object>> handleQuotationDeleteException(QuotationDeleteException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Faixa etaria invalida ao calcular cotacao
    @ExceptionHandler(InvalidAgeRangeException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAgeRangeException(InvalidAgeRangeException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }

    //Erro ao criar cotacao
    @ExceptionHandler(QuotationCreationException.class)
    public ResponseEntity<ApiResponse<Object>> handleQuotationCreationException(QuotationCreationException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Erro ao atualizar cotacao
    @ExceptionHandler(QuotationUpdateException.class)
    public ResponseEntity<ApiResponse<Object>> handleQuotationUpdateException(QuotationUpdateException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //====================Refresh Token exceptions=================

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.UNAUTHORIZED);
    }


    //==================EXCEPTION GENERICA==================

    //Erros de validacao de argumentos (ex: campos obrigatorios faltando)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Object> response = ApiResponse.error("Erro de validação: " + errorMsg);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    //Credenciais invalidas ao tentar logar
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex){
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST);
    }

}
