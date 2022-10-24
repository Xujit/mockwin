package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contest.class);
        Contest contest1 = new Contest();
        contest1.id = 1L;
        Contest contest2 = new Contest();
        contest2.id = contest1.id;
        assertThat(contest1).isEqualTo(contest2);
        contest2.id = 2L;
        assertThat(contest1).isNotEqualTo(contest2);
        contest1.id = null;
        assertThat(contest1).isNotEqualTo(contest2);
    }
}
