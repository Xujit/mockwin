package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class SubContestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubContest.class);
        SubContest subContest1 = new SubContest();
        subContest1.id = 1L;
        SubContest subContest2 = new SubContest();
        subContest2.id = subContest1.id;
        assertThat(subContest1).isEqualTo(subContest2);
        subContest2.id = 2L;
        assertThat(subContest1).isNotEqualTo(subContest2);
        subContest1.id = null;
        assertThat(subContest1).isNotEqualTo(subContest2);
    }
}
