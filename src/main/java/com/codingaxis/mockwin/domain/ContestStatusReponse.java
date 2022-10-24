package com.codingaxis.mockwin.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A ContestStatusReponse.
 */
@Entity
@Table(name = "contest_status_reponse")
@Cacheable
@RegisterForReflection
public class ContestStatusReponse extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "contest_name")
    public String contestName;

    @Column(name = "contest_id")
    public Long contestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestStatusReponse)) {
            return false;
        }
        return id != null && id.equals(((ContestStatusReponse) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestStatusReponse{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", startDate='" + startDate + "'" +
            ", contestName='" + contestName + "'" +
            ", contestId=" + contestId +
            "}";
    }

    public ContestStatusReponse update() {
        return update(this);
    }

    public ContestStatusReponse persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestStatusReponse update(ContestStatusReponse contestStatusReponse) {
        if (contestStatusReponse == null) {
            throw new IllegalArgumentException("contestStatusReponse can't be null");
        }
        var entity = ContestStatusReponse.<ContestStatusReponse>findById(contestStatusReponse.id);
        if (entity != null) {
            entity.modificationCounter = contestStatusReponse.modificationCounter;
            entity.startDate = contestStatusReponse.startDate;
            entity.contestName = contestStatusReponse.contestName;
            entity.contestId = contestStatusReponse.contestId;
        }
        return entity;
    }

    public static ContestStatusReponse persistOrUpdate(ContestStatusReponse contestStatusReponse) {
        if (contestStatusReponse == null) {
            throw new IllegalArgumentException("contestStatusReponse can't be null");
        }
        if (contestStatusReponse.id == null) {
            persist(contestStatusReponse);
            return contestStatusReponse;
        } else {
            return update(contestStatusReponse);
        }
    }


}
