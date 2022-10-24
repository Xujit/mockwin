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
 * A ContestCategory.
 */
@Entity
@Table(name = "contest_category")
@Cacheable
@RegisterForReflection
public class ContestCategory extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "contest_id")
    public Long contestId;

    @Column(name = "status")
    public String status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestCategory)) {
            return false;
        }
        return id != null && id.equals(((ContestCategory) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestCategory{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", contestId=" + contestId +
            ", status='" + status + "'" +
            "}";
    }

    public ContestCategory update() {
        return update(this);
    }

    public ContestCategory persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestCategory update(ContestCategory contestCategory) {
        if (contestCategory == null) {
            throw new IllegalArgumentException("contestCategory can't be null");
        }
        var entity = ContestCategory.<ContestCategory>findById(contestCategory.id);
        if (entity != null) {
            entity.modificationCounter = contestCategory.modificationCounter;
            entity.contestId = contestCategory.contestId;
            entity.status = contestCategory.status;
        }
        return entity;
    }

    public static ContestCategory persistOrUpdate(ContestCategory contestCategory) {
        if (contestCategory == null) {
            throw new IllegalArgumentException("contestCategory can't be null");
        }
        if (contestCategory.id == null) {
            persist(contestCategory);
            return contestCategory;
        } else {
            return update(contestCategory);
        }
    }


}
