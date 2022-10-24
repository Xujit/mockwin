package com.codingaxis.mockwin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.mockwin.TestUtil;
import org.junit.jupiter.api.Test;


public class UserNotificationTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserNotification.class);
        UserNotification userNotification1 = new UserNotification();
        userNotification1.id = 1L;
        UserNotification userNotification2 = new UserNotification();
        userNotification2.id = userNotification1.id;
        assertThat(userNotification1).isEqualTo(userNotification2);
        userNotification2.id = 2L;
        assertThat(userNotification1).isNotEqualTo(userNotification2);
        userNotification1.id = null;
        assertThat(userNotification1).isNotEqualTo(userNotification2);
    }
}
