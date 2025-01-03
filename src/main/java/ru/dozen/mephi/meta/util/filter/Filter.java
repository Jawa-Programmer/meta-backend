package ru.dozen.mephi.meta.util.filter;

import static java.util.Objects.nonNull;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
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
public class Filter<T> {

    private T eq;
    private T neq;

    private static <T, A> Specification<A> eq(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.equal(pathFunction.apply(root), value) : null;
    }

    private static <T, A> Specification<A> neq(Function<Root<A>, Path<T>> pathFunction, T value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.notEqual(pathFunction.apply(root), value) : null;
    }

    public <A> Specification<A> toSpecification(final String attributeName) {
        if (eq != null) {
            return eq(root -> root.get(attributeName), eq);
        }
        if (neq != null) {
            return neq(root -> root.get(attributeName), neq);
        }
        return null;
    }
}