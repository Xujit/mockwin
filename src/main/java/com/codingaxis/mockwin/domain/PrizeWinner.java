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
 * A PrizeWinner.
 */
@Entity
@Table(name = "prize_winner")
@Cacheable
@RegisterForReflection
public class PrizeWinner extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @Column(name = "name")
    public String name;

    @NotNull
    @Column(name = "prize_name", nullable = false)
    public String prizeName;

    @Column(name = "position")
    public Long position;

    @ManyToOne
    @JoinColumn(name = "contest_conquistador_id")
    @JsonbTransient
    public ContestConquistador contestConquistador;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrizeWinner)) {
            return false;
        }
        return id != null && id.equals(((PrizeWinner) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PrizeWinner{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", name='" + name + "'" +
            ", prizeName='" + prizeName + "'" +
            ", position=" + position +
            "}";
    }

    public PrizeWinner update() {
        return update(this);
    }

    public PrizeWinner persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static PrizeWinner update(PrizeWinner prizeWinner) {
        if (prizeWinner == null) {
            throw new IllegalArgumentException("prizeWinner can't be null");
        }
        var entity = PrizeWinner.<PrizeWinner>findById(prizeWinner.id);
        if (entity != null) {
            entity.modificationCounter = prizeWinner.modificationCounter;
            entity.name = prizeWinner.name;
            entity.prizeName = prizeWinner.prizeName;
            entity.position = prizeWinner.position;
            entity.contestConquistador = prizeWinner.contestConquistador;
        }
        return entity;
    }

    public static PrizeWinner persistOrUpdate(PrizeWinner prizeWinner) {
        if (prizeWinner == null) {
            throw new IllegalArgumentException("prizeWinner can't be null");
        }
        if (prizeWinner.id == null) {
            persist(prizeWinner);
            return prizeWinner;
        } else {
            return update(prizeWinner);
        }
    }


}
