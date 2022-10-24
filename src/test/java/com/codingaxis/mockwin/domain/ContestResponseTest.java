package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestResponseTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestResponse.class);
        ContestResponse contestResponse1 = new ContestResponse();
        contestResponse1.id = 1L;
        ContestResponse contestResponse2 = new ContestResponse();
        contestResponse2.id = contestResponse1.id;
        assertThat(contestResponse1).isEqualTo(contestResponse2);
        contestResponse2.id = 2L;
        assertThat(contestResponse1).isNotEqualTo(contestResponse2);
        contestResponse1.id = null;
        assertThat(contestResponse1).isNotEqualTo(contestResponse2);
    }
}
