package csd.cheaterbug;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import csd.cheaterbug.Entity.Request;
import csd.cheaterbug.Entity.Response;
import csd.cheaterbug.Entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cheaterbug")
public class CheaterbugController {

    private static final Logger logger = LoggerFactory.getLogger(CheaterbugController.class);
    private final CheaterbugService cheaterbugService;

    public CheaterbugController(CheaterbugService cheaterbugService) {
        this.cheaterbugService = cheaterbugService;
    }

    @PostMapping("/analysis")
    public Response analyseResults(@RequestBody List<Request> request) {
        List<Result> resultsList = request.stream()
            .map(r -> {
                Result result = new Result(r.getExpectedScore(), r.getActualScore());
                logger.info(result.toString());
                return result;
            })
            .collect(Collectors.toList());

        return cheaterbugService.getAnalysis(resultsList);
    }

    @GetMapping("/analysis")
    public ResponseEntity<String> handleGetRequest() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .body("This endpoint only supports POST requests with JSON data.");
    }
}
