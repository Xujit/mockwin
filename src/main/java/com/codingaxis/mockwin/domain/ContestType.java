package com.codingaxis.mockwin.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A ContestType.
 */
@Entity
@Table(name = "contest_type")
@Cacheable
@RegisterForReflection
public class ContestType extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @NotNull
    @Column(name = "contest_type", nullable = false)
    public String contestType;

    @Column(name = "description")
    public String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestType)) {
            return false;
        }
        return id != null && id.equals(((ContestType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestType{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", contestType='" + contestType + "'" +
            ", description='" + description + "'" +
            "}";
    }

    public ContestType update() {
        return update(this);
    }

    public ContestType persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestType update(ContestType contestType) {
        if (contestType == null) {
            throw new IllegalArgumentException("contestType can't be null");
        }
        var entity = ContestType.<ContestType>findById(contestType.id);
        if (entity != null) {
            entity.modificationCounter = contestType.modificationCounter;
            entity.contestType = contestType.contestType;
            entity.description = contestType.description;
        }
        return entity;
    }

    public static ContestType persistOrUpdate(ContestType contestType) {
        if (contestType == null) {
            throw new IllegalArgumentException("contestType can't be null");
        }
        if (contestType.id == null) {
            persist(contestType);
            return contestType;
        } else {
            return update(contestType);
        }
    }


}
