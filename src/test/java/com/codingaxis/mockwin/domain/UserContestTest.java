package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class UserContestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserContest.class);
        UserContest userContest1 = new UserContest();
        userContest1.id = 1L;
        UserContest userContest2 = new UserContest();
        userContest2.id = userContest1.id;
        assertThat(userContest1).isEqualTo(userContest2);
        userContest2.id = 2L;
        assertThat(userContest1).isNotEqualTo(userContest2);
        userContest1.id = null;
        assertThat(userContest1).isNotEqualTo(userContest2);
    }
}
