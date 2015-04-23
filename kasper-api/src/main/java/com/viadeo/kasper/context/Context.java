package com.viadeo.kasper.context;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.ID;

import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Context implements Serializable {

    /**
     * The name of the context key when stored in meta data maps
     */
    public static final String METANAME = "context";
    public static final String CALL_TYPE = "callType";
    public static final String USER_AGENT = "userAgent";
    public static final String REFERER = "Referer";
    public static final String KASPER_CID_SHORTNAME = "kcid";
    public static final String SEQ_INC_SHORTNAME = "seq";
    public static final String USER_ID_SHORTNAME = "userID";
    public static final String UID_SHORTNAME = "userId";
    public static final String ULANG_SHORTNAME = "userLang";
    public static final String UCOUNTRY_SHORTNAME = "userCountry";
    public static final String APPLICATION_ID_SHORTNAME = "appId";
    public static final String REQUEST_CID_SHORTNAME = "corrRequestId";
    public static final String FUNNEL_CID_SHORTNAME = "corrFunnelId";
    public static final String SESSION_CID_SHORTNAME = "corrSessionId";
    public static final String SECURITY_TOKEN_SHORTNAME = "authToken";
    public static final String ACCESS_TOKEN_SHORTNAME = "accessToken";
    public static final String FUNNEL_NAME_SHORTNAME = "funnelName";
    public static final String FUNNEL_VERS_SHORTNAME = "funnelVersion";
    public static final String IP_ADDRESS_SHORTNAME = "ipAddress";
    public static final String TAGS_SHORTNAME = "tags";

    private static final char SEPARATOR = ',';
    private static final Splitter SPLITTER = Splitter.on(SEPARATOR).omitEmptyStrings().trimResults();
    private static final Joiner JOINER = Joiner.on(SEPARATOR).skipNulls();

    private final Map<String, Serializable> properties;

    private Context(final Map<String, Serializable> properties) {
        this.properties = checkNotNull(properties);
    }

    private <T extends Serializable> Optional<T> getGenericProperty(String key) {
        return Optional.fromNullable((T) properties.get(key));
    }

    /**
     * Get a property value
     *
     * @param key the property name
     * @return the optional value
     */
    public Optional<Serializable> getProperty(String key) {
        return getGenericProperty(key);
    }

    /**
     * Checks whether context owns this property by name
     *
     * @param key the property name
     * @return true if this context owns this property name
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Retrieve all the context properties
     *
     * @return all the context properties
     */
    public Map<String, Serializable> getProperties() {
        return ImmutableMap.copyOf(properties);
    }

    /**
     * @return the security token used in current context
     */
    public Optional<String> getSecurityToken() {
        return getGenericProperty(SECURITY_TOKEN_SHORTNAME);
    }

    /**
     * @return the access token used in current context
     */
    public Optional<String> getAccessToken() {
        return getGenericProperty(ACCESS_TOKEN_SHORTNAME);
    }

    /**
     * @return the associated ID of the current user
     */
    public Optional<ID> getUserID() {
        return getGenericProperty(USER_ID_SHORTNAME);
    }

    /**
     * @return the associated id of the current user
     * @deprecated use {@link #getUserID()} instead.
     */
    @Deprecated
    public Optional<String>  getUserId() {
        return getGenericProperty(UID_SHORTNAME);
    }

    /**
     * @return the user default language (preferred: ISO 639-1)
     */
    public Optional<String> getUserLang() {
        return getGenericProperty(ULANG_SHORTNAME);
    }

    /**
     * @return the user default language as locale
     */
    public Optional<Locale> getUserLangAsLocale() {
        Optional<String> userLang = getUserLang();
        if (userLang.isPresent()) {
            return Optional.of(Locale.forLanguageTag(userLang.get()));
        }
        return Optional.absent();
    }

    /**
     * @return the user country (ISO 3166)
     */
    public Optional<String> getUserCountry() {
        return getGenericProperty(UCOUNTRY_SHORTNAME);
    }

    /**
     * @return the caller application id
     */
    public Optional<String> getApplicationId() {
        return getGenericProperty(APPLICATION_ID_SHORTNAME);
    }

    /**
     * @return the application request correlation id
     */
    public Optional<String> getKasperCorrelationId() {
        return getGenericProperty(KASPER_CID_SHORTNAME);
    }

    /**
     * @return the application request correlation id
     */
    public Optional<String> getRequestCorrelationId() {
        return getGenericProperty(REQUEST_CID_SHORTNAME);
    }

    /**
     * @return the application request correlation id
     */
    public Optional<String> getFunnelCorrelationId() {
        return getGenericProperty(FUNNEL_CID_SHORTNAME);
    }

    /**
     * @return the current funnel name
     */
    public Optional<String> getFunnelName() {
        return getGenericProperty(FUNNEL_NAME_SHORTNAME);
    }

    /**
     * @return the current funnel version
     */
    public Optional<String> getFunnelVersion() {
        return getGenericProperty(FUNNEL_VERS_SHORTNAME);
    }

    /**
     * @return the application session correlation id
     */
    public Optional<String> getSessionCorrelationId() {
        return getGenericProperty(SESSION_CID_SHORTNAME);
    }

    /**
     * @return the application request ip addresses
     */
    public Optional<String> getIpAddress() {
        return getGenericProperty(IP_ADDRESS_SHORTNAME);
    }

    /**
     * @return the first application request ip address
     */
    public Optional<String> getFirstIpAddress() {
        List<String> values = Lists.newArrayList(SPLITTER.split(getIpAddress().or("")));
        if ( ! values.isEmpty()) {
            return Optional.fromNullable(values.get(0));
        }
        return Optional.absent();
    }

    /**
     * Return the context's sequence number
     *
     * @return the sequence number
     */
    public int getSequence() {
        Optional<Serializable> optional = getGenericProperty(SEQ_INC_SHORTNAME);
        if (optional.isPresent()) {
            return Integer.valueOf(String.valueOf(optional.get()));
        }
        return 0;
    }

    /**
     * @return the tags
     */
    public Set<String> getTags() {
        Optional<String> value = getGenericProperty(TAGS_SHORTNAME);
        return Tags.valueOf(value.or(""));
    }

    /**
     * Childify this context, the sequence will be incremented
     *
     * @return a new child context
     */
    public Context.Builder child() {
        return new Builder(this).incrementSequence();
    }

    /**
     * @return the context as a map
     */
    public Map<String, String> asMap() {
        return this.asMap(Maps.<String, String>newHashMap());
    }

    /**
     * @return the context as a map
     */
    public Map<String, String> asMap(final Map<String, String> origMap) {
        final Map<String, String> map = Maps.newHashMap(origMap);
        for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
            map.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return map;
    }

    /**
     * return as a metadata map
     */
    public Map<String, ?> asMetaDataMap() {
        final Context that = this;
        return new HashMap<String, Object>() {{
            this.put(METANAME, that);
        }};
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Context other = (Context) obj;
        return Objects.equal(this.properties, other.properties);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("properties", properties).toString();
    }


    public static class Builder {

        private final Map<String, Serializable> properties;

        public Builder() {
            this(UUID.randomUUID());
        }

        public Builder(UUID kasperCorrelationId) {
            this(ImmutableMap.<String, Serializable>builder().put(KASPER_CID_SHORTNAME, kasperCorrelationId.toString())
                    .put(SEQ_INC_SHORTNAME, 1)
                    .build());
        }

        public Builder(Context context) {
            this(context.properties);
        }

        private Builder(final Map<String, Serializable> properties) {
            this.properties = Maps.newHashMap(properties);
        }

        public Builder with(final String key, final Serializable value) {
            checkNotNull(key);
            checkNotNull(value);

            if (SEQ_INC_SHORTNAME.equals(key) || KASPER_CID_SHORTNAME.equals(key)) {
                // this properties are not overridable
                return this;
            }

            properties.put(key, value);
            return this;
        }

        public Builder withSecurityToken(final String token) {
            return with(SECURITY_TOKEN_SHORTNAME, token);
        }

        public Builder withApplicationId(final String applicationId) {
            return with(APPLICATION_ID_SHORTNAME, applicationId);
        }

        public Builder withUserID(final ID userID) {
            return with(USER_ID_SHORTNAME, userID);
        }

        @Deprecated
        public Builder withUserId(final String userId) {
            return with(UID_SHORTNAME, userId);
        }

        public Builder withUserLang(final String userLang) {
            return with(ULANG_SHORTNAME, userLang);
        }

        public Builder withUserCountry(final String userCountry) {
            return with(UCOUNTRY_SHORTNAME, userCountry);
        }

        public Builder withTags(final Set<String> tags) {
            checkNotNull(tags);
            return with(TAGS_SHORTNAME, Tags.toString(tags));
        }

        public Builder addTags(final Set<String> tags) {
            checkNotNull(tags);

            if (tags.isEmpty()) {
                return this;
            }

            Set<String> collection = Sets.newHashSet(tags);
            Serializable value = properties.get(TAGS_SHORTNAME);

            if (value != null) {
                collection.addAll(Tags.valueOf(String.valueOf(value)));
            }

            return with(TAGS_SHORTNAME, Tags.toString(collection));
        }

        public Builder withIpAddress(final String ipAddress) {
            checkNotNull(ipAddress);
            return withIpAddresses(Lists.newArrayList(SPLITTER.split(ipAddress)));
        }

        public Builder withIpAddresses(final List<String> ipAddresses) {
            checkNotNull(ipAddresses);
            return with(IP_ADDRESS_SHORTNAME, JOINER.join(ipAddresses));
        }

        public Builder withRequestCorrelationId(final String requestCorrelationId) {
            return with(REQUEST_CID_SHORTNAME, requestCorrelationId);
        }

        public Builder withSessionCorrelationId(final String sessionCorrelationId) {
            return with(SESSION_CID_SHORTNAME, sessionCorrelationId);
        }

        public Builder withFunnelCorrelationId(final String funnelCorrelationId) {
            return with(FUNNEL_CID_SHORTNAME, funnelCorrelationId);
        }

        public Builder withFunnelVersion(final String funnelVersion) {
            return with(FUNNEL_VERS_SHORTNAME, funnelVersion);
        }

        public Builder withFunnelName(final String funnelName) {
            return with(FUNNEL_NAME_SHORTNAME, funnelName);
        }

        public Context build() {
            return new Context(properties);
        }

        public Builder incrementSequence() {
            this.properties.put(
                    SEQ_INC_SHORTNAME,
                    Objects.firstNonNull((Integer) this.properties.get(SEQ_INC_SHORTNAME), 1) + 1
            );
            return this;
        }
    }
}
