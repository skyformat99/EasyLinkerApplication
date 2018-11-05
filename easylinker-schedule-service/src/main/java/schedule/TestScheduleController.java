package schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestScheduleController {
    @Value("${test}")
    private String test;
    @RequestMapping("/testSchedule")
    public String testSchedule(){
        return test;
    }
}
