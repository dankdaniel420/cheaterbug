package csd.cheaterbug.Entity;

import java.util.Map;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class Response {
    private Map<String, String> expectedProbability;
    private Map<String, String> cheatProbability;
}
