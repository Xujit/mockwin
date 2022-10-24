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
 * A Competition.
 */
@Entity
@Table(name = "competition")
@Cacheable
@RegisterForReflection
public class Competition extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "country_id")
    public Long countryId;

    @Column(name = "category_id")
    public Long categoryId;

    @NotNull
    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Competition)) {
            return false;
        }
        return id != null && id.equals(((Competition) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Competition{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", countryId=" + countryId +
            ", categoryId=" + categoryId +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            "}";
    }

    public Competition update() {
        return update(this);
    }

    public Competition persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Competition update(Competition competition) {
        if (competition == null) {
            throw new IllegalArgumentException("competition can't be null");
        }
        var entity = Competition.<Competition>findById(competition.id);
        if (entity != null) {
            entity.modificationCounter = competition.modificationCounter;
            entity.countryId = competition.countryId;
            entity.categoryId = competition.categoryId;
            entity.name = competition.name;
            entity.description = competition.description;
        }
        return entity;
    }

    public static Competition persistOrUpdate(Competition competition) {
        if (competition == null) {
            throw new IllegalArgumentException("competition can't be null");
        }
        if (competition.id == null) {
            persist(competition);
            return competition;
        } else {
            return update(competition);
        }
    }


}
