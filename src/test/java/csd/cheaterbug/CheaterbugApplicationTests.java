package csd.cheaterbug;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.ArrayList;

@SpringBootTest
class CheaterbugApplicationTests {

	@Autowired
	private CheaterbugService cheaterbugService;

	@Test
	void checkResultsToResponse_twoResults() {
        List<Result> results = new ArrayList<>();
        results.add(new Result(0.7,1.0));
		results.add(new Result(0.3,0.0));

		Response response = cheaterbugService.getAnalysis(results);
		assertNotNull(response);
	}

	@Test
	void checkResults_expectedDraw() {
		Result result = new Result(0.6,1.0);

		assertTrue(result.getPDrawExpected() > result.getPWinExpected());
		assertTrue(result.getPWinExpected() > result.getPLoseExpected());
	}

	@Test
	void checkResults_expectedWin() {
		Result result = new Result(0.8,1.0);

		assertTrue(result.getPWinExpected() > result.getPDrawExpected());
		assertTrue(result.getPDrawExpected() > result.getPLoseExpected());
	}

	@Test
	void checkResults_expectedLose() {
		Result result = new Result(0.2,1.0);

		assertTrue(result.getPLoseExpected() > result.getPDrawExpected());
		assertTrue(result.getPDrawExpected() > result.getPWinExpected());
	}
}