package com.github.beaver010.cooldownlib;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * The Cooldown class represents a cooldown mechanism that can be applied to a {@link PersistentDataHolder}.
 * It uses a {@link NamespacedKey} to uniquely identify and manage cooldowns within the persistent data container.
 */
public record Cooldown(NamespacedKey key) {

    /**
     * Constructs a new Cooldown instance with the specified {@link NamespacedKey}.
     *
     * @param key The key used to store and manage the cooldown.
     * @throws NullPointerException if the key is null.
     */
    public Cooldown {
        Objects.requireNonNull(key, "NamespacedKey must not be null");
    }

    /**
     * Sets a cooldown on the specified {@link PersistentDataHolder} for the given duration.
     *
     * @param dataHolder The data holder to set the cooldown on.
     * @param duration   The duration of the cooldown.
     * @throws NullPointerException if the dataHolder or duration is null.
     */
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

    /**
     * Checks if a cooldown is set on the specified {@link PersistentDataHolder}.
     *
     * @param dataHolder The data holder to check for a cooldown.
     * @return true if a cooldown is set, false otherwise.
     * @throws NullPointerException if the dataHolder is null.
     */
    public boolean isSet(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        return dataHolder.getPersistentDataContainer().has(this.key, PersistentDataType.LONG);
    }

    /**
     * Gets the remaining time of the cooldown on the specified {@link PersistentDataHolder}.
     *
     * @param dataHolder The data holder to check the remaining cooldown time.
     * @return The remaining cooldown duration, or {@link Duration#ZERO} if no cooldown is set or the cooldown has expired.
     * @throws NullPointerException if the dataHolder is null.
     */
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

    /**
     * Checks if the cooldown on the specified {@link PersistentDataHolder} is expired.
     *
     * @param dataHolder The data holder to check for an expired cooldown.
     * @return true if the cooldown is expired or not set, false otherwise.
     * @throws NullPointerException if the dataHolder is null.
     */
    public boolean isExpired(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        return remainingTime(dataHolder).isZero();
    }

    /**
     * Removes the cooldown from the specified {@link PersistentDataHolder}.
     *
     * @param dataHolder The data holder to remove the cooldown from.
     * @return true if the cooldown was removed, false if no cooldown was set.
     * @throws NullPointerException if the dataHolder is null.
     */
    public boolean remove(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        if (isSet(dataHolder)) {
            dataHolder.getPersistentDataContainer().remove(this.key);
            return true;
        }
        return false;
    }

    /**
     * Removes the cooldown from the specified {@link PersistentDataHolder} if it has expired.
     *
     * @param dataHolder The data holder to remove the cooldown from if it has expired.
     * @return true if the cooldown was removed, false if the cooldown was not set or has not expired.
     * @throws NullPointerException if the dataHolder is null.
     */
    public boolean removeIfExpired(final PersistentDataHolder dataHolder) {
        Objects.requireNonNull(dataHolder, "PersistentDataHolder must not be null");

        if (isExpired(dataHolder)) {
            dataHolder.getPersistentDataContainer().remove(this.key);
            return true;
        }
        return false;
    }
}
