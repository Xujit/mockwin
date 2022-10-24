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
 * A MCQContest.
 */
@Entity
@Table(name = "mcq_contest")
@Cacheable
@RegisterForReflection
public class MCQContest extends PanacheEntityBase implements Serializable {

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

    @Column(name = "user_type_id")
    public Long userTypeId;

    @Column(name = "status")
    public String status;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "comments")
    public String comments;

    @Column(name = "reason")
    public String reason;

    @Column(name = "super_sme_id")
    public Long superSmeId;

    @OneToOne
    @JoinColumn(unique = true)
    public Exam exam;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MCQContest)) {
            return false;
        }
        return id != null && id.equals(((MCQContest) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MCQContest{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", contestId=" + contestId +
            ", noOfMcqs=" + noOfMcqs +
            ", assignedTo=" + assignedTo +
            ", userTypeId=" + userTypeId +
            ", status='" + status + "'" +
            ", fullName='" + fullName + "'" +
            ", comments='" + comments + "'" +
            ", reason='" + reason + "'" +
            ", superSmeId=" + superSmeId +
            "}";
    }

    public MCQContest update() {
        return update(this);
    }

    public MCQContest persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static MCQContest update(MCQContest mCQContest) {
        if (mCQContest == null) {
            throw new IllegalArgumentException("mCQContest can't be null");
        }
        var entity = MCQContest.<MCQContest>findById(mCQContest.id);
        if (entity != null) {
            entity.modificationCounter = mCQContest.modificationCounter;
            entity.contestId = mCQContest.contestId;
            entity.noOfMcqs = mCQContest.noOfMcqs;
            entity.assignedTo = mCQContest.assignedTo;
            entity.userTypeId = mCQContest.userTypeId;
            entity.status = mCQContest.status;
            entity.fullName = mCQContest.fullName;
            entity.comments = mCQContest.comments;
            entity.reason = mCQContest.reason;
            entity.superSmeId = mCQContest.superSmeId;
            entity.exam = mCQContest.exam;
        }
        return entity;
    }

    public static MCQContest persistOrUpdate(MCQContest mCQContest) {
        if (mCQContest == null) {
            throw new IllegalArgumentException("mCQContest can't be null");
        }
        if (mCQContest.id == null) {
            persist(mCQContest);
            return mCQContest;
        } else {
            return update(mCQContest);
        }
    }


}
