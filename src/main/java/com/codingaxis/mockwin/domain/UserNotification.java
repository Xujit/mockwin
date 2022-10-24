package com.codingaxis.mockwin.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A UserNotification.
 */
@Entity
@Table(name = "user_notification")
@Cacheable
@RegisterForReflection
public class UserNotification extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "message")
    public String message;

    @Column(name = "read")
    public Boolean read;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserNotification)) {
            return false;
        }
        return id != null && id.equals(((UserNotification) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserNotification{" +
            "id=" + id +
            ", userId=" + userId +
            ", message='" + message + "'" +
            ", read='" + read + "'" +
            "}";
    }

    public UserNotification update() {
        return update(this);
    }

    public UserNotification persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static UserNotification update(UserNotification userNotification) {
        if (userNotification == null) {
            throw new IllegalArgumentException("userNotification can't be null");
        }
        var entity = UserNotification.<UserNotification>findById(userNotification.id);
        if (entity != null) {
            entity.userId = userNotification.userId;
            entity.message = userNotification.message;
            entity.read = userNotification.read;
        }
        return entity;
    }

    public static UserNotification persistOrUpdate(UserNotification userNotification) {
        if (userNotification == null) {
            throw new IllegalArgumentException("userNotification can't be null");
        }
        if (userNotification.id == null) {
            persist(userNotification);
            return userNotification;
        } else {
            return update(userNotification);
        }
    }


}
