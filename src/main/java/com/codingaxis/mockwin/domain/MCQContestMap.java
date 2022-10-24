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
 * A MCQContestMap.
 */
@Entity
@Table(name = "mcq_contest_map")
@Cacheable
@RegisterForReflection
public class MCQContestMap extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "contest_id")
    public Long contestId;

    @Column(name = "noofmcqs")
    public String noofmcqs;

    @Column(name = "sub_category")
    public String subCategory;

    @Column(name = "assigned_to")
    public Long assignedTo;

    @Column(name = "status")
    public String status;

    @OneToMany(mappedBy = "mCQContestMap")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<Mcq> mcqs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MCQContestMap)) {
            return false;
        }
        return id != null && id.equals(((MCQContestMap) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MCQContestMap{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", contestId=" + contestId +
            ", noofmcqs='" + noofmcqs + "'" +
            ", subCategory='" + subCategory + "'" +
            ", assignedTo=" + assignedTo +
            ", status='" + status + "'" +
            "}";
    }

    public MCQContestMap update() {
        return update(this);
    }

    public MCQContestMap persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static MCQContestMap update(MCQContestMap mCQContestMap) {
        if (mCQContestMap == null) {
            throw new IllegalArgumentException("mCQContestMap can't be null");
        }
        var entity = MCQContestMap.<MCQContestMap>findById(mCQContestMap.id);
        if (entity != null) {
            entity.modificationCounter = mCQContestMap.modificationCounter;
            entity.contestId = mCQContestMap.contestId;
            entity.noofmcqs = mCQContestMap.noofmcqs;
            entity.subCategory = mCQContestMap.subCategory;
            entity.assignedTo = mCQContestMap.assignedTo;
            entity.status = mCQContestMap.status;
            entity.mcqs = mCQContestMap.mcqs;
        }
        return entity;
    }

    public static MCQContestMap persistOrUpdate(MCQContestMap mCQContestMap) {
        if (mCQContestMap == null) {
            throw new IllegalArgumentException("mCQContestMap can't be null");
        }
        if (mCQContestMap.id == null) {
            persist(mCQContestMap);
            return mCQContestMap;
        } else {
            return update(mCQContestMap);
        }
    }


}
