package com.tictactoe.backend.Entity;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "spring_session_attributes", schema = "public", catalog = "tictactoe")
@IdClass(SpringSessionAttributesEntityPK.class)
public class SpringSessionAttributesEntity {
    private String sessionPrimaryId;
    private String attributeName;
    private byte[] attributeBytes;

    @Id
    @Column(name = "session_primary_id")
    public String getSessionPrimaryId() {
        return sessionPrimaryId;
    }

    public void setSessionPrimaryId(String sessionPrimaryId) {
        this.sessionPrimaryId = sessionPrimaryId;
    }

    @Id
    @Column(name = "attribute_name")
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Basic
    @Column(name = "attribute_bytes")
    public byte[] getAttributeBytes() {
        return attributeBytes;
    }

    public void setAttributeBytes(byte[] attributeBytes) {
        this.attributeBytes = attributeBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpringSessionAttributesEntity that = (SpringSessionAttributesEntity) o;

        if (sessionPrimaryId != null ? !sessionPrimaryId.equals(that.sessionPrimaryId) : that.sessionPrimaryId != null)
            return false;
        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (!Arrays.equals(attributeBytes, that.attributeBytes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionPrimaryId != null ? sessionPrimaryId.hashCode() : 0;
        result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(attributeBytes);
        return result;
    }
}
