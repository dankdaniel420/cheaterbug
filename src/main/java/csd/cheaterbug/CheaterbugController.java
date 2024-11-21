package csd.cheaterbug;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.cheaterbug.Entity.Request;
import csd.cheaterbug.Entity.Response;
import csd.cheaterbug.Entity.Result;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/cheaterbug")
public class CheaterbugController {

    private final CheaterbugService cheaterbugService;

    public CheaterbugController(CheaterbugService cheaterbugService) {
        this.cheaterbugService = cheaterbugService;
    }

    /**
     * Analyse the list of expected and actual scores given by client
     * 
     * @param request List of Request objects containing expected and actual scores
     * @return Response object containing the probabilities of expected and cheat
     */
    @PostMapping("/analysis")
    public Response analyseResults(@RequestBody List<Request> request) {
        List<Result> resultsList = new ArrayList<>();
        for (Request r : request) {
            Result result = new Result(r.getExpectedScore(), r.getActualScore());
            resultsList.add(result);
        }
        return cheaterbugService.getAnalysis(resultsList);
    }
}
