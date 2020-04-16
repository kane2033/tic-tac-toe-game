package com.tictactoe.backend.Entity;

import javax.persistence.*;

@Entity
@Table(name = "spring_session", schema = "public", catalog = "tictactoe")
public class SpringSessionEntity {
    private String primaryId;
    private String sessionId;
    private long creationTime;
    private long lastAccessTime;
    private int maxInactiveInterval;
    private long expiryTime;
    private String principalName;

    @Id
    @Column(name = "primary_id")
    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    @Basic
    @Column(name = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Basic
    @Column(name = "creation_time")
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Basic
    @Column(name = "last_access_time")
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Basic
    @Column(name = "max_inactive_interval")
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Basic
    @Column(name = "expiry_time")
    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Basic
    @Column(name = "principal_name")
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpringSessionEntity that = (SpringSessionEntity) o;

        if (creationTime != that.creationTime) return false;
        if (lastAccessTime != that.lastAccessTime) return false;
        if (maxInactiveInterval != that.maxInactiveInterval) return false;
        if (expiryTime != that.expiryTime) return false;
        if (primaryId != null ? !primaryId.equals(that.primaryId) : that.primaryId != null) return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        if (principalName != null ? !principalName.equals(that.principalName) : that.principalName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = primaryId != null ? primaryId.hashCode() : 0;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        result = 31 * result + (int) (lastAccessTime ^ (lastAccessTime >>> 32));
        result = 31 * result + maxInactiveInterval;
        result = 31 * result + (int) (expiryTime ^ (expiryTime >>> 32));
        result = 31 * result + (principalName != null ? principalName.hashCode() : 0);
        return result;
    }
}
