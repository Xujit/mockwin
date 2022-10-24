package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class MCQContestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MCQContest.class);
        MCQContest mCQContest1 = new MCQContest();
        mCQContest1.id = 1L;
        MCQContest mCQContest2 = new MCQContest();
        mCQContest2.id = mCQContest1.id;
        assertThat(mCQContest1).isEqualTo(mCQContest2);
        mCQContest2.id = 2L;
        assertThat(mCQContest1).isNotEqualTo(mCQContest2);
        mCQContest1.id = null;
        assertThat(mCQContest1).isNotEqualTo(mCQContest2);
    }
}
