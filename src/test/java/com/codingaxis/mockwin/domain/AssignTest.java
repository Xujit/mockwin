package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class AssignTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assign.class);
        Assign assign1 = new Assign();
        assign1.id = 1L;
        Assign assign2 = new Assign();
        assign2.id = assign1.id;
        assertThat(assign1).isEqualTo(assign2);
        assign2.id = 2L;
        assertThat(assign1).isNotEqualTo(assign2);
        assign1.id = null;
        assertThat(assign1).isNotEqualTo(assign2);
    }
}
