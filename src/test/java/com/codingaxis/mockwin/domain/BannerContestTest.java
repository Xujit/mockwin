package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class BannerContestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BannerContest.class);
        BannerContest bannerContest1 = new BannerContest();
        bannerContest1.id = 1L;
        BannerContest bannerContest2 = new BannerContest();
        bannerContest2.id = bannerContest1.id;
        assertThat(bannerContest1).isEqualTo(bannerContest2);
        bannerContest2.id = 2L;
        assertThat(bannerContest1).isNotEqualTo(bannerContest2);
        bannerContest1.id = null;
        assertThat(bannerContest1).isNotEqualTo(bannerContest2);
    }
}
