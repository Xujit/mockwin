package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class OptionsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Options.class);
        Options options1 = new Options();
        options1.id = 1L;
        Options options2 = new Options();
        options2.id = options1.id;
        assertThat(options1).isEqualTo(options2);
        options2.id = 2L;
        assertThat(options1).isNotEqualTo(options2);
        options1.id = null;
        assertThat(options1).isNotEqualTo(options2);
    }
}
