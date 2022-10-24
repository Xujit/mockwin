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
 * A ContestAssign.
 */
@Entity
@Table(name = "contest_assign")
@Cacheable
@RegisterForReflection
public class ContestAssign extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "designer_id")
    public Long designerId;

    @Column(name = "super_sme_id")
    public Long superSmeId;

    @Column(name = "surveyer_id")
    public Long surveyerId;

    @Column(name = "sme_id")
    public Long smeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContestAssign)) {
            return false;
        }
        return id != null && id.equals(((ContestAssign) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ContestAssign{" +
            "id=" + id +
            ", designerId=" + designerId +
            ", superSmeId=" + superSmeId +
            ", surveyerId=" + surveyerId +
            ", smeId=" + smeId +
            "}";
    }

    public ContestAssign update() {
        return update(this);
    }

    public ContestAssign persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static ContestAssign update(ContestAssign contestAssign) {
        if (contestAssign == null) {
            throw new IllegalArgumentException("contestAssign can't be null");
        }
        var entity = ContestAssign.<ContestAssign>findById(contestAssign.id);
        if (entity != null) {
            entity.designerId = contestAssign.designerId;
            entity.superSmeId = contestAssign.superSmeId;
            entity.surveyerId = contestAssign.surveyerId;
            entity.smeId = contestAssign.smeId;
        }
        return entity;
    }

    public static ContestAssign persistOrUpdate(ContestAssign contestAssign) {
        if (contestAssign == null) {
            throw new IllegalArgumentException("contestAssign can't be null");
        }
        if (contestAssign.id == null) {
            persist(contestAssign);
            return contestAssign;
        } else {
            return update(contestAssign);
        }
    }


}
