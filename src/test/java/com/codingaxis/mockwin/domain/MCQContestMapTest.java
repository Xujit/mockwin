package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class MCQContestMapTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MCQContestMap.class);
        MCQContestMap mCQContestMap1 = new MCQContestMap();
        mCQContestMap1.id = 1L;
        MCQContestMap mCQContestMap2 = new MCQContestMap();
        mCQContestMap2.id = mCQContestMap1.id;
        assertThat(mCQContestMap1).isEqualTo(mCQContestMap2);
        mCQContestMap2.id = 2L;
        assertThat(mCQContestMap1).isNotEqualTo(mCQContestMap2);
        mCQContestMap1.id = null;
        assertThat(mCQContestMap1).isNotEqualTo(mCQContestMap2);
    }
}
