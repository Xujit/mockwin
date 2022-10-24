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
 * A ContestSme.
 */
@Entity
@Table(name = "contest_sme")
@Cacheable
@RegisterForReflection
public class ContestSme extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "contest_id")
    public Long contestId;

    @Column(name = "no_of_mcqs")
    public Long noOfMcqs;

    @Column(name = "assigned_to")
    public Long assignedTo;

    @Column(name = "status")
    public String status;

    @Column(name = "instructions")
    public String instructions;

    @Column(name = "super_sme_id")
    public Long superSMEId;

    @OneToOne
    @JoinColumn(unique = true)
    public Exam exam;

    @OneToOne
    @JoinColumn(unique = true)
    public Mcq mcq;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestSme)) {
            return false;
        }
        return id != null && id.equals(((ContestSme) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestSme{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", contestId=" + contestId +
            ", noOfMcqs=" + noOfMcqs +
            ", assignedTo=" + assignedTo +
            ", status='" + status + "'" +
            ", instructions='" + instructions + "'" +
            ", superSMEId=" + superSMEId +
            "}";
    }

    public ContestSme update() {
        return update(this);
    }

    public ContestSme persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestSme update(ContestSme contestSme) {
        if (contestSme == null) {
            throw new IllegalArgumentException("contestSme can't be null");
        }
        var entity = ContestSme.<ContestSme>findById(contestSme.id);
        if (entity != null) {
            entity.modificationCounter = contestSme.modificationCounter;
            entity.contestId = contestSme.contestId;
            entity.noOfMcqs = contestSme.noOfMcqs;
            entity.assignedTo = contestSme.assignedTo;
            entity.status = contestSme.status;
            entity.instructions = contestSme.instructions;
            entity.superSMEId = contestSme.superSMEId;
            entity.exam = contestSme.exam;
            entity.mcq = contestSme.mcq;
        }
        return entity;
    }

    public static ContestSme persistOrUpdate(ContestSme contestSme) {
        if (contestSme == null) {
            throw new IllegalArgumentException("contestSme can't be null");
        }
        if (contestSme.id == null) {
            persist(contestSme);
            return contestSme;
        } else {
            return update(contestSme);
        }
    }


}
