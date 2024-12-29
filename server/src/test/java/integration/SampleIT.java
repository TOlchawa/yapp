package integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Slf4j
@Tag("integration")
public class SampleIT {

    @Test
    void testIntegrationScenario() {
        log.info("Integration test scenario");
    }

}
