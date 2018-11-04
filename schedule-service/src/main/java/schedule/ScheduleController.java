package schedule;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {
    @RequestMapping(value = "/test")
    public String test() {
        return "this is a test!";
    }

}
