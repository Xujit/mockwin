package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class AssignMCQTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignMCQ.class);
        AssignMCQ assignMCQ1 = new AssignMCQ();
        assignMCQ1.id = 1L;
        AssignMCQ assignMCQ2 = new AssignMCQ();
        assignMCQ2.id = assignMCQ1.id;
        assertThat(assignMCQ1).isEqualTo(assignMCQ2);
        assignMCQ2.id = 2L;
        assertThat(assignMCQ1).isNotEqualTo(assignMCQ2);
        assignMCQ1.id = null;
        assertThat(assignMCQ1).isNotEqualTo(assignMCQ2);
    }
}
