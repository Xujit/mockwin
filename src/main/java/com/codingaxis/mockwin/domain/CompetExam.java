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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A CompetExam.
 */
@Entity
@Table(name = "compet_exam")
@Cacheable
@RegisterForReflection
public class CompetExam extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "compet_exams_id")
    public Long competExamsId;

    @NotNull
    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "status")
    public Integer status;

    @Column(name = "created_date")
    public LocalDate createdDate;

    @Column(name = "last_updated")
    public LocalDate lastUpdated;

    @OneToMany(mappedBy = "competExam")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<Course> courses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompetExam)) {
            return false;
        }
        return id != null && id.equals(((CompetExam) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CompetExam{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", competExamsId=" + competExamsId +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", status=" + status +
            ", createdDate='" + createdDate + "'" +
            ", lastUpdated='" + lastUpdated + "'" +
            "}";
    }

    public CompetExam update() {
        return update(this);
    }

    public CompetExam persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static CompetExam update(CompetExam competExam) {
        if (competExam == null) {
            throw new IllegalArgumentException("competExam can't be null");
        }
        var entity = CompetExam.<CompetExam>findById(competExam.id);
        if (entity != null) {
            entity.modificationCounter = competExam.modificationCounter;
            entity.competExamsId = competExam.competExamsId;
            entity.name = competExam.name;
            entity.description = competExam.description;
            entity.status = competExam.status;
            entity.createdDate = competExam.createdDate;
            entity.lastUpdated = competExam.lastUpdated;
            entity.courses = competExam.courses;
        }
        return entity;
    }

    public static CompetExam persistOrUpdate(CompetExam competExam) {
        if (competExam == null) {
            throw new IllegalArgumentException("competExam can't be null");
        }
        if (competExam.id == null) {
            persist(competExam);
            return competExam;
        } else {
            return update(competExam);
        }
    }


}
