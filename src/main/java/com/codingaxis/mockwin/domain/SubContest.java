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
 * A SubContest.
 */
@Entity
@Table(name = "sub_contest")
@Cacheable
@RegisterForReflection
public class SubContest extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "description")
    public String description;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    @JsonbTransient
    public Contest contest;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubContest)) {
            return false;
        }
        return id != null && id.equals(((SubContest) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "SubContest{" +
            "id=" + id +
            ", description='" + description + "'" +
            "}";
    }

    public SubContest update() {
        return update(this);
    }

    public SubContest persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static SubContest update(SubContest subContest) {
        if (subContest == null) {
            throw new IllegalArgumentException("subContest can't be null");
        }
        var entity = SubContest.<SubContest>findById(subContest.id);
        if (entity != null) {
            entity.description = subContest.description;
            entity.contest = subContest.contest;
        }
        return entity;
    }

    public static SubContest persistOrUpdate(SubContest subContest) {
        if (subContest == null) {
            throw new IllegalArgumentException("subContest can't be null");
        }
        if (subContest.id == null) {
            persist(subContest);
            return subContest;
        } else {
            return update(subContest);
        }
    }


}
