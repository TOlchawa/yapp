package integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Tag("integration")
public class SampleIT {

    @Test
    void testIntegrationScenario() {
        String test = "ok";
        assertThat(test).isEqualTo("ok");
    }

}
