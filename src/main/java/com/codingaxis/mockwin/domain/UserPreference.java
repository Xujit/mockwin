package com.codingaxis.mockwin.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A UserPreference.
 */
@Entity
@Table(name = "user_preference")
@Cacheable
@RegisterForReflection
public class UserPreference extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "user_id")
    public Long userId;

    @OneToMany(mappedBy = "userPreference")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<Country> countries = new HashSet<>();

    @OneToMany(mappedBy = "userPreference")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<ExamType> examTypes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPreference)) {
            return false;
        }
        return id != null && id.equals(((UserPreference) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserPreference{" +
            "id=" + id +
            ", userId=" + userId +
            "}";
    }

    public UserPreference update() {
        return update(this);
    }

    public UserPreference persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static UserPreference update(UserPreference userPreference) {
        if (userPreference == null) {
            throw new IllegalArgumentException("userPreference can't be null");
        }
        var entity = UserPreference.<UserPreference>findById(userPreference.id);
        if (entity != null) {
            entity.userId = userPreference.userId;
            entity.countries = userPreference.countries;
            entity.examTypes = userPreference.examTypes;
        }
        return entity;
    }

    public static UserPreference persistOrUpdate(UserPreference userPreference) {
        if (userPreference == null) {
            throw new IllegalArgumentException("userPreference can't be null");
        }
        if (userPreference.id == null) {
            persist(userPreference);
            return userPreference;
        } else {
            return update(userPreference);
        }
    }


}
