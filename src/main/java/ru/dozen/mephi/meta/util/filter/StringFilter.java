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
public class StringFilter extends Filter<String> {

    private String contains;
    private String notContains;
    private String startsWith;
    private String endsWith;

    private String like;


    private static <A> Specification<A> like(Function<Root<A>, Path<String>> pathFunction, String value) {
        return (nonNull(value)) ? (root, query, cb) -> cb.like(pathFunction.apply(root), value) : null;
    }

    private static <A> Specification<A> contains(Function<Root<A>, Path<String>> pathFunction, String value) {
        return like(pathFunction, '%' + value + '%');
    }

    private static <A> Specification<A> notContains(Function<Root<A>, Path<String>> pathFunction, String value) {
        return Specification.not(contains(pathFunction, '%' + value + '%'));
    }

    private static <A> Specification<A> startsWith(Function<Root<A>, Path<String>> pathFunction, String value) {
        return like(pathFunction, value + '%');
    }

    private static <A> Specification<A> endsWith(Function<Root<A>, Path<String>> pathFunction, String value) {
        return like(pathFunction, '%' + value);
    }

    @Override
    public <A> Specification<A> toSpecification(final String attributePath) {
        Specification<A> s = super.toSpecification(attributePath);
        if (s != null) {
            return s;
        }
        var pf = this.<A, String>pathFunction(attributePath);
        if (contains != null) {
            s = contains(pf, contains);
        }
        if (notContains != null) {
            Specification<A> toAdd = notContains(pf, notContains);
            s = s == null ? toAdd : s.and(toAdd);
        }
        if (startsWith != null) {
            Specification<A> toAdd = startsWith(pf, startsWith);
            s = s == null ? toAdd : s.and(toAdd);
        }
        if (endsWith != null) {
            Specification<A> toAdd = endsWith(pf, endsWith);
            s = s == null ? toAdd : s.and(toAdd);
        }
        if (like != null) {
            Specification<A> toAdd = like(pf, like);
            s = s == null ? toAdd : s.and(toAdd);
        }
        return s;
    }
}
