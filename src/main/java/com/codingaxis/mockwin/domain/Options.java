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
 * A Options.
 */
@Entity
@Table(name = "options")
@Cacheable
@RegisterForReflection
public class Options extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "modification_counter")
    public Integer modificationCounter;

    @NotNull
    @Column(name = "answer", nullable = false)
    public String answer;

    @Column(name = "correct")
    public Boolean correct;

    @ManyToOne
    @JoinColumn(name = "mcq_id")
    @JsonbTransient
    public Mcq mcq;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Options)) {
            return false;
        }
        return id != null && id.equals(((Options) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Options{" +
            "id=" + id +
            ", modificationCounter=" + modificationCounter +
            ", answer='" + answer + "'" +
            ", correct='" + correct + "'" +
            "}";
    }

    public Options update() {
        return update(this);
    }

    public Options persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Options update(Options options) {
        if (options == null) {
            throw new IllegalArgumentException("options can't be null");
        }
        var entity = Options.<Options>findById(options.id);
        if (entity != null) {
            entity.modificationCounter = options.modificationCounter;
            entity.answer = options.answer;
            entity.correct = options.correct;
            entity.mcq = options.mcq;
        }
        return entity;
    }

    public static Options persistOrUpdate(Options options) {
        if (options == null) {
            throw new IllegalArgumentException("options can't be null");
        }
        if (options.id == null) {
            persist(options);
            return options;
        } else {
            return update(options);
        }
    }


}
