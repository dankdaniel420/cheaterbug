package csd.cheaterbug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/cheaterbug")
public class CheaterbugController {

    private static final Logger logger = LoggerFactory.getLogger(CheaterbugController.class);

    private final CheaterbugService cheaterbugService;

    @Autowired
    public CheaterbugController(CheaterbugService cheaterbugService) {
        this.cheaterbugService = cheaterbugService;
    }

    @PostMapping("/analysis")
    public Response analyseResults(@RequestBody List<Request> request) {
        // TODO remove DEBUG 1
        // logger.info("Received JSON payload: {}", request);

        List<Result> resultsList = new ArrayList<>();
        for (Request r : request) {
            // TODO remove DEBUG 2
            // logger.info(String.format("Received Expected: %f Actual: %f", r.getExpectedScore(), r.getActualScore()));
            Result result = new Result(r.getExpectedScore(), r.getActualScore());
            resultsList.add(result);
        }
        return cheaterbugService.getAnalysis(resultsList);
    }
}
