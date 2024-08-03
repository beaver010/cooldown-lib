package com.github.beaver010.cooldownlib;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record Cooldown(NamespacedKey key) {

    public Cooldown {
        Objects.requireNonNull(key, "NamespacedKey must not be null");
    }

    public void set(final PersistentDataHolder dataHolder, final Duration duration) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");
        Objects.requireNonNull(duration, "Duration must not be null");

        final Instant expiration = Instant.now().plus(duration);

        dataHolder.getPersistentDataContainer().set(
            this.key,
            PersistentDataType.LONG,
            expiration.getEpochSecond()
        );
    }

    public boolean isSet(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        return dataHolder.getPersistentDataContainer().has(this.key, PersistentDataType.LONG);
    }

    public Duration remainingTime(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        final @Nullable Long expirationEpochSecond = dataHolder.getPersistentDataContainer().get(
            this.key,
            PersistentDataType.LONG
        );

        if (expirationEpochSecond == null) {
            return Duration.ZERO;
        }

        final Instant now = Instant.now();
        final Instant expiration = Instant.ofEpochSecond(expirationEpochSecond);

        if (expiration.isAfter(now)) {
            return Duration.between(now, expiration);
        } else {
            return Duration.ZERO;
        }
    }

    public boolean remove(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        if (isSet(dataHolder)) {
            dataHolder.getPersistentDataContainer().remove(this.key);
            return true;
        }
        return false;
    }
}
