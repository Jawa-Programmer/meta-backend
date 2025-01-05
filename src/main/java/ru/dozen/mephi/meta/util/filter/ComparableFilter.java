package ru.dozen.mephi.meta.util.filter;

import static java.util.Objects.nonNull;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
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
public class ComparableFilter<T extends Comparable<T> & Serializable> extends Filter<T> {

    private T lt;
    private T gt;
    private T lte;
    private T gte;

    private static <A, T extends Comparable<T>> Specification<A> lt(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.lessThan(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> gt(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.greaterThan(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> lte(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.lessThanOrEqualTo(pathFunction.apply(root), value) : null;
    }

    private static <A, T extends Comparable<T>> Specification<A> gte(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.greaterThanOrEqualTo(pathFunction.apply(root), value) : null;
    }


    @Override
    public <A> Specification<A> toSpecification(final String attributePath) {
        Specification<A> s = super.toSpecification(attributePath);
        if (s != null) {
            return s;
        }
        var pf = this.<A, T>pathFunction(attributePath);
        Specification<A> less = null;
        if (lt != null) {
            less = lt(pf, lt);
        } else if (lte != null) {
            less = lte(pf, lte);
        }

        Specification<A> greater = null;
        if (gt != null) {
            greater = gt(pf, gt);
        } else if (gte != null) {
            greater = gte(pf, gte);
        }

        if (less != null && greater != null) {
            return less.and(greater);
        }
        return less != null ? less : greater;

    }
}
