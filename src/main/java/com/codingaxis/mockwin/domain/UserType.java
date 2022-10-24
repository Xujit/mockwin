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
 * A UserType.
 */
@Entity
@Table(name = "user_type")
@Cacheable
@RegisterForReflection
public class UserType extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "user_type")
    public String userType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserType)) {
            return false;
        }
        return id != null && id.equals(((UserType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserType{" +
            "id=" + id +
            ", userType='" + userType + "'" +
            "}";
    }

    public UserType update() {
        return update(this);
    }

    public UserType persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static UserType update(UserType userType) {
        if (userType == null) {
            throw new IllegalArgumentException("userType can't be null");
        }
        var entity = UserType.<UserType>findById(userType.id);
        if (entity != null) {
            entity.userType = userType.userType;
        }
        return entity;
    }

    public static UserType persistOrUpdate(UserType userType) {
        if (userType == null) {
            throw new IllegalArgumentException("userType can't be null");
        }
        if (userType.id == null) {
            persist(userType);
            return userType;
        } else {
            return update(userType);
        }
    }


}
