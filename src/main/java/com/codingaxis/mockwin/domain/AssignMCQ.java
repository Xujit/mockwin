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
 * A AssignMCQ.
 */
@Entity
@Table(name = "assign_mcq")
@Cacheable
@RegisterForReflection
public class AssignMCQ extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "mcq_id")
    public Integer mcqId;

    @Column(name = "approved")
    public Boolean approved;

    @Column(name = "reason")
    public String reason;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignMCQ)) {
            return false;
        }
        return id != null && id.equals(((AssignMCQ) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AssignMCQ{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", mcqId=" + mcqId +
            ", approved='" + approved + "'" +
            ", reason='" + reason + "'" +
            "}";
    }

    public AssignMCQ update() {
        return update(this);
    }

    public AssignMCQ persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static AssignMCQ update(AssignMCQ assignMCQ) {
        if (assignMCQ == null) {
            throw new IllegalArgumentException("assignMCQ can't be null");
        }
        var entity = AssignMCQ.<AssignMCQ>findById(assignMCQ.id);
        if (entity != null) {
            entity.modificationCounter = assignMCQ.modificationCounter;
            entity.mcqId = assignMCQ.mcqId;
            entity.approved = assignMCQ.approved;
            entity.reason = assignMCQ.reason;
        }
        return entity;
    }

    public static AssignMCQ persistOrUpdate(AssignMCQ assignMCQ) {
        if (assignMCQ == null) {
            throw new IllegalArgumentException("assignMCQ can't be null");
        }
        if (assignMCQ.id == null) {
            persist(assignMCQ);
            return assignMCQ;
        } else {
            return update(assignMCQ);
        }
    }


}
