package csd.cheaterbug;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class Request {
    private Double expectedScore;
    private Double actualScore;
}
