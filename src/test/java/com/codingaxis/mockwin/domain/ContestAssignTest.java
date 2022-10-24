package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestAssignTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestAssign.class);
        ContestAssign contestAssign1 = new ContestAssign();
        contestAssign1.id = 1L;
        ContestAssign contestAssign2 = new ContestAssign();
        contestAssign2.id = contestAssign1.id;
        assertThat(contestAssign1).isEqualTo(contestAssign2);
        contestAssign2.id = 2L;
        assertThat(contestAssign1).isNotEqualTo(contestAssign2);
        contestAssign1.id = null;
        assertThat(contestAssign1).isNotEqualTo(contestAssign2);
    }
}
