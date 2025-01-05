package ru.dozen.mephi.meta.util.filter;

import static java.util.Objects.nonNull;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CollectionFilter<V extends Serializable, C extends Collection<V> & Serializable> extends Filter<C> {

    private V contains;
    private V notContains;

    private static <A, V> Specification<A> contains(Function<Root<A>, Path<Collection<V>>> pathFunction, V value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.isMember(value, pathFunction.apply(root)) : null;
    }


    private static <A, V> Specification<A> notContains(Function<Root<A>, Path<Collection<V>>> pathFunction, V value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.isNotMember(value, pathFunction.apply(root)) : null;
    }

    @Override
    public <A> Specification<A> toSpecification(final String attributePath) {
        Specification<A> s = super.toSpecification(attributePath);
        if (s != null) {
            return s;
        }
        if (contains != null) {
            s = contains(pathFunction(attributePath), contains);
        }
        if (notContains != null) {
            Specification<A> toAdd = notContains(pathFunction(attributePath), notContains);
            s = s == null ? toAdd : s.and(toAdd);
        }
        return s;
    }
}
