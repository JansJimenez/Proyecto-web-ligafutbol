package pe.ligacopaperu.web;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import pe.ligacopaperu.club.ClubNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClubNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(Model model, ClubNotFoundException exception) {
        model.addAttribute("message", exception.getMessage());
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String invalidResource(Model model, IllegalArgumentException exception) {
        model.addAttribute("message", exception.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String uploadTooLarge(Model model) {
        model.addAttribute("message", "Cada archivo puede pesar hasta 5 MB y el total del formulario hasta 12 MB.");
        return "error/413";
    }
}
