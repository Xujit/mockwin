package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class ContestCategoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContestCategory.class);
        ContestCategory contestCategory1 = new ContestCategory();
        contestCategory1.id = 1L;
        ContestCategory contestCategory2 = new ContestCategory();
        contestCategory2.id = contestCategory1.id;
        assertThat(contestCategory1).isEqualTo(contestCategory2);
        contestCategory2.id = 2L;
        assertThat(contestCategory1).isNotEqualTo(contestCategory2);
        contestCategory1.id = null;
        assertThat(contestCategory1).isNotEqualTo(contestCategory2);
    }
}
