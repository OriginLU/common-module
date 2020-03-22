package com.charleslu.tenancy.multi.cache;

import com.charleslu.tenancy.multi.util.ThreadTenantUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用于多租户的redis 键序列化
 *
 * @author ziyan
 * @email zhengmengyan@charleslucar.com
 * @date 2018年12月27日
 */
public class TenantStringRedisSerializer implements RedisSerializer<String> {

    private final Charset charset;

    private static final String TENANT_KEY = "tenant(%s)->%s";

    private static final String NULL = "null";

    private static final Pattern PATTERN = Pattern.compile("tenant\\(.+\\)->");

    public TenantStringRedisSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public TenantStringRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        String str = null;
        if (bytes != null) {
            str = new String(bytes, charset);
            str = PATTERN.matcher(str).replaceFirst("");
            if (StringUtils.equals(str, NULL)) {
                str = null;
            }
        }
        return str;
    }

    @Override
    public byte[] serialize(String string) {
        String tenant = Optional.ofNullable(ThreadTenantUtil.getTenant()).orElse("system");
        return String.format(TENANT_KEY, tenant, string == null ? NULL : string).getBytes(charset);
    }
}
