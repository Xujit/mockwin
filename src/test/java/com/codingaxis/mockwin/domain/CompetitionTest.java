package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class CompetitionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Competition.class);
        Competition competition1 = new Competition();
        competition1.id = 1L;
        Competition competition2 = new Competition();
        competition2.id = competition1.id;
        assertThat(competition1).isEqualTo(competition2);
        competition2.id = 2L;
        assertThat(competition1).isNotEqualTo(competition2);
        competition1.id = null;
        assertThat(competition1).isNotEqualTo(competition2);
    }
}
