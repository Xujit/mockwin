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
 * A ContestResponse.
 */
@Entity
@Table(name = "contest_response")
@Cacheable
@RegisterForReflection
public class ContestResponse extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "description")
    public String description;

    @OneToOne
    @JoinColumn(unique = true)
    public Contest contest;

    @OneToMany(mappedBy = "contestResponse")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<Mcq> mcqs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestResponse)) {
            return false;
        }
        return id != null && id.equals(((ContestResponse) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestResponse{" +
            "id=" + id +
            ", description='" + description + "'" +
            "}";
    }

    public ContestResponse update() {
        return update(this);
    }

    public ContestResponse persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestResponse update(ContestResponse contestResponse) {
        if (contestResponse == null) {
            throw new IllegalArgumentException("contestResponse can't be null");
        }
        var entity = ContestResponse.<ContestResponse>findById(contestResponse.id);
        if (entity != null) {
            entity.description = contestResponse.description;
            entity.contest = contestResponse.contest;
            entity.mcqs = contestResponse.mcqs;
        }
        return entity;
    }

    public static ContestResponse persistOrUpdate(ContestResponse contestResponse) {
        if (contestResponse == null) {
            throw new IllegalArgumentException("contestResponse can't be null");
        }
        if (contestResponse.id == null) {
            persist(contestResponse);
            return contestResponse;
        } else {
            return update(contestResponse);
        }
    }


}
