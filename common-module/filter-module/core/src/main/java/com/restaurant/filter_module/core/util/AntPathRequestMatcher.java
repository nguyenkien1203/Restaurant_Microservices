package com.restaurant.filter_module.core.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import java.util.Collections;
import java.util.Map;


/**
 * The type Ant path request matcher.
 */
public class AntPathRequestMatcher implements RequestMatcher {

    private static final String MATCH_ALL = "/**";

    private final Matcher matcher;

    private final String pattern;

    private final HttpMethod httpMethod;

    private final boolean caseSensitive;

    private final UrlPathHelper urlPathHelper;

    /**
     * Instantiates a new Ant path request matcher.
     *
     * @param pattern the pattern
     */
    public AntPathRequestMatcher(String pattern) {
        this(pattern, null);
    }

    /**
     * Instantiates a new Ant path request matcher.
     *
     * @param pattern    the pattern
     * @param httpMethod the http method
     */
    public AntPathRequestMatcher(String pattern,
                                 String httpMethod) {
        this(pattern, httpMethod, true);
    }

    /**
     * Instantiates a new Ant path request matcher.
     *
     * @param pattern       the pattern
     * @param httpMethod    the http method
     * @param caseSensitive the case sensitive
     */
    public AntPathRequestMatcher(String pattern,
                                 String httpMethod,
                                 boolean caseSensitive) {
        this(pattern, httpMethod, caseSensitive, null);
    }

    /**
     * Instantiates a new Ant path request matcher.
     *
     * @param pattern       the pattern
     * @param httpMethod    the http method
     * @param caseSensitive the case sensitive
     * @param urlPathHelper the url path helper
     */
    public AntPathRequestMatcher(String pattern,
                                 String httpMethod,
                                 boolean caseSensitive,
                                 UrlPathHelper urlPathHelper) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new SpringAntMatcher(pattern, caseSensitive);
            }
        }
        this.pattern = pattern;
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
        this.urlPathHelper = urlPathHelper;
    }

    /**
     * Ant matcher ant path request matcher.
     *
     * @param pattern the pattern
     * @return the ant path request matcher
     */
    public static AntPathRequestMatcher antMatcher(String pattern) {
        Assert.hasText(pattern, "pattern cannot be empty");
        return new AntPathRequestMatcher(pattern);
    }

    /**
     * Ant matcher ant path request matcher.
     *
     * @param method the method
     * @return the ant path request matcher
     */
    public static AntPathRequestMatcher antMatcher(HttpMethod method) {
        Assert.notNull(method, "method cannot be null");
        return new AntPathRequestMatcher(MATCH_ALL, method.name());
    }

    /**
     * Ant matcher ant path request matcher.
     *
     * @param method  the method
     * @param pattern the pattern
     * @return the ant path request matcher
     */
    public static AntPathRequestMatcher antMatcher(HttpMethod method, String pattern) {
        Assert.notNull(method, "method cannot be null");
        Assert.hasText(pattern, "pattern cannot be empty");
        return new AntPathRequestMatcher(pattern, method.name());
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtils.hasText(request.getMethod())
                && this.httpMethod != HttpMethod.valueOf(request.getMethod())
        ) {
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        String url = getRequestPath(request);
        return this.matcher.matches(url);
    }


    private String getRequestPath(HttpServletRequest request) {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper.getPathWithinApplication(request);
        }
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
        }
        return url;
    }

    /**
     * Gets pattern.
     *
     * @return the pattern
     */
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AntPathRequestMatcher other)) {
            return false;
        }
        return this.pattern.equals(other.pattern)
                && this.httpMethod == other.httpMethod
                && this.caseSensitive == other.caseSensitive;
    }

    @Override
    public int hashCode() {
        int result = (this.pattern != null) ? this.pattern.hashCode() : 0;
        result = 31 * result + ((this.httpMethod != null) ? this.httpMethod.hashCode() : 0);
        result = 31 * result + (this.caseSensitive ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ant [pattern='").append(this.pattern).append("'");
        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }
        sb.append("]");
        return sb.toString();
    }

    private interface Matcher {

        /**
         * Matches boolean.
         *
         * @param path the path
         * @return the boolean
         */
        boolean matches(String path);

        /**
         * Extract uri template variables map.
         *
         * @param path the path
         * @return the map
         */
        Map<String, String> extractUriTemplateVariables(String path);

    }

    private static final class SpringAntMatcher implements Matcher {

        private final AntPathMatcher antMatcher;

        private final String pattern;

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }

    }

    private static final class SubpathMatcher implements Matcher {

        private final String subpath;

        private final int length;

        private final boolean caseSensitive;

        private SubpathMatcher(String subpath, boolean caseSensitive) {
            Assert.isTrue(!subpath.contains("*"), "subpath cannot contain \"*\"");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }

    }
}
