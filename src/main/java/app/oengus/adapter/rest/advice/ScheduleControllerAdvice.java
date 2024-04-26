package app.oengus.adapter.rest.advice;

import app.oengus.adapter.rest.controller.v2.ScheduleApiController;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(assignableTypes = {
    ScheduleApiController.class
})
public class ScheduleControllerAdvice {
}
